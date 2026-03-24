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
import com.zhb.wms2.module.product.model.query.StockDistributionQuery;
import com.zhb.wms2.module.product.model.query.ProductQuery;
import com.zhb.wms2.module.product.model.vo.StockDistributionGroupVO;
import com.zhb.wms2.module.product.model.vo.StockDistributionItemVO;
import com.zhb.wms2.module.product.model.vo.ProductPageVO;
import com.zhb.wms2.module.product.service.ProductService;
import com.zhb.wms2.module.product.service.ProductStockDetailService;
import com.zhb.wms2.module.product.service.support.ProductStockSummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Author zhb
 * @Description
 * @Date 2026/3/17 19:07
 */
@Service
@RequiredArgsConstructor
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    // 约定 0 表示“无货位”，用于和库存明细中的未分配期初库存保持一致。
    private static final Long NO_LOCATION_ID = 0L;
    private static final String NO_LOCATION_CODE = "无货位";

    private final BaseDictMapService baseDictMapService;
    private final ProductStockDetailService productStockDetailService;
    private final IoApplyDetailService ioApplyDetailService;
    private final IoOrderDetailService ioOrderDetailService;
    private final ProductStockSummaryService productStockSummaryService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveChecked(Product product) {
        normalizeProduct(product);
        validateProduct(product, null, null);
        if (!super.save(product)) {
            throw new BaseException("商品新增失败");
        }
        applyInitialStockChange(product.getId(), 0L, NO_LOCATION_ID,
                product.getInitialStock(), product.getInitialStockLocationId());
    }

    @Override
    public IPage<ProductPageVO> pageQuery(ProductQuery query) {
        IPage<Product> productPage = page(new Page<>(query.getCurrent(), query.getSize()), buildWrapper(query));
        List<Product> productList = productPage.getRecords();
        if (productList.isEmpty()) {
            return new Page<>(query.getCurrent(), query.getSize());
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
    @Transactional(rollbackFor = Exception.class)
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
        normalizeProduct(oldProduct);
        applyInitialStockChange(product.getId(), oldProduct.getInitialStock(), oldProduct.getInitialStockLocationId(),
                product.getInitialStock(), product.getInitialStockLocationId());
    }

    private LambdaQueryWrapper<Product> buildWrapper(ProductQuery query) {
        return new LambdaQueryWrapper<Product>()
                .like(StrUtil.isNotBlank(query.getName()), Product::getName, query.getName())
                .like(StrUtil.isNotBlank(query.getCode()), Product::getCode, query.getCode())
                .like(StrUtil.isNotBlank(query.getBarcode()), Product::getBarcode, query.getBarcode())
                .like(StrUtil.isNotBlank(query.getModel()), Product::getModel, query.getModel())
                .eq(query.getCategoryId() != null, Product::getCategoryId, query.getCategoryId())
                .eq(query.getUnitId() != null, Product::getUnitId, query.getUnitId())
                .gt(Boolean.FALSE.equals(query.getIncludeZeroStock()), Product::getTotalStockQty, 0)
                .apply(Boolean.TRUE.equals(query.getOnlyShortageStock()), "min_stock > COALESCE(total_stock_qty, 0)")
                .orderByDesc(Product::getId);
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
        vo.setInitialStock(product.getInitialStock());
        vo.setInitialStockLocationId(product.getInitialStockLocationId());
        vo.setTotalStockQty(product.getTotalStockQty() == null ? 0L : product.getTotalStockQty());
        vo.setLocationIdsStr(product.getLocationIdsStr());
        vo.setLocationCodes(buildLocationCodes(product.getLocationIdsStr(), locationMap));
        ProductUnit unit = unitMap.get(product.getUnitId());
        vo.setProductUnitName(unit == null ? null : unit.getName());
        ProductCategory category = categoryMap.get(product.getCategoryId());
        vo.setProductCategoryName(category == null ? null : category.getName());
        if (Objects.equals(product.getInitialStockLocationId(), NO_LOCATION_ID)) {
            vo.setInitialStockLocationCode(NO_LOCATION_CODE);
        } else {
            ProductLocation location = locationMap.get(product.getInitialStockLocationId());
            vo.setInitialStockLocationCode(location == null ? null : location.getCode());
        }
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
        if (currentProduct == null || !StrUtil.equals(product.getCode(), currentProduct.getCode())) {
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
        if (!Objects.equals(product.getInitialStockLocationId(), NO_LOCATION_ID)
                && !dictMap.getProductLocationMap().containsKey(product.getInitialStockLocationId())) {
            throw new BaseException("商品货位不存在");
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
        product.setCode(StrUtil.trim(product.getCode()));
        product.setBarcode(StrUtil.emptyToNull(StrUtil.trim(product.getBarcode())));
        product.setModel(StrUtil.emptyToNull(StrUtil.trim(product.getModel())));
        product.setRemark(StrUtil.emptyToNull(StrUtil.trim(product.getRemark())));
        if (product.getMinStock() == null) {
            product.setMinStock(0L);
        }
        if (product.getInitialStock() == null) {
            product.setInitialStock(0L);
        }
        // 期初库存为 0 时不应再保留具体货位引用，统一收口为“无货位”。
        if (product.getInitialStock() == 0L || product.getInitialStockLocationId() == null) {
            product.setInitialStockLocationId(NO_LOCATION_ID);
        }
        if (product.getId() == null) {
            product.setTotalStockQty(0L);
            product.setLocationIdsStr(null);
        }
    }

    private void applyInitialStockChange(Long productId, Long oldQty, Long oldLocationId,
                                         Long newQty, Long newLocationId) {
        // 只调整“期初库存贡献”的那部分明细，保留后续真实入库形成的其它货位库存。
        Map<Long, ProductStockDetail> detailMap = productStockDetailService.list(new LambdaQueryWrapper<ProductStockDetail>()
                        .eq(ProductStockDetail::getProductId, productId))
                .stream()
                .collect(Collectors.toMap(ProductStockDetail::getLocationId, Function.identity(),
                        (left, right) -> left, LinkedHashMap::new));

        if (oldQty > 0) {
            changeDetailQty(detailMap, productId, oldLocationId, -oldQty);
        }
        if (newQty > 0) {
            changeDetailQty(detailMap, productId, newLocationId, newQty);
        }
        productStockSummaryService.syncByDetailMap(productId, detailMap);
    }

    private void changeDetailQty(Map<Long, ProductStockDetail> detailMap, Long productId, Long locationId, Long delta) {
        if (delta == 0) {
            return;
        }

        ProductStockDetail detail = detailMap.get(locationId);
        long currentQty = detail == null || detail.getQty() == null ? 0L : detail.getQty();
        long targetQty = currentQty + delta;
        if (targetQty < 0) {
            throw new BaseException("库存明细数量异常，无法调整期初库存");
        }
        if (targetQty == 0) {
            // 该货位数量归零时直接删除明细，避免留下 0 库存脏数据。
            productStockDetailService.removeById(detail.getId());
            detailMap.remove(locationId);
            return;
        }
        if (detail == null) {
            // 新货位首次出现时补一条库存明细。
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
