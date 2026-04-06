package com.zhb.wms2.module.base.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhb.wms2.common.exception.BaseException;
import com.zhb.wms2.module.base.mapper.ProductUnitMapper;
import com.zhb.wms2.module.base.model.dto.BaseSortUpdateDTO;
import com.zhb.wms2.module.base.model.entity.ProductUnit;
import com.zhb.wms2.module.base.model.query.ProductUnitQuery;
import com.zhb.wms2.module.base.service.ProductUnitService;
import com.zhb.wms2.module.base.service.support.BaseDictMapStore;
import com.zhb.wms2.module.product.mapper.ProductMapper;
import com.zhb.wms2.module.product.model.entity.Product;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ProductUnitServiceImpl 服务实现
 *
 * @author zhb
 * @since 2026/3/26
 */
@Service
@RequiredArgsConstructor
public class ProductUnitServiceImpl extends ServiceImpl<ProductUnitMapper, ProductUnit> implements ProductUnitService {

    private final ProductMapper productMapper;
    private final BaseDictMapStore baseDictMapStore;

    /**
     * 新增商品单位并校验名称唯一。
     */
    @Override
    public void saveChecked(ProductUnit unit) {
        // 商品单位展示面广，新增前先做唯一校验。
        validateNameUnique(unit.getName(), null);
        if (!super.save(unit)) {
            throw new BaseException("商品单位新增失败");
        }
        applyDefaultSortOrder(unit.getId());
        // 单位字典变更后清缓存，保证商品详情展示一致。
        baseDictMapStore.clearProductUnitMap();
    }

    /**
     * 分页查询商品单位。
     */
    @Override
    public IPage<ProductUnit> pageQuery(ProductUnitQuery query) {
        // 商品单位分页只按名称过滤。
        LambdaQueryWrapper<ProductUnit> wrapper = buildWrapper(query);
        return page(new Page<>(query.getCurrent(), query.getSize()), wrapper);
    }

    /**
     * 查询全部商品单位。
     */
    @Override
    public List<ProductUnit> listAll() {
        // 下拉场景直接返回全量单位。
        return list(new LambdaQueryWrapper<ProductUnit>()
                .orderByDesc(ProductUnit::getSortOrder)
                .orderByDesc(ProductUnit::getId));
    }

    /**
     * 修改商品单位并校验名称唯一。
     */
    @Override
    public void updateByIdChecked(ProductUnit unit) {
        if (getById(unit.getId()) == null) {
            throw new BaseException("商品单位不存在");
        }
        // 修改时排除自身后再校验名称唯一。
        validateNameUnique(unit.getName(), unit.getId());
        if (!updateById(unit)) {
            throw new BaseException("商品单位不存在");
        }
        // 修改后清缓存，避免商品页继续展示旧单位名。
        baseDictMapStore.clearProductUnitMap();
    }

    /**
     * 批量修改商品单位排序并清理字典缓存。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSortOrderBatch(List<BaseSortUpdateDTO> dtoList) {
        if (CollUtil.isEmpty(dtoList)) {
            throw new BaseException("排序列表不能为空");
        }
        Set<Long> idSet = new HashSet<>();
        List<ProductUnit> updateList = dtoList.stream().map(dto -> {
            if (!idSet.add(dto.getId())) {
                throw new BaseException("商品单位ID不能重复");
            }
            ProductUnit unit = new ProductUnit();
            unit.setId(dto.getId());
            unit.setSortOrder(dto.getSortOrder());
            return unit;
        }).toList();
        long count = count(new LambdaQueryWrapper<ProductUnit>().in(ProductUnit::getId, idSet));
        if (count != idSet.size()) {
            throw new BaseException("存在不存在的商品单位");
        }
        if (!updateBatchById(updateList)) {
            throw new BaseException("商品单位排序修改失败");
        }
        baseDictMapStore.clearProductUnitMap();
    }

    /**
     * 删除商品单位前校验是否被商品引用。
     */
    @Override
    public void removeByIdChecked(Long id) {
        // 单位一旦被商品引用，就不能直接删除。
        long count = productMapper.selectCount(new LambdaQueryWrapper<Product>().eq(Product::getUnitId, id));
        if (count > 0) {
            throw new BaseException("该单位已被商品使用，无法删除");
        }
        if (!removeById(id)) {
            throw new BaseException("商品单位不存在");
        }
        // 删除后同步失效缓存。
        baseDictMapStore.clearProductUnitMap();
    }

    /**
     * 构建商品单位分页查询条件。
     */
    private LambdaQueryWrapper<ProductUnit> buildWrapper(ProductUnitQuery query) {
        // 单位列表仅支持名称模糊搜索。
        return new LambdaQueryWrapper<ProductUnit>()
                .like(StrUtil.isNotBlank(query.getName()), ProductUnit::getName, query.getName())
                .orderByDesc(ProductUnit::getSortOrder)
                .orderByDesc(ProductUnit::getId);
    }

    /**
     * 校验商品单位名称唯一。
     */
    private void validateNameUnique(String name, Long excludeId) {
        // 编辑时排除当前记录，避免自身触发重复校验。
        LambdaQueryWrapper<ProductUnit> wrapper = new LambdaQueryWrapper<ProductUnit>()
                .eq(ProductUnit::getName, name)
                .ne(excludeId != null, ProductUnit::getId, excludeId);
        if (count(wrapper) > 0) {
            throw new BaseException("商品单位名称已存在");
        }
    }

    /**
     * 新增后按主键回填默认排序。
     */
    private void applyDefaultSortOrder(Long id) {
        ProductUnit updateEntity = new ProductUnit();
        updateEntity.setId(id);
        updateEntity.setSortOrder(Math.toIntExact(id));
        if (!updateById(updateEntity)) {
            throw new BaseException("商品单位默认排序回填失败");
        }
    }
}
