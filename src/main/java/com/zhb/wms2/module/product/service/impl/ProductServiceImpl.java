package com.zhb.wms2.module.product.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
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
import com.zhb.wms2.module.product.model.vo.ProductStockStatVO;
import com.zhb.wms2.module.product.model.vo.StockDistributionGroupVO;
import com.zhb.wms2.module.product.model.vo.StockDistributionItemVO;
import com.zhb.wms2.module.product.service.ProductService;
import com.zhb.wms2.module.product.service.ProductStockDetailService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * ProductServiceImpl 服务实现
 *
 * @author zhb
 * @since 2026/3/26
 */
@Service
@RequiredArgsConstructor
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    private final BaseDictMapService baseDictMapService;
    private final ProductStockDetailService productStockDetailService;
    private final IoApplyDetailService ioApplyDetailService;
    private final IoOrderDetailService ioOrderDetailService;

    /**
     * 新增商品，并在保存前做规范化与唯一性校验。
     */
    @Override
    public void saveChecked(Product product) {
        // 商品入库前统一做字段清洗和引用校验，避免脏数据进入库存链路。
        normalizeProduct(product);
        validateProduct(product, null, null);
        if (!super.save(product)) {
            throw new BaseException("商品新增失败");
        }
    }

    /**
     * 分页查询商品，并补充分类、单位和货位展示信息。
     */
    @Override
    public IPage<ProductPageVO> pageQuery(ProductQuery query) {
        BaseDictMapDTO dictMap = baseDictMapService.getBaseDictMap();
        Map<Long, ProductCategory> categoryMap = dictMap.getProductCategoryMap() == null
                ? Collections.emptyMap()
                : dictMap.getProductCategoryMap();
        IPage<Product> productPage = page(new Page<>(query.getCurrent(), query.getSize()),
                buildPageWrapper(query, categoryMap));
        List<Product> productList = productPage.getRecords();
        if (productList.isEmpty()) {
            return new Page<>(query.getCurrent(), query.getSize());
        }
        // 商品分页统一批量回填分类、货位和单位展示信息。
        Map<Long, ProductLocation> locationMap = dictMap.getProductLocationMap() == null
                ? Collections.emptyMap()
                : dictMap.getProductLocationMap();
        Map<Long, ProductUnit> unitMap = dictMap.getProductUnitMap() == null
                ? Collections.emptyMap()
                : dictMap.getProductUnitMap();
        return productPage.convert(product -> buildProductPageVO(product, categoryMap, locationMap, unitMap));
    }

    /**
     * 查询商品详情，并组装页面展示对象。
     */
    @Override
    public ProductPageVO getDetailById(Long id) {
        Product product = getById(id);
        if (product == null) {
            throw new BaseException("商品不存在");
        }

        // 详情页复用分页 VO 组装逻辑，保持展示字段一致。
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

    /**
     * 查询商品库存汇总，并复用商品列表筛选条件。
     */
    @Override
    public ProductStockStatVO getStockStat(ProductQuery query) {
        BaseDictMapDTO dictMap = baseDictMapService.getBaseDictMap();
        Map<Long, ProductCategory> categoryMap = dictMap.getProductCategoryMap() == null
                ? Collections.emptyMap()
                : dictMap.getProductCategoryMap();
        Long totalStockQty = list(buildFilterWrapper(query, categoryMap)
                .select(Product::getTotalStockQty)).stream()
                .map(Product::getTotalStockQty)
                .filter(Objects::nonNull)
                .mapToLong(Long::longValue)
                .sum();
        return new ProductStockStatVO().setTotalStockQty(totalStockQty);
    }

    /**
     * 批量查询商品详情映射，供其他模块复用。
     */
    @Override
    public Map<Long, ProductPageVO> getDetailMapByIds(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyMap();
        }

        List<Product> productList = listByIds(ids);
        if (productList.isEmpty()) {
            return Collections.emptyMap();
        }

        // 批量组装商品详情，供申请单、出入库单等模块复用。
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

    /**
     * 按货位维度汇总商品库存分布。
     */
    @Override
    public List<StockDistributionGroupVO> listDistribution(StockDistributionQuery query) {
        // 库存分布只统计正库存明细，零库存不进入展示结果。
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

        // 先拉平成可排序行，再按货位聚合成前端展示结构。
        List<StockDistributionRow> rowList = detailList.stream()
                .map(detail -> buildDistributionRow(detail, productMap, locationMap, unitMap))
                .filter(Objects::nonNull)
                .sorted(buildRowComparator())
                .toList();
        if (rowList.isEmpty()) {
            return List.of();
        }
        return buildLocationGroupList(rowList);
    }

    /**
     * 导出库存货位分布。
     */
    @Override
    public void exportDistribution(StockDistributionQuery query, HttpServletResponse response) throws IOException {
        List<StockDistributionGroupVO> groupList = listDistribution(query);
        String fileName = URLEncoder.encode("库存货位分布.xlsx", StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20");
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setHeader("Content-Disposition", "attachment;filename*=UTF-8''" + fileName);

        try (ExcelWriter writer = ExcelUtil.getWriter(true)) {
            writer.renameSheet("库存货位分布");
            writeDistributionSheet(writer, groupList);
            writer.autoSizeColumnAll();
            writer.flush(response.getOutputStream(), true);
        }
    }

    /**
     * 删除商品前校验关联申请、出入库记录和库存数据。
     */
    @Override
    public void removeByIdChecked(Long id) {
        Product product = getById(id);
        if (product == null) {
            throw new BaseException("商品不存在");
        }
        // 商品删除需要同时拦截申请、单据、库存汇总和库存明细四条引用链。
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

    /**
     * 修改商品，并校验唯一字段与基础资料引用。
     */
    @Override
    public void updateByIdChecked(Product product) {
        Product oldProduct = getById(product.getId());
        if (oldProduct == null) {
            throw new BaseException("商品不存在");
        }
        // 修改时保留旧数据做唯一字段差异校验，避免无变更字段重复查重。
        normalizeProduct(product);
        validateProduct(product, product.getId(), oldProduct);
        if (!updateById(product)) {
            throw new BaseException("商品不存在");
        }
    }

    /**
     * 构建商品分页查询条件。
     */
    private LambdaQueryWrapper<Product> buildPageWrapper(ProductQuery query, Map<Long, ProductCategory> categoryMap) {
        return buildFilterWrapper(query, categoryMap)
                .orderByDesc(Product::getId);
    }

    /**
     * 构建商品查询过滤条件。
     */
    private LambdaQueryWrapper<Product> buildFilterWrapper(ProductQuery query, Map<Long, ProductCategory> categoryMap) {
        // 分类查询需要把子分类一并展开，保证树节点筛选符合业务预期。
        List<Long> categoryIdList = buildCategoryIdList(query.getCategoryId(), categoryMap);
        return new LambdaQueryWrapper<Product>()
                .like(StrUtil.isNotBlank(query.getName()), Product::getName, query.getName())
                .like(StrUtil.isNotBlank(query.getCode()), Product::getCode, query.getCode())
                .like(StrUtil.isNotBlank(query.getBarcode()), Product::getBarcode, query.getBarcode())
                .like(StrUtil.isNotBlank(query.getModel()), Product::getModel, query.getModel())
                .in(!categoryIdList.isEmpty(), Product::getCategoryId, categoryIdList)
                .eq(query.getUnitId() != null, Product::getUnitId, query.getUnitId())
                .gt(Boolean.FALSE.equals(query.getIncludeZeroStock()), Product::getTotalStockQty, 0)
                .apply(Boolean.TRUE.equals(query.getOnlyShortageStock()), "min_stock > COALESCE(total_stock_qty, 0)");
    }

    /**
     * 递归展开分类树，生成包含子分类的查询范围。
     */
    private List<Long> buildCategoryIdList(Long categoryId, Map<Long, ProductCategory> categoryMap) {
        if (categoryId == null) {
            return List.of();
        }
        if (categoryMap.isEmpty()) {
            return List.of(categoryId);
        }

        // 先构建父子映射，再按广度优先把所有后代分类展开。
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

    /**
     * 组装商品分页展示对象。
     */
    private ProductPageVO buildProductPageVO(Product product,
                                             Map<Long, ProductCategory> categoryMap,
                                             Map<Long, ProductLocation> locationMap,
                                             Map<Long, ProductUnit> unitMap) {
        ProductPageVO vo = new ProductPageVO();
        vo.setId(product.getId())
                .setName(product.getName())
                .setCode(product.getCode())
                .setBarcode(product.getBarcode())
                .setModel(product.getModel())
                .setUnitId(product.getUnitId())
                .setCategoryId(product.getCategoryId())
                .setMinStock(product.getMinStock())
                .setTotalStockQty(product.getTotalStockQty() == null ? 0L : product.getTotalStockQty())
                .setLocationIdsStr(product.getLocationIdsStr());
        ProductUnit unit = unitMap.get(product.getUnitId());
        vo.setCreateTime(product.getCreateTime())
                .setUpdateTime(product.getUpdateTime())
                .setCreateBy(product.getCreateBy())
                .setUpdateBy(product.getUpdateBy());
        ProductCategory category = categoryMap.get(product.getCategoryId());
        // 货位编码列表由 locationIdsStr 派生，避免实体中冗余存储展示字段。
        vo.setLocationCodes(buildLocationCodes(product.getLocationIdsStr(), locationMap))
                .setProductUnitName(unit == null ? null : unit.getName())
                .setProductCategoryName(category == null ? null : category.getName());
        vo.setRemark(product.getRemark());
        return vo;
    }

    /**
     * 将货位 ID 串转换成可展示的货位编码列表。
     */
    private List<String> buildLocationCodes(String locationIdsStr, Map<Long, ProductLocation> locationMap) {
        if (StrUtil.isBlank(locationIdsStr)) {
            return List.of();
        }
        // 历史数据里可能混有非法货位 ID，这里降级回显原始值而不是中断查询。
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

    /**
     * 批量查询商品实体并转成 ID 映射。
     */
    private Map<Long, Product> buildProductMap(Collection<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return Collections.emptyMap();
        }
        // 用有序映射保持后续聚合结果的稳定性。
        return baseMapper.selectBatchIds(productIds).stream()
                .collect(LinkedHashMap::new, (map, product) -> map.put(product.getId(), product), Map::putAll);
    }

    /**
     * 将库存明细转换为货位分布计算行。
     */
    private StockDistributionRow buildDistributionRow(ProductStockDetail detail,
                                                      Map<Long, Product> productMap,
                                                      Map<Long, ProductLocation> locationMap,
                                                      Map<Long, ProductUnit> unitMap) {
        Product product = productMap.get(detail.getProductId());
        if (product == null) {
            return null;
        }

        // 分布统计以库存明细为主表，商品维度信息在这里一次性回填。
        Long locationId = detail.getLocationId();
        String locationCode = buildLocationCode(locationId, locationMap);
        Integer locationSortOrder = buildLocationSortOrder(locationId, locationMap);
        ProductUnit productUnit = unitMap.get(product.getUnitId());
        return new StockDistributionRow(
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

    /**
     * 构建库存分布展示所需的排序规则。
     */
    private Comparator<StockDistributionRow> buildRowComparator() {
        return Comparator
                .comparing(StockDistributionRow::getLocationSortOrder, Comparator.nullsLast(Integer::compareTo))
                .thenComparing(StockDistributionRow::getLocationCode, Comparator.nullsLast(String::compareToIgnoreCase))
                .thenComparing(StockDistributionRow::getLocationId, Comparator.nullsLast(Long::compareTo))
                .thenComparing(StockDistributionRow::getProductCode, Comparator.nullsLast(String::compareToIgnoreCase))
                .thenComparing(StockDistributionRow::getProductId, Comparator.nullsLast(Long::compareTo));
    }

    /**
     * 按货位将库存分布行聚合为页面展示结构。
     */
    private List<StockDistributionGroupVO> buildLocationGroupList(List<StockDistributionRow> rowList) {
        Map<Long, StockDistributionGroupVO> groupMap = new LinkedHashMap<>();
        for (StockDistributionRow row : rowList) {
            // 同一货位下的商品明细汇总到一个分组中，便于前端直接渲染。
            StockDistributionGroupVO group = groupMap.computeIfAbsent(row.getLocationId(), locationId -> {
                return new StockDistributionGroupVO()
                        .setLocationId(locationId)
                        .setLocationCode(row.getLocationCode())
                        .setTotalQty(0L)
                        .setItemList(new ArrayList<>());
            });
            group.getItemList().add(buildDistributionItem(row));
            group.setTotalQty(group.getTotalQty() + row.getQty());
        }
        return new ArrayList<>(groupMap.values());
    }

    /**
     * 按查询接口的货位分组结构写出导出内容。
     */
    private void writeDistributionSheet(ExcelWriter writer, List<StockDistributionGroupVO> groupList) {
        for (StockDistributionGroupVO group : groupList) {
            List<StockDistributionItemVO> itemList = group.getItemList();
            if (itemList == null || itemList.isEmpty()) {
                continue;
            }
            writer.writeRow(List.of(buildLocationTitle(group)));
            writer.writeRow(List.of("名称", "型号", "数量"));
            itemList.forEach(item -> writer.writeRow(Arrays.asList(item.getProductName(), item.getModel(), item.getQty())));
            writer.writeRow(List.of());
        }
    }

    /**
     * 组装货位分组标题。
     */
    private String buildLocationTitle(StockDistributionGroupVO group) {
        String locationCode = StrUtil.blankToDefault(group.getLocationCode(), "-");
        Long totalQty = group.getTotalQty() == null ? 0L : group.getTotalQty();
        return "货位：" + locationCode + "，合计：" + totalQty;
    }

    /**
     * 构建单条库存分布明细项。
     */
    private StockDistributionItemVO buildDistributionItem(StockDistributionRow row) {
        return new StockDistributionItemVO()
                .setProductId(row.getProductId())
                .setProductCode(row.getProductCode())
                .setProductName(row.getProductName())
                .setModel(row.getModel())
                .setUnitId(row.getUnitId())
                .setUnitName(row.getUnitName())
                .setQty(row.getQty());
    }

    /**
     * 将货位 ID 转成可展示的货位编码。
     */
    private String buildLocationCode(Long locationId, Map<Long, ProductLocation> locationMap) {
        ProductLocation location = locationMap.get(locationId);
        return location == null ? null : location.getCode();
    }

    /**
     * 读取货位排序值。
     */
    private Integer buildLocationSortOrder(Long locationId, Map<Long, ProductLocation> locationMap) {
        ProductLocation location = locationMap.get(locationId);
        return location == null ? null : location.getSortOrder();
    }

    /**
     * 校验商品唯一字段与基础资料引用。
     */
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

    /**
     * 校验商品编号唯一。
     */
    private void validateCodeUnique(String code, Long excludeId) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<Product>()
                .eq(Product::getCode, code)
                .ne(excludeId != null, Product::getId, excludeId);
        if (count(wrapper) > 0) {
            throw new BaseException("商品编号已存在");
        }
    }

    /**
     * 校验商品条形码唯一。
     */
    private void validateBarcodeUnique(String barcode, Long excludeId) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<Product>()
                .eq(Product::getBarcode, barcode)
                .ne(excludeId != null, Product::getId, excludeId);
        if (count(wrapper) > 0) {
            throw new BaseException("商品条形码已存在");
        }
    }

    /**
     * 校验商品型号唯一。
     */
    private void validateModelUnique(String model, Long excludeId) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<Product>()
                .eq(Product::getModel, model)
                .ne(excludeId != null, Product::getId, excludeId);
        if (count(wrapper) > 0) {
            throw new BaseException("商品型号已存在");
        }
    }

    /**
     * 清洗可选字段，并初始化新增商品的库存汇总字段。
     */
    private void normalizeProduct(Product product) {
        // 保存前统一清洗可选字符串字段，避免空串和空白字符污染唯一校验。
        String code = StrUtil.emptyToNull(StrUtil.trim(product.getCode()));
        String barcode = StrUtil.emptyToNull(StrUtil.trim(product.getBarcode()));
        if (code == null && barcode != null) {
            // 商品编号为空时默认复用条形码，减少前端重复录入。
            code = barcode;
        }
        product.setName(StrUtil.trim(product.getName()))
                .setCode(code)
                .setBarcode(barcode)
                .setModel(StrUtil.emptyToNull(StrUtil.trim(product.getModel())))
                .setRemark(StrUtil.emptyToNull(StrUtil.trim(product.getRemark())));
        if (product.getMinStock() == null) {
            product.setMinStock(0L);
        }
        if (product.getId() == null) {
            // 新增商品初始无库存和货位分布，统一由库存业务后续维护。
            product.setTotalStockQty(0L)
                    .setLocationIdsStr(null);
        }
    }

}
