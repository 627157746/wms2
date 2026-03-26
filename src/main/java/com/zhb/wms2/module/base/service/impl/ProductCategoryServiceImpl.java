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
 * ProductCategoryServiceImpl 服务实现
 *
 * @author zhb
 * @since 2026/3/26
 */
@Service
@RequiredArgsConstructor
public class ProductCategoryServiceImpl extends ServiceImpl<ProductCategoryMapper, ProductCategory> implements ProductCategoryService {

    private final ProductMapper productMapper;
    private final BaseDictMapStore baseDictMapStore;

    /**
     * 新增商品分类，并根据父节点自动计算层级。
     */
    @Override
    public void saveChecked(ProductCategory category) {
        // 新增时由服务端统一补齐父节点和层级信息。
        prepareCategory(category, null);
        validateNameUnique(category.getName(), null);
        if (!super.save(category)) {
            throw new BaseException("商品分类新增失败");
        }
        // 分类树会被商品模块频繁读取，保存后立即清缓存。
        baseDictMapStore.clearProductCategoryMap();
    }

    /**
     * 查询分类树结构，供前端树形组件使用。
     */
    @Override
    public List<ProductCategoryTreeVO> tree() {
        List<ProductCategory> categoryList = list(new LambdaQueryWrapper<ProductCategory>()
                .orderByAsc(ProductCategory::getSortOrder)
                .orderByAsc(ProductCategory::getId));
        if (categoryList.isEmpty()){
            return List.of();
        }
        // 树组件按 sortOrder 渲染，服务端统一输出树结构避免前端重复组装。
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

    /**
     * 修改商品分类，并重新校验父子关系与层级。
     */
    @Override
    public void updateByIdChecked(ProductCategory category) {
        ProductCategory currentCategory = getById(category.getId());
        if (currentCategory == null) {
            throw new BaseException("商品分类不存在");
        }
        // 修改时重新校验父子关系，防止形成环。
        prepareCategory(category, currentCategory);
        validateNameUnique(category.getName(), category.getId());
        if (!updateById(category)) {
            throw new BaseException("商品分类不存在");
        }
        // 修改后清理分类缓存，保证树和商品详情展示一致。
        baseDictMapStore.clearProductCategoryMap();
    }

    /**
     * 删除分类前校验是否存在子分类或商品引用。
     */
    @Override
    public void removeByIdChecked(Long id) {
        // 分类删除先拦截树结构引用，再拦截商品引用。
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
        // 删除后同步失效缓存。
        baseDictMapStore.clearProductCategoryMap();
    }

    /**
     * 对同级分类重新排序。
     */
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

        // 只允许同级全量重排，避免局部提交把排序关系打乱。
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

        // 排序值按前端提交顺序从 1 开始重建。
        List<ProductCategory> updateList = IntStream.range(0, categoryIdList.size())
                .mapToObj(index -> {
                    return new ProductCategory()
                            .setId(categoryIdList.get(index))
                            .setSortOrder(index + 1);
                })
                .toList();
        if (!updateBatchById(updateList)) {
            throw new BaseException("商品分类排序失败");
        }
        // 批量调整后清缓存，保证树形查询立即生效。
        baseDictMapStore.clearProductCategoryMap();
    }

    /**
     * 将 Hutool 树节点转换为项目使用的树形 VO。
     */
    private ProductCategoryTreeVO convertTree(Tree<Long> tree) {
        List<Tree<Long>> children = tree.getChildren();
        // 递归转换为项目自己的 VO，避免控制层直接暴露第三方 Tree 结构。
        return new ProductCategoryTreeVO()
                .setId(tree.getId())
                .setParentId(tree.getParentId())
                .setName(tree.getName() == null ? null : tree.getName().toString())
                .setSortOrder((Integer) tree.get("sortOrder"))
                .setLevel((Integer) tree.get("level"))
                .setChildren(CollUtil.isEmpty(children)
                        ? CollUtil.newArrayList()
                        : children.stream().map(this::convertTree).toList());
    }

    /**
     * 规范化分类父节点信息，并重新计算层级。
     */
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

        // 层级由服务端根据父节点统一计算，不信任前端传入值。
        category.setParentId(parentId)
                .setLevel(parentCategory == null ? 1 : parentCategory.getLevel() + 1);
    }

    /**
     * 校验上级分类链路中不存在循环引用。
     */
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

    /**
     * 校验商品分类名称唯一。
     */
    private void validateNameUnique(String name, Long excludeId) {
        // 分类名称在当前系统内全局唯一。
        LambdaQueryWrapper<ProductCategory> wrapper = new LambdaQueryWrapper<ProductCategory>()
                .eq(ProductCategory::getName, name)
                .ne(excludeId != null, ProductCategory::getId, excludeId);
        if (count(wrapper) > 0) {
            throw new BaseException("商品分类名称已存在");
        }
    }
}
