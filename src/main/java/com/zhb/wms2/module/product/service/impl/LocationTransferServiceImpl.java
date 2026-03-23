package com.zhb.wms2.module.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhb.wms2.common.exception.BaseException;
import com.zhb.wms2.module.base.model.dto.BaseDictMapDTO;
import com.zhb.wms2.module.base.model.entity.ProductLocation;
import com.zhb.wms2.module.base.service.BaseDictMapService;
import com.zhb.wms2.module.product.mapper.ProductMapper;
import com.zhb.wms2.module.product.mapper.LocationTransferMapper;
import com.zhb.wms2.module.product.model.entity.Product;
import com.zhb.wms2.module.product.model.entity.ProductStockDetail;
import com.zhb.wms2.module.product.model.entity.LocationTransfer;
import com.zhb.wms2.module.product.model.dto.LocationTransferCreateDTO;
import com.zhb.wms2.module.product.model.query.LocationTransferQuery;
import com.zhb.wms2.module.product.model.vo.LocationTransferPageVO;
import com.zhb.wms2.module.product.service.LocationTransferService;
import com.zhb.wms2.module.product.service.ProductStockDetailService;
import com.zhb.wms2.module.product.service.support.ProductStockSummaryService;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LocationTransferServiceImpl extends ServiceImpl<LocationTransferMapper, LocationTransfer>
        implements LocationTransferService {

    private static final Long NO_LOCATION_ID = 0L;
    private static final String NO_LOCATION_CODE = "无货位";

    private final ProductMapper productMapper;
    private final BaseDictMapService baseDictMapService;
    private final ProductStockDetailService productStockDetailService;
    private final ProductStockSummaryService productStockSummaryService;

    @Override
    public IPage<LocationTransferPageVO> pageQuery(LocationTransferQuery query) {
        IPage<LocationTransfer> page = page(new Page<>(query.getCurrent(), query.getSize()), buildWrapper(query));
        List<LocationTransfer> recordList = page.getRecords();
        if (recordList.isEmpty()) {
            return new Page<>(query.getCurrent(), query.getSize());
        }

        Set<Long> productIds = recordList.stream()
                .map(LocationTransfer::getProductId)
                .collect(Collectors.toSet());
        Map<Long, Product> productMap = productIds.isEmpty()
                ? Map.of()
                : productMapper.selectBatchIds(productIds).stream()
                .collect(Collectors.toMap(Product::getId, Function.identity(),
                        (left, right) -> left, LinkedHashMap::new));
        BaseDictMapDTO dictMap = baseDictMapService.getBaseDictMap();
        Map<Long, ProductLocation> locationMap = dictMap.getProductLocationMap() == null
                ? Map.of()
                : dictMap.getProductLocationMap();
        return page.convert(record -> buildPageVO(record, productMap, locationMap));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createTransfer(LocationTransferCreateDTO dto) {
        validateTransfer(dto);

        Map<Long, ProductStockDetail> detailMap = productStockDetailService.list(new LambdaQueryWrapper<ProductStockDetail>()
                        .eq(ProductStockDetail::getProductId, dto.getProductId()))
                .stream()
                .collect(Collectors.toMap(ProductStockDetail::getLocationId, Function.identity(),
                        (left, right) -> left, LinkedHashMap::new));

        ProductStockDetail fromDetail = detailMap.get(dto.getFromLocationId());
        long fromQty = fromDetail == null || fromDetail.getQty() == null ? 0L : fromDetail.getQty();
        if (fromQty < dto.getTransferQty()) {
            throw new BaseException("原货位库存不足，无法转移");
        }

        changeDetailQty(detailMap, dto.getProductId(), dto.getFromLocationId(), -dto.getTransferQty());
        changeDetailQty(detailMap, dto.getProductId(), dto.getToLocationId(), dto.getTransferQty());
        productStockSummaryService.syncByDetailMap(dto.getProductId(), detailMap);

        LocationTransfer transfer = new LocationTransfer();
        transfer.setProductId(dto.getProductId());
        transfer.setFromLocationId(dto.getFromLocationId());
        transfer.setToLocationId(dto.getToLocationId());
        transfer.setTransferQty(dto.getTransferQty());
        transfer.setRemark(dto.getRemark());
        if (!save(transfer)) {
            throw new BaseException("发起转货位失败");
        }
        return transfer.getId();
    }

    private LambdaQueryWrapper<LocationTransfer> buildWrapper(LocationTransferQuery query) {
        return new LambdaQueryWrapper<LocationTransfer>()
                .eq(query.getProductId() != null, LocationTransfer::getProductId, query.getProductId())
                .eq(query.getFromLocationId() != null, LocationTransfer::getFromLocationId, query.getFromLocationId())
                .eq(query.getToLocationId() != null, LocationTransfer::getToLocationId, query.getToLocationId())
                .orderByDesc(LocationTransfer::getId);
    }

    private void validateTransfer(LocationTransferCreateDTO dto) {
        if (Objects.equals(dto.getFromLocationId(), dto.getToLocationId())) {
            throw new BaseException("原货位和转移货位不能相同");
        }
        if (productMapper.selectById(dto.getProductId()) == null) {
            throw new BaseException("商品不存在");
        }
        BaseDictMapDTO dictMap = baseDictMapService.getBaseDictMap();
        Map<Long, ProductLocation> locationMap = dictMap.getProductLocationMap() == null
                ? Map.of()
                : dictMap.getProductLocationMap();
        validateLocationExists(dto.getFromLocationId(), "原货位不存在", locationMap);
        validateLocationExists(dto.getToLocationId(), "转移货位不存在", locationMap);
    }

    private void validateLocationExists(Long locationId, String message, Map<Long, ProductLocation> locationMap) {
        if (Objects.equals(locationId, NO_LOCATION_ID)) {
            return;
        }
        if (!locationMap.containsKey(locationId)) {
            throw new BaseException(message);
        }
    }

    private LocationTransferPageVO buildPageVO(LocationTransfer record,
                                               Map<Long, Product> productMap,
                                               Map<Long, ProductLocation> locationMap) {
        LocationTransferPageVO vo = new LocationTransferPageVO();
        vo.setId(record.getId());
        vo.setProductId(record.getProductId());
        vo.setFromLocationId(record.getFromLocationId());
        vo.setToLocationId(record.getToLocationId());
        vo.setTransferQty(record.getTransferQty());
        vo.setRemark(record.getRemark());
        vo.setCreateTime(record.getCreateTime());
        vo.setUpdateTime(record.getUpdateTime());
        vo.setCreateBy(record.getCreateBy());
        vo.setUpdateBy(record.getUpdateBy());

        Product product = productMap.get(record.getProductId());
        vo.setProductName(product == null ? null : product.getName());
        vo.setProductCode(product == null ? null : product.getCode());
        vo.setFromLocationCode(buildLocationCode(record.getFromLocationId(), locationMap));
        vo.setToLocationCode(buildLocationCode(record.getToLocationId(), locationMap));
        return vo;
    }

    private String buildLocationCode(Long locationId, Map<Long, ProductLocation> locationMap) {
        if (Objects.equals(locationId, NO_LOCATION_ID)) {
            return NO_LOCATION_CODE;
        }
        ProductLocation location = locationMap.get(locationId);
        return location == null ? null : location.getCode();
    }

    private void changeDetailQty(Map<Long, ProductStockDetail> detailMap, Long productId, Long locationId, Long delta) {
        if (delta == 0) {
            return;
        }

        ProductStockDetail detail = detailMap.get(locationId);
        long currentQty = detail == null || detail.getQty() == null ? 0L : detail.getQty();
        long targetQty = currentQty + delta;
        if (targetQty < 0) {
            throw new BaseException("库存明细数量异常，无法完成转货位");
        }
        if (targetQty == 0) {
            productStockDetailService.removeById(detail.getId());
            detailMap.remove(locationId);
            return;
        }
        if (detail == null) {
            ProductStockDetail productStockDetail = new ProductStockDetail();
            productStockDetail.setProductId(productId);
            productStockDetail.setLocationId(locationId);
            productStockDetail.setQty(targetQty);
            productStockDetailService.save(productStockDetail);
            detailMap.put(locationId, productStockDetail);
            return;
        }
        detail.setQty(targetQty);
        productStockDetailService.updateById(detail);
    }
}
