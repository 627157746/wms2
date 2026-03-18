package com.zhb.wms2.module.base.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNodeConfig;
import cn.hutool.core.lang.tree.TreeUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhb.wms2.common.exception.BaseException;
import com.zhb.wms2.module.base.mapper.ProductCategoryMapper;
import com.zhb.wms2.module.base.model.entity.ProductCategory;
import com.zhb.wms2.module.base.model.vo.ProductCategoryTreeVO;
import com.zhb.wms2.module.base.service.ProductCategoryService;
import com.zhb.wms2.module.base.service.support.BaseDictMapStore;
import com.zhb.wms2.module.product.model.entity.Product;
import com.zhb.wms2.module.product.service.ProductService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @Author zhb
 * @Description
 * @Date 2026/3/17 19:02
 */
@Service
@RequiredArgsConstructor
public class ProductCategoryServiceImpl extends ServiceImpl<ProductCategoryMapper, ProductCategory> implements ProductCategoryService {

    private final ProductService productService;
    private final BaseDictMapStore baseDictMapStore;

    @Override
    public boolean save(ProductCategory category) {
        validateNameUnique(category.getName(), null);
        boolean saved = super.save(category);
        if (saved) {
            baseDictMapStore.clearProductCategoryMap();
        }
        return saved;
    }

    @Override
    public List<ProductCategoryTreeVO> tree() {
        List<ProductCategory> categoryList = list(new LambdaQueryWrapper<ProductCategory>()
                .orderByAsc(ProductCategory::getSortOrder)
                .orderByAsc(ProductCategory::getId));
        TreeNodeConfig config = new TreeNodeConfig().setWeightKey("sortOrder");
        List<Tree<Long>> treeList = TreeUtil.build(categoryList, 0L, config, (category, tree) -> {
            tree.setId(category.getId());
            tree.setParentId(category.getParentId());
            tree.setName(category.getName());
            tree.setWeight(category.getSortOrder());
            tree.putExtra("level", category.getLevel());
        });
        return treeList.stream().map(this::convertTree).toList();
    }

    @Override
    public void updateByIdChecked(ProductCategory category) {
        if (getById(category.getId()) == null) {
            throw new BaseException("商品分类不存在");
        }
        validateNameUnique(category.getName(), category.getId());
        if (!updateById(category)) {
            throw new BaseException("商品分类不存在");
        }
        baseDictMapStore.clearProductCategoryMap();
    }

    @Override
    public void removeByIdChecked(Long id) {
        long childCount = count(new LambdaQueryWrapper<ProductCategory>().eq(ProductCategory::getParentId, id));
        if (childCount > 0) {
            throw new BaseException("该分类存在子分类，无法删除");
        }
        long count = productService.count(new LambdaQueryWrapper<Product>().eq(Product::getCategoryId, id));
        if (count > 0) {
            throw new BaseException("该分类已被商品使用，无法删除");
        }
        if (!removeById(id)) {
            throw new BaseException("商品分类不存在");
        }
        baseDictMapStore.clearProductCategoryMap();
    }

    private ProductCategoryTreeVO convertTree(Tree<Long> tree) {
        ProductCategoryTreeVO treeVO = new ProductCategoryTreeVO();
        treeVO.setId(tree.getId());
        treeVO.setParentId(tree.getParentId());
        treeVO.setName(tree.getName() == null ? null : tree.getName().toString());
        treeVO.setSortOrder((Integer) tree.get("sortOrder"));
        treeVO.setLevel((Integer) tree.get("level"));
        List<Tree<Long>> children = tree.getChildren();
        treeVO.setChildren(CollUtil.isEmpty(children)
                ? CollUtil.newArrayList()
                : children.stream().map(this::convertTree).toList());
        return treeVO;
    }

    private void validateNameUnique(String name, Long excludeId) {
        LambdaQueryWrapper<ProductCategory> wrapper = new LambdaQueryWrapper<ProductCategory>()
                .eq(ProductCategory::getName, name)
                .ne(excludeId != null, ProductCategory::getId, excludeId);
        if (count(wrapper) > 0) {
            throw new BaseException("商品分类名称已存在");
        }
    }
}
