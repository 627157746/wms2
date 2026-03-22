package com.zhb.wms2.module.inventory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhb.wms2.module.base.model.dto.BaseDictMapDTO;
import com.zhb.wms2.module.base.model.entity.ProductLocation;
import com.zhb.wms2.module.base.model.entity.ProductUnit;
import com.zhb.wms2.module.base.service.BaseDictMapService;
import com.zhb.wms2.module.inventory.model.entity.InventoryDetail;
import com.zhb.wms2.module.inventory.mapper.InventoryMapper;
import com.zhb.wms2.module.inventory.model.entity.Inventory;
import com.zhb.wms2.module.inventory.model.query.InventoryDistributionQuery;
import com.zhb.wms2.module.inventory.model.vo.InventoryDistributionGroupVO;
import com.zhb.wms2.module.inventory.model.vo.InventoryDistributionItemVO;
import com.zhb.wms2.module.inventory.service.InventoryDetailService;
import com.zhb.wms2.module.inventory.service.InventoryService;
import com.zhb.wms2.module.product.mapper.ProductMapper;
import com.zhb.wms2.module.product.model.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
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
public class InventoryServiceImpl extends ServiceImpl<InventoryMapper, Inventory> implements InventoryService {

    private static final Long NO_LOCATION_ID = 0L;
    private static final String NO_LOCATION_CODE = "无货位";

    private final InventoryDetailService inventoryDetailService;
    private final ProductMapper productMapper;
    private final BaseDictMapService baseDictMapService;

    @Override
    public List<InventoryDistributionGroupVO> listDistribution(InventoryDistributionQuery query) {
        LambdaQueryWrapper<InventoryDetail> wrapper = new LambdaQueryWrapper<InventoryDetail>()
                .gt(InventoryDetail::getQty, 0)
                .eq(query.getLocationId() != null, InventoryDetail::getLocationId, query.getLocationId())
                .eq(query.getProductId() != null, InventoryDetail::getProductId, query.getProductId())
                .orderByAsc(InventoryDetail::getProductId)
                .orderByAsc(InventoryDetail::getLocationId)
                .orderByAsc(InventoryDetail::getId);
        List<InventoryDetail> detailList = inventoryDetailService.list(wrapper);
        if (detailList.isEmpty()) {
            return List.of();
        }

        BaseDictMapDTO dictMap = baseDictMapService.getBaseDictMap();
        Map<Long, ProductLocation> locationMap = dictMap.getProductLocationMap() == null
                ? Collections.emptyMap()
                : dictMap.getProductLocationMap();
        Map<Long, ProductUnit> unitMap = dictMap.getProductUnitMap() == null
                ? Collections.emptyMap()
                : dictMap.getProductUnitMap();
        Map<Long, Product> productMap = buildProductMap(detailList.stream()
                .map(InventoryDetail::getProductId)
                .distinct()
                .toList());

        List<DistributionRow> rowList = detailList.stream()
                .map(detail -> buildDistributionRow(detail, productMap, locationMap, unitMap))
                .filter(Objects::nonNull)
                .sorted(buildRowComparator())
                .toList();
        if (rowList.isEmpty()) {
            return List.of();
        }

        return buildLocationGroupList(rowList);
    }

    private Map<Long, Product> buildProductMap(Collection<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return productMapper.selectBatchIds(productIds).stream()
                .collect(LinkedHashMap::new, (map, product) -> map.put(product.getId(), product), Map::putAll);
    }

    private DistributionRow buildDistributionRow(InventoryDetail detail,
                                                 Map<Long, Product> productMap,
                                                 Map<Long, ProductLocation> locationMap,
                                                 Map<Long, ProductUnit> unitMap) {
        Product product = productMap.get(detail.getProductId());
        if (product == null) {
            return null;
        }

        Long locationId = detail.getLocationId();
        String locationCode = buildLocationCode(locationId, locationMap);
        Integer locationSortOrder = buildLocationSortOrder(locationId, locationMap);
        ProductUnit productUnit = unitMap.get(product.getUnitId());
        return new DistributionRow(
                product.getId(),
                product.getCode(),
                product.getName(),
                product.getUnitId(),
                productUnit == null ? null : productUnit.getName(),
                locationId,
                locationCode,
                locationSortOrder,
                detail.getQty()
        );
    }

    private Comparator<DistributionRow> buildRowComparator() {
        return Comparator
                .comparing(DistributionRow::getLocationSortOrder, Comparator.nullsLast(Integer::compareTo))
                .thenComparing(DistributionRow::getLocationCode, Comparator.nullsLast(String::compareToIgnoreCase))
                .thenComparing(DistributionRow::getLocationId, Comparator.nullsLast(Long::compareTo))
                .thenComparing(DistributionRow::getProductCode, Comparator.nullsLast(String::compareToIgnoreCase))
                .thenComparing(DistributionRow::getProductId, Comparator.nullsLast(Long::compareTo));
    }

    private List<InventoryDistributionGroupVO> buildLocationGroupList(List<DistributionRow> rowList) {
        Map<Long, InventoryDistributionGroupVO> groupMap = new LinkedHashMap<>();
        for (DistributionRow row : rowList) {
            InventoryDistributionGroupVO group = groupMap.computeIfAbsent(row.getLocationId(), locationId -> {
                InventoryDistributionGroupVO vo = new InventoryDistributionGroupVO();
                vo.setLocationId(locationId);
                vo.setLocationCode(row.getLocationCode());
                vo.setTotalQty(0L);
                vo.setItemList(new ArrayList<>());
                return vo;
            });
            group.getItemList().add(buildItem(row));
            group.setTotalQty(group.getTotalQty() + row.getQty());
        }
        return new ArrayList<>(groupMap.values());
    }

    private InventoryDistributionItemVO buildItem(DistributionRow row) {
        InventoryDistributionItemVO item = new InventoryDistributionItemVO();
        item.setProductId(row.getProductId());
        item.setProductCode(row.getProductCode());
        item.setProductName(row.getProductName());
        item.setUnitId(row.getUnitId());
        item.setUnitName(row.getUnitName());
        item.setQty(row.getQty());
        return item;
    }

    private String buildLocationCode(Long locationId, Map<Long, ProductLocation> locationMap) {
        if (Objects.equals(locationId, NO_LOCATION_ID)) {
            return NO_LOCATION_CODE;
        }
        ProductLocation location = locationMap.get(locationId);
        return location == null ? null : location.getCode();
    }

    private Integer buildLocationSortOrder(Long locationId, Map<Long, ProductLocation> locationMap) {
        if (Objects.equals(locationId, NO_LOCATION_ID)) {
            return Integer.MAX_VALUE;
        }
        ProductLocation location = locationMap.get(locationId);
        return location == null ? null : location.getSortOrder();
    }

    private static class DistributionRow {

        private final Long productId;
        private final String productCode;
        private final String productName;
        private final Long unitId;
        private final String unitName;
        private final Long locationId;
        private final String locationCode;
        private final Integer locationSortOrder;
        private final Long qty;

        private DistributionRow(Long productId, String productCode, String productName,
                                Long unitId, String unitName, Long locationId,
                                String locationCode, Integer locationSortOrder, Long qty) {
            this.productId = productId;
            this.productCode = productCode;
            this.productName = productName;
            this.unitId = unitId;
            this.unitName = unitName;
            this.locationId = locationId;
            this.locationCode = locationCode;
            this.locationSortOrder = locationSortOrder;
            this.qty = qty;
        }

        public Long getProductId() {
            return productId;
        }

        public String getProductCode() {
            return productCode;
        }

        public String getProductName() {
            return productName;
        }

        public Long getUnitId() {
            return unitId;
        }

        public String getUnitName() {
            return unitName;
        }

        public Long getLocationId() {
            return locationId;
        }

        public String getLocationCode() {
            return locationCode;
        }

        public Integer getLocationSortOrder() {
            return locationSortOrder;
        }

        public Long getQty() {
            return qty;
        }
    }

}
