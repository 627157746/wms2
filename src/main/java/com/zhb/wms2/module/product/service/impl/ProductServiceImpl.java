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
import com.zhb.wms2.module.inventory.model.entity.Inventory;
import com.zhb.wms2.module.inventory.model.entity.InventoryDetail;
import com.zhb.wms2.module.inventory.service.InventoryDetailService;
import com.zhb.wms2.module.inventory.service.InventoryService;
import com.zhb.wms2.module.io.model.entity.IoApplyDetail;
import com.zhb.wms2.module.io.model.entity.IoOrderDetail;
import com.zhb.wms2.module.io.service.IoApplyDetailService;
import com.zhb.wms2.module.io.service.IoOrderDetailService;
import com.zhb.wms2.module.product.mapper.ProductMapper;
import com.zhb.wms2.module.product.model.entity.Product;
import com.zhb.wms2.module.product.model.query.ProductQuery;
import com.zhb.wms2.module.product.model.vo.ProductPageVO;
import com.zhb.wms2.module.product.service.ProductService;
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
    private final InventoryService inventoryService;
    private final InventoryDetailService inventoryDetailService;
    private final IoApplyDetailService ioApplyDetailService;
    private final IoOrderDetailService ioOrderDetailService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveChecked(Product product) {
        normalizeProduct(product);
        validateProduct(product, null);
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

        List<Long> productIds = productList.stream().map(Product::getId).toList();
        Map<Long, Inventory> inventoryMap = inventoryService.list(new LambdaQueryWrapper<Inventory>()
                        .in(Inventory::getProductId, productIds))
                .stream()
                .collect(Collectors.toMap(Inventory::getProductId, Function.identity(),
                        (left, right) -> left, LinkedHashMap::new));
        return productPage.convert(product -> buildProductPageVO(product, categoryMap, locationMap, unitMap,
                inventoryMap.get(product.getId())));
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
        Inventory inventory = inventoryService.getOne(new LambdaQueryWrapper<Inventory>()
                .eq(Inventory::getProductId, id), false);
        return buildProductPageVO(product, categoryMap, locationMap, unitMap, inventory);
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
        Map<Long, Inventory> inventoryMap = inventoryService.list(new LambdaQueryWrapper<Inventory>()
                        .in(Inventory::getProductId, productList.stream().map(Product::getId).toList()))
                .stream()
                .collect(Collectors.toMap(Inventory::getProductId, Function.identity(),
                        (left, right) -> left, LinkedHashMap::new));
        return productList.stream()
                .collect(Collectors.toMap(Product::getId,
                        product -> buildProductPageVO(product, categoryMap, locationMap, unitMap,
                                inventoryMap.get(product.getId())),
                        (left, right) -> left, LinkedHashMap::new));
    }

    @Override
    public void removeByIdChecked(Long id) {
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
        long inventoryCount = inventoryService.count(
                new LambdaQueryWrapper<Inventory>().eq(Inventory::getProductId, id));
        if (inventoryCount > 0) {
            throw new BaseException("该商品仍有库存，无法删除");
        }
        long inventoryDetailCount = inventoryDetailService.count(
                new LambdaQueryWrapper<InventoryDetail>().eq(InventoryDetail::getProductId, id));
        if (inventoryDetailCount > 0) {
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
        validateProduct(product, product.getId());
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
                .eq(query.getCategoryId() != null, Product::getCategoryId, query.getCategoryId())
                .eq(query.getUnitId() != null, Product::getUnitId, query.getUnitId())
                .inSql(Boolean.FALSE.equals(query.getIncludeZeroStock()), Product::getId,
                        "select product_id from inventory where total_qty > 0")
                .orderByDesc(Product::getId);
    }

    private ProductPageVO buildProductPageVO(Product product,
                                             Map<Long, ProductCategory> categoryMap,
                                             Map<Long, ProductLocation> locationMap,
                                             Map<Long, ProductUnit> unitMap,
                                             Inventory inventory) {
        ProductPageVO vo = new ProductPageVO();
        vo.setId(product.getId());
        vo.setName(product.getName());
        vo.setCode(product.getCode());
        vo.setBarcode(product.getBarcode());
        vo.setUnitId(product.getUnitId());
        vo.setCategoryId(product.getCategoryId());
        vo.setMinStock(product.getMinStock());
        vo.setInitialStock(product.getInitialStock());
        vo.setInitialStockLocationId(product.getInitialStockLocationId());
        vo.setTotalStockQty(inventory == null || inventory.getTotalQty() == null ? 0L : inventory.getTotalQty());
        vo.setLocationCodes(buildLocationCodes(inventory == null ? null : inventory.getLocationIdsStr(), locationMap));
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

    private void validateProduct(Product product, Long excludeId) {
        // 基础资料统一走字典缓存校验，避免每次保存/修改都分别查表。
        BaseDictMapDTO dictMap = baseDictMapService.getBaseDictMap();
        validateCodeUnique(product.getCode(), excludeId);
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

    private void normalizeProduct(Product product) {
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
    }

    private void applyInitialStockChange(Long productId, Long oldQty, Long oldLocationId,
                                         Long newQty, Long newLocationId) {
        // 只调整“期初库存贡献”的那部分明细，保留后续真实入库形成的其它货位库存。
        Map<Long, InventoryDetail> detailMap = inventoryDetailService.list(new LambdaQueryWrapper<InventoryDetail>()
                        .eq(InventoryDetail::getProductId, productId))
                .stream()
                .collect(Collectors.toMap(InventoryDetail::getLocationId, Function.identity(),
                        (left, right) -> left, LinkedHashMap::new));

        if (oldQty > 0) {
            changeDetailQty(detailMap, productId, oldLocationId, -oldQty);
        }
        if (newQty > 0) {
            changeDetailQty(detailMap, productId, newLocationId, newQty);
        }
        syncInventoryMain(productId, detailMap);
    }

    private void syncInventoryMain(Long productId, Map<Long, InventoryDetail> detailMap) {
        // 主库存始终以当前全部库存明细汇总结果为准。
        Inventory inventory = inventoryService.getOne(new LambdaQueryWrapper<Inventory>()
                .eq(Inventory::getProductId, productId), false);
        if (detailMap.isEmpty()) {
            if (inventory != null) {
                inventoryService.removeById(inventory.getId());
            }
            return;
        }

        Long totalQty = detailMap.values().stream()
                .map(InventoryDetail::getQty)
                .reduce(0L, Long::sum);
        String locationIdsStr = detailMap.keySet().stream()
                .filter(locationId -> !Objects.equals(locationId, NO_LOCATION_ID))
                .sorted(Comparator.naturalOrder())
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        if (locationIdsStr.isBlank()) {
            locationIdsStr = null;
        }
        if (inventory == null) {
            inventory = new Inventory();
            inventory.setProductId(productId);
            inventory.setTotalQty(totalQty);
            inventory.setLocationIdsStr(locationIdsStr);
            inventoryService.save(inventory);
            return;
        }
        inventory.setTotalQty(totalQty);
        inventory.setLocationIdsStr(locationIdsStr);
        inventoryService.updateById(inventory);
    }

    private void changeDetailQty(Map<Long, InventoryDetail> detailMap, Long productId, Long locationId, Long delta) {
        if (delta == 0) {
            return;
        }

        InventoryDetail detail = detailMap.get(locationId);
        long currentQty = detail == null || detail.getQty() == null ? 0L : detail.getQty();
        long targetQty = currentQty + delta;
        if (targetQty < 0) {
            throw new BaseException("库存明细数量异常，无法调整期初库存");
        }
        if (targetQty == 0) {
            // 该货位数量归零时直接删除明细，避免留下 0 库存脏数据。
            inventoryDetailService.removeById(detail.getId());
            detailMap.remove(locationId);
            return;
        }
        if (detail == null) {
            // 新货位首次出现时补一条库存明细。
            InventoryDetail inventoryDetail = new InventoryDetail();
            inventoryDetail.setProductId(productId);
            inventoryDetail.setLocationId(locationId);
            inventoryDetail.setQty(targetQty);
            inventoryDetailService.save(inventoryDetail);
            detailMap.put(locationId, inventoryDetail);
            return;
        }
        detail.setQty(targetQty);
        inventoryDetailService.updateById(detail);
    }
}
