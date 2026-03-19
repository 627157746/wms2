package com.zhb.wms2.module.inventory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhb.wms2.common.exception.BaseException;
import com.zhb.wms2.module.base.model.dto.BaseDictMapDTO;
import com.zhb.wms2.module.base.model.entity.ProductLocation;
import com.zhb.wms2.module.base.service.BaseDictMapService;
import com.zhb.wms2.module.inventory.mapper.InventoryDetailMapper;
import com.zhb.wms2.module.inventory.model.entity.InventoryDetail;
import com.zhb.wms2.module.inventory.model.vo.InventoryDetailVO;
import com.zhb.wms2.module.inventory.service.InventoryDetailService;
import com.zhb.wms2.module.product.mapper.ProductMapper;
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
public class InventoryDetailServiceImpl extends ServiceImpl<InventoryDetailMapper, InventoryDetail>
        implements InventoryDetailService {

    private static final Long NO_LOCATION_ID = 0L;
    private static final String NO_LOCATION_CODE = "无货位";

    private final ProductMapper productMapper;
    private final BaseDictMapService baseDictMapService;

    @Override
    public List<InventoryDetailVO> listByProductId(Long productId) {
        if (productMapper.selectById(productId) == null) {
            throw new BaseException("商品不存在");
        }

        List<InventoryDetail> detailList = list(new LambdaQueryWrapper<InventoryDetail>()
                .eq(InventoryDetail::getProductId, productId)
                .orderByAsc(InventoryDetail::getLocationId)
                .orderByAsc(InventoryDetail::getId));
        if (detailList.isEmpty()) {
            return List.of();
        }

        BaseDictMapDTO dictMap = baseDictMapService.getBaseDictMap();
        Map<Long, ProductLocation> locationMap = dictMap.getProductLocationMap() == null
                ? Map.of()
                : new LinkedHashMap<>(dictMap.getProductLocationMap());

        return detailList.stream().map(detail -> {
            InventoryDetailVO vo = new InventoryDetailVO();
            vo.setId(detail.getId());
            vo.setProductId(detail.getProductId());
            vo.setLocationId(detail.getLocationId());
            vo.setQty(detail.getQty());
            vo.setCreateTime(detail.getCreateTime());
            vo.setUpdateTime(detail.getUpdateTime());
            vo.setCreateBy(detail.getCreateBy());
            vo.setUpdateBy(detail.getUpdateBy());
            vo.setLocation(buildLocation(detail.getLocationId(), locationMap));
            return vo;
        }).toList();
    }

    private ProductLocation buildLocation(Long locationId, Map<Long, ProductLocation> locationMap) {
        if (Objects.equals(locationId, NO_LOCATION_ID)) {
            ProductLocation location = new ProductLocation();
            location.setId(NO_LOCATION_ID);
            location.setCode(NO_LOCATION_CODE);
            return location;
        }
        return locationMap.get(locationId);
    }

}
