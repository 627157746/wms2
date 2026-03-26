package com.zhb.wms2.module.product.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhb.wms2.common.exception.BaseException;
import com.zhb.wms2.module.base.model.dto.BaseDictMapDTO;
import com.zhb.wms2.module.base.model.entity.ProductCategory;
import com.zhb.wms2.module.base.model.entity.ProductLocation;
import com.zhb.wms2.module.base.model.entity.ProductUnit;
import com.zhb.wms2.module.base.service.BaseDictMapService;
import com.zhb.wms2.module.io.model.entity.IoApplyDetail;
import com.zhb.wms2.module.io.model.entity.IoOrderDetail;
import com.zhb.wms2.module.io.service.IoApplyDetailService;
import com.zhb.wms2.module.io.service.IoOrderDetailService;
import com.zhb.wms2.module.product.mapper.ProductMapper;
import com.zhb.wms2.module.product.model.entity.Product;
import com.zhb.wms2.module.product.model.entity.ProductStockDetail;
import com.zhb.wms2.module.product.model.query.ProductQuery;
import com.zhb.wms2.module.product.model.query.StockDistributionQuery;
import com.zhb.wms2.module.product.model.vo.ProductPageVO;
import com.zhb.wms2.module.product.model.vo.StockDistributionGroupVO;
import com.zhb.wms2.module.product.model.vo.StockDistributionItemVO;
import com.zhb.wms2.module.product.service.ProductService;
import com.zhb.wms2.module.product.service.ProductStockDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author zhb
 * @Description
 * @Date 2026/3/17 19:07
 */
@Service
@RequiredArgsConstructor
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    // 约定 0 表示“无货位”，用于展示未分配到具体货位的库存。
    private static final Long NO_LOCATION_ID = 0L;
    private static final String NO_LOCATION_CODE = "无货位";

    private final BaseDictMapService baseDictMapService;
    private final ProductStockDetailService productStockDetailService;
    private final IoApplyDetailService ioApplyDetailService;
    private final IoOrderDetailService ioOrderDetailService;

    @Override
    public void saveChecked(Product product) {
        normalizeProduct(product);
        validateProduct(product, null, null);
        if (!super.save(product)) {
            throw new BaseException("商品新增失败");
        }
    }

    @Override
    public IPage<ProductPageVO> pageQuery(ProductQuery query) {
        BaseDictMapDTO dictMap = baseDictMapService.getBaseDictMap();
        Map<Long, ProductCategory> categoryMap = dictMap.getProductCategoryMap() == null
                ? Collections.emptyMap()
                : dictMap.getProductCategoryMap();
        IPage<Product> productPage = page(new Page<>(query.getCurrent(), query.getSize()), buildWrapper(query, categoryMap));
        List<Product> productList = productPage.getRecords();
        if (productList.isEmpty()) {
            return new Page<>(query.getCurrent(), query.getSize());
        }
        Map<Long, ProductLocation> locationMap = dictMap.getProductLocationMap() == null
                ? Collections.emptyMap()
                : dictMap.getProductLocationMap();
        Map<Long, ProductUnit> unitMap = dictMap.getProductUnitMap() == null
                ? Collections.emptyMap()
                : dictMap.getProductUnitMap();
        return productPage.convert(product -> buildProductPageVO(product, categoryMap, locationMap, unitMap));
    }

    @Override
    public ProductPageVO getDetailById(Long id) {
        Product product = getById(id);
        if (product == null) {
            throw new BaseException("商品不存在");
        }

        BaseDictMapDTO dictMap = baseDictMapService.getBaseDictMap();
        Map<Long, ProductCategory> categoryMap = dictMap.getProductCategoryMap() == null
                ? Collections.emptyMap()
                : dictMap.getProductCategoryMap();
        Map<Long, ProductLocation> locationMap = dictMap.getProductLocationMap() == null
                ? Collections.emptyMap()
                : dictMap.getProductLocationMap();
        Map<Long, ProductUnit> unitMap = dictMap.getProductUnitMap() == null
                ? Collections.emptyMap()
                : dictMap.getProductUnitMap();
        return buildProductPageVO(product, categoryMap, locationMap, unitMap);
    }

    @Override
    public Map<Long, ProductPageVO> getDetailMapByIds(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyMap();
        }

        List<Product> productList = listByIds(ids);
        if (productList.isEmpty()) {
            return Collections.emptyMap();
        }

        BaseDictMapDTO dictMap = baseDictMapService.getBaseDictMap();
        Map<Long, ProductCategory> categoryMap = dictMap.getProductCategoryMap() == null
                ? Collections.emptyMap()
                : dictMap.getProductCategoryMap();
        Map<Long, ProductLocation> locationMap = dictMap.getProductLocationMap() == null
                ? Collections.emptyMap()
                : dictMap.getProductLocationMap();
        Map<Long, ProductUnit> unitMap = dictMap.getProductUnitMap() == null
                ? Collections.emptyMap()
                : dictMap.getProductUnitMap();
        return productList.stream()
                .collect(Collectors.toMap(Product::getId,
                        product -> buildProductPageVO(product, categoryMap, locationMap, unitMap),
                        (left, right) -> left, LinkedHashMap::new));
    }

    @Override
    public List<StockDistributionGroupVO> listDistribution(StockDistributionQuery query) {
        LambdaQueryWrapper<ProductStockDetail> wrapper = new LambdaQueryWrapper<ProductStockDetail>()
                .gt(ProductStockDetail::getQty, 0)
                .eq(query.getLocationId() != null, ProductStockDetail::getLocationId, query.getLocationId())
                .eq(query.getProductId() != null, ProductStockDetail::getProductId, query.getProductId())
                .orderByAsc(ProductStockDetail::getProductId)
                .orderByAsc(ProductStockDetail::getLocationId)
                .orderByAsc(ProductStockDetail::getId);
        List<ProductStockDetail> detailList = productStockDetailService.list(wrapper);
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
                .map(ProductStockDetail::getProductId)
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

    @Override
    public void removeByIdChecked(Long id) {
        Product product = getById(id);
        if (product == null) {
            throw new BaseException("商品不存在");
        }
        long ioApplyDetailCount = ioApplyDetailService.count(
                new LambdaQueryWrapper<IoApplyDetail>().eq(IoApplyDetail::getProductId, id));
        if (ioApplyDetailCount > 0) {
            throw new BaseException("该商品已被出入库申请使用，无法删除");
        }
        long ioOrderDetailCount = ioOrderDetailService.count(
                new LambdaQueryWrapper<IoOrderDetail>().eq(IoOrderDetail::getProductId, id));
        if (ioOrderDetailCount > 0) {
            throw new BaseException("该商品已被出入库记录使用，无法删除");
        }
        if (product.getTotalStockQty() != null && product.getTotalStockQty() > 0) {
            throw new BaseException("该商品仍有库存，无法删除");
        }
        long stockDetailCount = productStockDetailService.count(
                new LambdaQueryWrapper<ProductStockDetail>().eq(ProductStockDetail::getProductId, id));
        if (stockDetailCount > 0) {
            throw new BaseException("该商品仍有库存明细，无法删除");
        }
        if (!removeById(id)) {
            throw new BaseException("商品不存在");
        }
    }

    @Override
    public void updateByIdChecked(Product product) {
        Product oldProduct = getById(product.getId());
        if (oldProduct == null) {
            throw new BaseException("商品不存在");
        }
        normalizeProduct(product);
        validateProduct(product, product.getId(), oldProduct);
        if (!updateById(product)) {
            throw new BaseException("商品不存在");
        }
    }

    private LambdaQueryWrapper<Product> buildWrapper(ProductQuery query, Map<Long, ProductCategory> categoryMap) {
        List<Long> categoryIdList = buildCategoryIdList(query.getCategoryId(), categoryMap);
        return new LambdaQueryWrapper<Product>()
                .like(StrUtil.isNotBlank(query.getName()), Product::getName, query.getName())
                .like(StrUtil.isNotBlank(query.getCode()), Product::getCode, query.getCode())
                .like(StrUtil.isNotBlank(query.getBarcode()), Product::getBarcode, query.getBarcode())
                .like(StrUtil.isNotBlank(query.getModel()), Product::getModel, query.getModel())
                .in(!categoryIdList.isEmpty(), Product::getCategoryId, categoryIdList)
                .eq(query.getUnitId() != null, Product::getUnitId, query.getUnitId())
                .gt(Boolean.FALSE.equals(query.getIncludeZeroStock()), Product::getTotalStockQty, 0)
                .apply(Boolean.TRUE.equals(query.getOnlyShortageStock()), "min_stock > COALESCE(total_stock_qty, 0)")
                .orderByDesc(Product::getId);
    }

    private List<Long> buildCategoryIdList(Long categoryId, Map<Long, ProductCategory> categoryMap) {
        if (categoryId == null) {
            return List.of();
        }
        if (categoryMap.isEmpty()) {
            return List.of(categoryId);
        }

        Map<Long, List<Long>> childIdsMap = new HashMap<>();
        for (ProductCategory category : categoryMap.values()) {
            if (category == null || category.getId() == null) {
                continue;
            }
            Long parentId = category.getParentId() == null ? 0L : category.getParentId();
            childIdsMap.computeIfAbsent(parentId, key -> new ArrayList<>()).add(category.getId());
        }

        Set<Long> categoryIdSet = new LinkedHashSet<>();
        Deque<Long> waitHandleQueue = new ArrayDeque<>();
        waitHandleQueue.offer(categoryId);
        while (!waitHandleQueue.isEmpty()) {
            Long currentCategoryId = waitHandleQueue.poll();
            if (currentCategoryId == null || !categoryIdSet.add(currentCategoryId)) {
                continue;
            }
            List<Long> childIdList = childIdsMap.get(currentCategoryId);
            if (childIdList == null || childIdList.isEmpty()) {
                continue;
            }
            childIdList.forEach(waitHandleQueue::offer);
        }
        return new ArrayList<>(categoryIdSet);
    }

    private ProductPageVO buildProductPageVO(Product product,
                                             Map<Long, ProductCategory> categoryMap,
                                             Map<Long, ProductLocation> locationMap,
                                             Map<Long, ProductUnit> unitMap) {
        ProductPageVO vo = new ProductPageVO();
        vo.setId(product.getId());
        vo.setName(product.getName());
        vo.setCode(product.getCode());
        vo.setBarcode(product.getBarcode());
        vo.setModel(product.getModel());
        vo.setUnitId(product.getUnitId());
        vo.setCategoryId(product.getCategoryId());
        vo.setMinStock(product.getMinStock());
        vo.setTotalStockQty(product.getTotalStockQty() == null ? 0L : product.getTotalStockQty());
        vo.setLocationIdsStr(product.getLocationIdsStr());
        vo.setLocationCodes(buildLocationCodes(product.getLocationIdsStr(), locationMap));
        ProductUnit unit = unitMap.get(product.getUnitId());
        vo.setProductUnitName(unit == null ? null : unit.getName());
        ProductCategory category = categoryMap.get(product.getCategoryId());
        vo.setProductCategoryName(category == null ? null : category.getName());
        vo.setRemark(product.getRemark());
        vo.setCreateTime(product.getCreateTime());
        vo.setUpdateTime(product.getUpdateTime());
        vo.setCreateBy(product.getCreateBy());
        vo.setUpdateBy(product.getUpdateBy());
        return vo;
    }

    private List<String> buildLocationCodes(String locationIdsStr, Map<Long, ProductLocation> locationMap) {
        if (StrUtil.isBlank(locationIdsStr)) {
            return List.of();
        }
        return StrUtil.splitTrim(locationIdsStr, ',').stream()
                .filter(StrUtil::isNotBlank)
                .map(locationIdStr -> {
                    try {
                        ProductLocation location = locationMap.get(Long.valueOf(locationIdStr));
                        return location == null ? locationIdStr : location.getCode();
                    } catch (NumberFormatException ex) {
                        return locationIdStr;
                    }
                })
                .toList();
    }

    private Map<Long, Product> buildProductMap(Collection<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return baseMapper.selectBatchIds(productIds).stream()
                .collect(LinkedHashMap::new, (map, product) -> map.put(product.getId(), product), Map::putAll);
    }

    private DistributionRow buildDistributionRow(ProductStockDetail detail,
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
                product.getModel(),
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

    private List<StockDistributionGroupVO> buildLocationGroupList(List<DistributionRow> rowList) {
        Map<Long, StockDistributionGroupVO> groupMap = new LinkedHashMap<>();
        for (DistributionRow row : rowList) {
            StockDistributionGroupVO group = groupMap.computeIfAbsent(row.getLocationId(), locationId -> {
                StockDistributionGroupVO vo = new StockDistributionGroupVO();
                vo.setLocationId(locationId);
                vo.setLocationCode(row.getLocationCode());
                vo.setTotalQty(0L);
                vo.setItemList(new ArrayList<>());
                return vo;
            });
            group.getItemList().add(buildDistributionItem(row));
            group.setTotalQty(group.getTotalQty() + row.getQty());
        }
        return new ArrayList<>(groupMap.values());
    }

    private StockDistributionItemVO buildDistributionItem(DistributionRow row) {
        StockDistributionItemVO item = new StockDistributionItemVO();
        item.setProductId(row.getProductId());
        item.setProductCode(row.getProductCode());
        item.setProductName(row.getProductName());
        item.setModel(row.getModel());
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

    private void validateProduct(Product product, Long excludeId, Product currentProduct) {
        // 基础资料统一走字典缓存校验，避免每次保存/修改都分别查表。
        BaseDictMapDTO dictMap = baseDictMapService.getBaseDictMap();
        if (StrUtil.isNotBlank(product.getCode())
                && (currentProduct == null || !StrUtil.equals(product.getCode(), currentProduct.getCode()))) {
            validateCodeUnique(product.getCode(), excludeId);
        }
        if (StrUtil.isNotBlank(product.getBarcode())
                && (currentProduct == null || !StrUtil.equals(product.getBarcode(), currentProduct.getBarcode()))) {
            validateBarcodeUnique(product.getBarcode(), excludeId);
        }
        if (StrUtil.isNotBlank(product.getModel())
                && (currentProduct == null || !StrUtil.equals(product.getModel(), currentProduct.getModel()))) {
            validateModelUnique(product.getModel(), excludeId);
        }
        if (!dictMap.getProductUnitMap().containsKey(product.getUnitId())) {
            throw new BaseException("商品单位不存在");
        }
        if (product.getCategoryId() != null && !dictMap.getProductCategoryMap().containsKey(product.getCategoryId())) {
            throw new BaseException("商品分类不存在");
        }
    }

    private void validateCodeUnique(String code, Long excludeId) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<Product>()
                .eq(Product::getCode, code)
                .ne(excludeId != null, Product::getId, excludeId);
        if (count(wrapper) > 0) {
            throw new BaseException("商品编号已存在");
        }
    }

    private void validateBarcodeUnique(String barcode, Long excludeId) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<Product>()
                .eq(Product::getBarcode, barcode)
                .ne(excludeId != null, Product::getId, excludeId);
        if (count(wrapper) > 0) {
            throw new BaseException("商品条形码已存在");
        }
    }

    private void validateModelUnique(String model, Long excludeId) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<Product>()
                .eq(Product::getModel, model)
                .ne(excludeId != null, Product::getId, excludeId);
        if (count(wrapper) > 0) {
            throw new BaseException("商品型号已存在");
        }
    }

    private void normalizeProduct(Product product) {
        product.setName(StrUtil.trim(product.getName()));
        product.setCode(StrUtil.emptyToNull(StrUtil.trim(product.getCode())));
        product.setBarcode(StrUtil.emptyToNull(StrUtil.trim(product.getBarcode())));
        product.setModel(StrUtil.emptyToNull(StrUtil.trim(product.getModel())));
        product.setRemark(StrUtil.emptyToNull(StrUtil.trim(product.getRemark())));
        if (product.getMinStock() == null) {
            product.setMinStock(0L);
        }
        if (product.getId() == null) {
            product.setTotalStockQty(0L);
            product.setLocationIdsStr(null);
        }
    }

    private static class DistributionRow {

        private final Long productId;
        private final String productCode;
        private final String productName;
        private final String model;
        private final Long unitId;
        private final String unitName;
        private final Long locationId;
        private final String locationCode;
        private final Integer locationSortOrder;
        private final Long qty;

        private DistributionRow(Long productId, String productCode, String productName, String model,
                                Long unitId, String unitName, Long locationId,
                                String locationCode, Integer locationSortOrder, Long qty) {
            this.productId = productId;
            this.productCode = productCode;
            this.productName = productName;
            this.model = model;
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

        public String getModel() {
            return model;
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
