package com.zhb.wms2.module.base.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNodeConfig;
import cn.hutool.core.lang.tree.TreeUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhb.wms2.common.exception.BaseException;
import com.zhb.wms2.module.base.mapper.ProductCategoryMapper;
import com.zhb.wms2.module.base.model.dto.ProductCategorySortDTO;
import com.zhb.wms2.module.base.model.entity.ProductCategory;
import com.zhb.wms2.module.base.model.vo.ProductCategoryTreeVO;
import com.zhb.wms2.module.base.service.ProductCategoryService;
import com.zhb.wms2.module.base.service.support.BaseDictMapStore;
import com.zhb.wms2.module.product.mapper.ProductMapper;
import com.zhb.wms2.module.product.model.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @Author zhb
 * @Description
 * @Date 2026/3/17 19:02
 */
@Service
@RequiredArgsConstructor
public class ProductCategoryServiceImpl extends ServiceImpl<ProductCategoryMapper, ProductCategory> implements ProductCategoryService {

    private final ProductMapper productMapper;
    private final BaseDictMapStore baseDictMapStore;

    @Override
    public void saveChecked(ProductCategory category) {
        prepareCategory(category, null);
        validateNameUnique(category.getName(), null);
        if (!super.save(category)) {
            throw new BaseException("商品分类新增失败");
        }
        baseDictMapStore.clearProductCategoryMap();
    }

    @Override
    public List<ProductCategoryTreeVO> tree() {
        List<ProductCategory> categoryList = list(new LambdaQueryWrapper<ProductCategory>()
                .orderByAsc(ProductCategory::getSortOrder)
                .orderByAsc(ProductCategory::getId));
        if (categoryList.isEmpty()){
            return List.of();
        }
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
        ProductCategory currentCategory = getById(category.getId());
        if (currentCategory == null) {
            throw new BaseException("商品分类不存在");
        }
        prepareCategory(category, currentCategory);
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
        long count = productMapper.selectCount(new LambdaQueryWrapper<Product>().eq(Product::getCategoryId, id));
        if (count > 0) {
            throw new BaseException("该分类已被商品使用，无法删除");
        }
        if (!removeById(id)) {
            throw new BaseException("商品分类不存在");
        }
        baseDictMapStore.clearProductCategoryMap();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sortSameLevel(ProductCategorySortDTO dto) {
        Long parentId = dto.getParentId();
        List<Long> categoryIdList = dto.getCategoryIdList();
        if (CollUtil.isEmpty(categoryIdList)) {
            throw new BaseException("分类ID列表不能为空");
        }
        if (new HashSet<>(categoryIdList).size() != categoryIdList.size()) {
            throw new BaseException("分类ID不能重复");
        }
        if (parentId > 0 && getById(parentId) == null) {
            throw new BaseException("上级分类不存在");
        }

        List<ProductCategory> siblingList = list(new LambdaQueryWrapper<ProductCategory>()
                .eq(ProductCategory::getParentId, parentId)
                .select(ProductCategory::getId));
        if (siblingList.size() != categoryIdList.size()) {
            throw new BaseException("请提交同级全部分类进行排序");
        }

        Set<Long> siblingIdSet = siblingList.stream()
                .map(ProductCategory::getId)
                .collect(Collectors.toSet());
        if (!siblingIdSet.equals(new HashSet<>(categoryIdList))) {
            throw new BaseException("存在不属于当前同级的分类");
        }

        List<ProductCategory> updateList = IntStream.range(0, categoryIdList.size())
                .mapToObj(index -> {
                    ProductCategory category = new ProductCategory();
                    category.setId(categoryIdList.get(index));
                    category.setSortOrder(index + 1);
                    return category;
                })
                .toList();
        if (!updateBatchById(updateList)) {
            throw new BaseException("商品分类排序失败");
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

    private void prepareCategory(ProductCategory category, ProductCategory currentCategory) {
        Long parentId = category.getParentId();
        if (parentId == null) {
            parentId = 0L;
        }
        if (currentCategory != null && Objects.equals(parentId, currentCategory.getId())) {
            throw new BaseException("上级分类不能是自己");
        }

        ProductCategory parentCategory = null;
        if (parentId > 0) {
            parentCategory = getById(parentId);
            if (parentCategory == null) {
                throw new BaseException("上级分类不存在");
            }
            if (currentCategory != null) {
                validateNoCycle(currentCategory.getId(), parentCategory);
            }
        }

        category.setParentId(parentId);
        // 层级由服务端根据父节点统一计算，不信任前端传入值。
        category.setLevel(parentCategory == null ? 1 : parentCategory.getLevel() + 1);
    }

    private void validateNoCycle(Long currentId, ProductCategory parentCategory) {
        ProductCategory currentParent = parentCategory;
        // 沿父链向上检查，避免把当前节点挂到自己的子树下面。
        while (currentParent != null && currentParent.getId() != null && currentParent.getId() > 0) {
            if (Objects.equals(currentParent.getId(), currentId)) {
                throw new BaseException("上级分类不能是当前分类或其子分类");
            }
            Long nextParentId = currentParent.getParentId();
            if (nextParentId == null || nextParentId == 0) {
                return;
            }
            currentParent = getById(nextParentId);
            if (currentParent == null) {
                throw new BaseException("分类层级数据异常");
            }
        }
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
