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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @Author zhb
 * @Description 
 * @Date 2026/3/17 19:07
 */
@Service
@RequiredArgsConstructor
public class ProductStockDetailServiceImpl extends ServiceImpl<ProductStockDetailMapper, ProductStockDetail>
        implements ProductStockDetailService {

    private static final Long NO_LOCATION_ID = 0L;
    private static final String NO_LOCATION_CODE = "无货位";

    private final ProductMapper productMapper;
    private final BaseDictMapService baseDictMapService;

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

        return detailList.stream().map(detail -> {
            ProductStockDetailVO vo = new ProductStockDetailVO();
            vo.setId(detail.getId());
            vo.setProductId(detail.getProductId());
            vo.setLocationId(detail.getLocationId());
            vo.setQty(detail.getQty());
            vo.setCreateTime(detail.getCreateTime());
            vo.setUpdateTime(detail.getUpdateTime());
            vo.setCreateBy(detail.getCreateBy());
            vo.setUpdateBy(detail.getUpdateBy());
            vo.setLocationCode(buildLocationCode(detail.getLocationId(), locationMap));
            return vo;
        }).toList();
    }

    private String buildLocationCode(Long locationId, Map<Long, ProductLocation> locationMap) {
        if (Objects.equals(locationId, NO_LOCATION_ID)) {
            return NO_LOCATION_CODE;
        }
        ProductLocation location = locationMap.get(locationId);
        return location == null ? null : location.getCode();
    }

}
