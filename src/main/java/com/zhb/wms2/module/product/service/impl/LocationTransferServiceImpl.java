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

/**
 * LocationTransferServiceImpl 服务实现
 *
 * @author zhb
 * @since 2026/3/26
 */
@Service
@RequiredArgsConstructor
public class LocationTransferServiceImpl extends ServiceImpl<LocationTransferMapper, LocationTransfer>
        implements LocationTransferService {

    private final ProductMapper productMapper;
    private final BaseDictMapService baseDictMapService;
    private final ProductStockDetailService productStockDetailService;
    private final ProductStockSummaryService productStockSummaryService;

    /**
     * 分页查询转货位记录，并补充商品与货位展示信息。
     */
    @Override
    public IPage<LocationTransferPageVO> pageQuery(LocationTransferQuery query) {
        IPage<LocationTransfer> page = page(new Page<>(query.getCurrent(), query.getSize()), buildWrapper(query));
        List<LocationTransfer> recordList = page.getRecords();
        if (recordList.isEmpty()) {
            return new Page<>(query.getCurrent(), query.getSize());
        }

        // 分页查询统一批量补商品和货位展示信息，避免逐条查字典。
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

    /**
     * 执行转货位并同步商品库存汇总。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createTransfer(LocationTransferCreateDTO dto) {
        validateTransfer(dto);

        // 转货位只操作同一商品的库存明细映射。
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

        // 先扣原货位，再加目标货位，最后统一回写商品库存汇总。
        changeDetailQty(detailMap, dto.getProductId(), dto.getFromLocationId(), -dto.getTransferQty());
        changeDetailQty(detailMap, dto.getProductId(), dto.getToLocationId(), dto.getTransferQty());
        productStockSummaryService.syncByDetailMap(dto.getProductId(), detailMap);

        LocationTransfer transfer = new LocationTransfer()
                .setProductId(dto.getProductId())
                .setFromLocationId(dto.getFromLocationId())
                .setToLocationId(dto.getToLocationId())
                .setTransferQty(dto.getTransferQty())
                .setRemark(dto.getRemark());
        if (!save(transfer)) {
            throw new BaseException("发起转货位失败");
        }
        return transfer.getId();
    }

    /**
     * 构建转货位分页查询条件。
     */
    private LambdaQueryWrapper<LocationTransfer> buildWrapper(LocationTransferQuery query) {
        // 转货位记录只支持按商品和起止货位过滤。
        return new LambdaQueryWrapper<LocationTransfer>()
                .eq(query.getProductId() != null, LocationTransfer::getProductId, query.getProductId())
                .eq(query.getFromLocationId() != null, LocationTransfer::getFromLocationId, query.getFromLocationId())
                .eq(query.getToLocationId() != null, LocationTransfer::getToLocationId, query.getToLocationId())
                .orderByDesc(LocationTransfer::getId);
    }

    /**
     * 校验转货位请求中的商品与货位引用是否合法。
     */
    private void validateTransfer(LocationTransferCreateDTO dto) {
        if (Objects.equals(dto.getFromLocationId(), dto.getToLocationId())) {
            throw new BaseException("原货位和转移货位不能相同");
        }
        // 先校验商品存在，再校验货位字典，避免后续库存调整落到非法引用。
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

    /**
     * 校验货位是否存在。
     */
    private void validateLocationExists(Long locationId, String message, Map<Long, ProductLocation> locationMap) {
        if (!locationMap.containsKey(locationId)) {
            throw new BaseException(message);
        }
    }

    /**
     * 组装转货位分页展示对象。
     */
    private LocationTransferPageVO buildPageVO(LocationTransfer record,
                                               Map<Long, Product> productMap,
                                               Map<Long, ProductLocation> locationMap) {
        LocationTransferPageVO vo = new LocationTransferPageVO();
        vo.setId(record.getId())
                .setProductId(record.getProductId())
                .setFromLocationId(record.getFromLocationId())
                .setToLocationId(record.getToLocationId())
                .setTransferQty(record.getTransferQty())
                .setRemark(record.getRemark());
        vo.setCreateTime(record.getCreateTime())
                .setUpdateTime(record.getUpdateTime())
                .setCreateBy(record.getCreateBy())
                .setUpdateBy(record.getUpdateBy());

        Product product = productMap.get(record.getProductId());
        // 这里仅回填展示字段，不改变转货位原始记录。
        vo.setProductName(product == null ? null : product.getName())
                .setProductCode(product == null ? null : product.getCode())
                .setFromLocationCode(buildLocationCode(record.getFromLocationId(), locationMap))
                .setToLocationCode(buildLocationCode(record.getToLocationId(), locationMap));
        return vo;
    }

    /**
     * 将货位 ID 转成可展示的货位编码。
     */
    private String buildLocationCode(Long locationId, Map<Long, ProductLocation> locationMap) {
        ProductLocation location = locationMap.get(locationId);
        return location == null ? null : location.getCode();
    }

    /**
     * 调整单个商品在指定货位上的库存明细数量。
     */
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
            // 明细归零时直接删记录，避免保留零库存脏数据。
            productStockDetailService.removeByIdChecked(detail.getId());
            detailMap.remove(locationId);
            return;
        }
        if (detail == null) {
            // 目标货位原本没有库存明细时，按新增记录处理。
            ProductStockDetail productStockDetail = new ProductStockDetail()
                    .setProductId(productId)
                    .setLocationId(locationId)
                    .setQty(targetQty);
            productStockDetailService.saveChecked(productStockDetail);
            detailMap.put(locationId, productStockDetail);
            return;
        }
        detail.setQty(targetQty);
        productStockDetailService.updateByIdChecked(detail);
    }
}
