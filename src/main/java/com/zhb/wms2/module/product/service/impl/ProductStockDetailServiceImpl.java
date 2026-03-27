package com.zhb.wms2.module.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhb.wms2.common.exception.BaseException;
import com.zhb.wms2.module.base.model.dto.BaseDictMapDTO;
import com.zhb.wms2.module.base.model.entity.ProductLocation;
import com.zhb.wms2.module.base.service.BaseDictMapService;
import com.zhb.wms2.module.product.mapper.ProductMapper;
import com.zhb.wms2.module.product.mapper.ProductStockDetailMapper;
import com.zhb.wms2.module.product.model.entity.ProductStockDetail;
import com.zhb.wms2.module.product.model.vo.ProductStockDetailVO;
import com.zhb.wms2.module.product.service.ProductStockDetailService;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * ProductStockDetailServiceImpl 服务实现
 *
 * @author zhb
 * @since 2026/3/26
 */
@Service
@RequiredArgsConstructor
public class ProductStockDetailServiceImpl extends ServiceImpl<ProductStockDetailMapper, ProductStockDetail>
        implements ProductStockDetailService {

    private final ProductMapper productMapper;
    private final BaseDictMapService baseDictMapService;

    /**
     * 新增库存明细。
     */
    @Override
    public void saveChecked(ProductStockDetail detail) {
        // 库存明细由库存业务统一驱动创建，这里只负责失败兜底。
        validateLocationId(detail == null ? null : detail.getLocationId());
        if (!super.save(detail)) {
            throw new BaseException("库存明细新增失败");
        }
    }

    /**
     * 修改库存明细。
     */
    @Override
    public void updateByIdChecked(ProductStockDetail detail) {
        // 库存调整统一落在服务层，避免外部直接更新明细表。
        validateLocationId(detail == null ? null : detail.getLocationId());
        if (!super.updateById(detail)) {
            throw new BaseException("库存明细修改失败");
        }
    }

    /**
     * 查询商品的库存明细，并补充货位编码。
     */
    @Override
    public List<ProductStockDetailVO> listByProductId(Long productId) {
        if (productMapper.selectById(productId) == null) {
            throw new BaseException("商品不存在");
        }

        List<ProductStockDetail> detailList = list(new LambdaQueryWrapper<ProductStockDetail>()
                .eq(ProductStockDetail::getProductId, productId)
                .orderByAsc(ProductStockDetail::getLocationId)
                .orderByAsc(ProductStockDetail::getId));
        if (detailList.isEmpty()) {
            return List.of();
        }

        BaseDictMapDTO dictMap = baseDictMapService.getBaseDictMap();
        Map<Long, ProductLocation> locationMap = dictMap.getProductLocationMap() == null
                ? Map.of()
                : new LinkedHashMap<>(dictMap.getProductLocationMap());

        // 详情页只补充货位编码，不在这里重复计算商品汇总库存。
        return detailList.stream().map(detail -> {
            ProductStockDetailVO vo = new ProductStockDetailVO();
            vo.setId(detail.getId())
                    .setProductId(detail.getProductId())
                    .setLocationId(detail.getLocationId())
                    .setQty(detail.getQty());
            vo.setCreateTime(detail.getCreateTime())
                    .setUpdateTime(detail.getUpdateTime())
                    .setCreateBy(detail.getCreateBy())
                    .setUpdateBy(detail.getUpdateBy());
            vo.setLocationCode(buildLocationCode(detail.getLocationId(), locationMap));
            return vo;
        }).toList();
    }

    /**
     * 删除库存明细。
     */
    @Override
    public void removeByIdChecked(Long id) {
        // 删除动作由出库、转货位等业务流程触发，这里只做失败兜底。
        if (!super.removeById(id)) {
            throw new BaseException("库存明细删除失败");
        }
    }

    /**
     * 将货位 ID 转成可展示的货位编码。
     */
    private String buildLocationCode(Long locationId, Map<Long, ProductLocation> locationMap) {
        // 兼容历史脏数据，找不到货位时保留空值而不是直接报错。
        ProductLocation location = locationMap.get(locationId);
        return location == null ? null : location.getCode();
    }

    /**
     * 校验库存明细中的货位是否合法。
     */
    private void validateLocationId(Long locationId) {
        if (locationId == null || locationId < 1) {
            throw new BaseException("库存明细货位不能为空");
        }
    }

}
