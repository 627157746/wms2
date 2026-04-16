package com.zhb.wms2.module.product.service.impl;

import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhb.wms2.common.enums.IoBizTypeEnum;
import com.zhb.wms2.common.enums.StockCheckResultEnum;
import com.zhb.wms2.common.enums.StockCheckTaskStatusEnum;
import com.zhb.wms2.common.exception.BaseException;
import com.zhb.wms2.module.base.model.dto.BaseDictMapDTO;
import com.zhb.wms2.module.base.model.entity.ProductCategory;
import com.zhb.wms2.module.base.service.BaseDictMapService;
import com.zhb.wms2.module.io.model.dto.IoOrderDetailDTO;
import com.zhb.wms2.module.io.model.entity.IoOrder;
import com.zhb.wms2.module.product.mapper.StockCheckTaskMapper;
import com.zhb.wms2.module.product.model.dto.*;
import com.zhb.wms2.module.product.model.entity.Product;
import com.zhb.wms2.module.product.model.entity.StockCheckTask;
import com.zhb.wms2.module.product.model.entity.StockCheckTaskDetail;
import com.zhb.wms2.module.product.model.query.StockCheckTaskQuery;
import com.zhb.wms2.module.product.model.vo.*;
import com.zhb.wms2.module.product.service.ProductService;
import com.zhb.wms2.module.product.service.StockCheckTaskDetailService;
import com.zhb.wms2.module.product.service.StockCheckTaskService;
import com.zhb.wms2.util.PdfExportUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 盘点任务服务实现。
 *
 * @author zhb
 * @since 2026/4/12
 */
@Service
@RequiredArgsConstructor
public class StockCheckTaskServiceImpl extends ServiceImpl<StockCheckTaskMapper, StockCheckTask>
        implements StockCheckTaskService {

    private static final String TASK_NO_PREFIX = "PD";

    private static final int TASK_NO_DIGIT_LENGTH = 6;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final ProductService productService;
    private final StockCheckTaskDetailService stockCheckTaskDetailService;
    private final BaseDictMapService baseDictMapService;

    /**
     * 分页查询盘点任务，并实时汇总应盘、已盘、盘盈、盘亏数量。
     */
    @Override
    public IPage<StockCheckTaskPageVO> pageQuery(StockCheckTaskQuery query) {
        IPage<StockCheckTask> page = page(new Page<>(query.getCurrent(), query.getSize()), buildWrapper(query));
        List<StockCheckTask> recordList = page.getRecords();
        if (recordList.isEmpty()) {
            return new Page<>(query.getCurrent(), query.getSize());
        }
        Map<Long, StockCheckTaskStatVO> statMap = buildTaskStatMap(recordList.stream().map(StockCheckTask::getId).toList());
        return page.convert(task -> buildPageVO(task, statMap.get(task.getId())));
    }

    /**
     * 查询盘点任务详情，并补充商品信息和统计信息。
     */
    @Override
    public StockCheckTaskVO getDetailById(Long id) {
        StockCheckTask task = getById(id);
        if (task == null) {
            throw new BaseException("盘点任务不存在");
        }
        List<StockCheckTaskDetail> detailList = listDetailByTaskId(id);
        StockCheckTaskStatVO stat = buildTaskStat(detailList);
        StockCheckTaskVO vo = new StockCheckTaskVO();
        vo.setId(task.getId())
                .setTaskNo(task.getTaskNo())
                .setTaskDate(task.getTaskDate())
                .setStatus(task.getStatus())
                .setFinishTime(task.getFinishTime())
                .setProfitOrderId(task.getProfitOrderId())
                .setProfitOrderNo(task.getProfitOrderNo())
                .setLossOrderId(task.getLossOrderId())
                .setLossOrderNo(task.getLossOrderNo())
                .setRemark(task.getRemark())
                .setCreateTime(task.getCreateTime())
                .setUpdateTime(task.getUpdateTime())
                .setCreateBy(task.getCreateBy())
                .setUpdateBy(task.getUpdateBy());
        vo.setStatusName(StockCheckTaskStatusEnum.getDesc(task.getStatus()))
                .setTotalCount(stat.getTotalCount())
                .setCountedCount(stat.getCountedCount())
                .setUncountedCount(stat.getUncountedCount())
                .setProfitCount(stat.getProfitCount())
                .setLossCount(stat.getLossCount());
        vo.setDetailList(buildDetailVOList(detailList));
        return vo;
    }

    /**
     * 导出盘点任务详情 Excel。
     */
    @Override
    public void exportDetail(Long id, HttpServletResponse response) throws IOException {
        StockCheckTaskVO vo = getDetailById(id);
        String fileName = URLEncoder.encode(buildExportFileName(vo, ".xlsx"), StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20");
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setHeader("Content-Disposition", "attachment;filename*=UTF-8''" + fileName);

        try (ExcelWriter writer = ExcelUtil.getWriter(true)) {
            writer.renameSheet("盘点任务详情");
            writeDetailSheet(writer, vo);
            writer.autoSizeColumnAll();
            writer.flush(response.getOutputStream(), true);
        }
    }

    /**
     * 导出盘点任务详情 PDF。
     */
    @Override
    public void exportDetailPdf(Long id, HttpServletResponse response) throws IOException {
        StockCheckTaskVO vo = getDetailById(id);
        PdfExportUtil.writePdf(buildExportFileName(vo, ".pdf"), "盘点任务详情", true,
                buildDetailPdfBody(vo), response);
    }

    /**
     * 创建盘点任务，任务创建后直接进入盘点中状态。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createTask(StockCheckTaskCreateDTO dto) {
        StockCheckTask task = new StockCheckTask()
                .setTaskNo(generateTaskNo())
                .setTaskDate(dto.getTaskDate())
                .setStatus(StockCheckTaskStatusEnum.COUNTING.getCode())
                .setRemark(dto.getRemark());
        if (!save(task)) {
            throw new BaseException("盘点任务新增失败");
        }
        return task.getId();
    }

    /**
     * 向盘点任务中追加商品，并冻结加入当刻的商品总库存。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addProduct(StockCheckTaskAddProductDTO dto) {
        StockCheckTask task = getTaskChecked(dto.getTaskId(), true);
        Product product = productService.getById(dto.getProductId());
        if (product == null) {
            throw new BaseException("商品不存在");
        }
        long count = stockCheckTaskDetailService.count(new LambdaQueryWrapper<StockCheckTaskDetail>()
                .eq(StockCheckTaskDetail::getTaskId, task.getId())
                .eq(StockCheckTaskDetail::getProductId, dto.getProductId()));
        if (count > 0) {
            throw new BaseException("该商品已加入盘点任务");
        }
        // 账面数量以加入任务当刻的商品总库存为准，后续正常出入库不再回刷该快照。
        Long actualQty = dto.getActualQty();
        long snapshotQty = product.getTotalStockQty() == null ? 0L : product.getTotalStockQty();
        Long diffQty = actualQty == null ? null : actualQty - snapshotQty;
        StockCheckTaskDetail detail = new StockCheckTaskDetail()
                .setTaskId(task.getId())
                .setProductId(dto.getProductId())
                .setSnapshotQty(snapshotQty)
                .setActualQty(actualQty)
                .setDiffQty(diffQty)
                .setResultType(actualQty == null
                        ? StockCheckResultEnum.UNCOUNTED.getCode()
                        : StockCheckResultEnum.fromDiffQty(diffQty))
                .setCountTime(actualQty == null ? null : LocalDateTime.now())
                .setRemark(dto.getRemark());
        stockCheckTaskDetailService.saveChecked(detail);
        return detail.getId();
    }

    /**
     * 快捷加入当前库存不为0的商品，并自动跳过已存在的盘点商品。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addNonZeroStockProducts(StockCheckTaskBatchAddDTO dto) {
        getTaskChecked(dto.getTaskId(), true);
        List<Product> productList = productService.list(new LambdaQueryWrapper<Product>()
                .gt(Product::getTotalStockQty, 0)
                .orderByAsc(Product::getCategoryId));
        return addProductsToTask(dto.getTaskId(), productList);
    }

    /**
     * 快捷加入指定分类及其子分类商品，并自动跳过已存在的盘点商品。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addCategoryProducts(StockCheckTaskAddCategoryDTO dto) {
        getTaskChecked(dto.getTaskId(), true);
        BaseDictMapDTO dictMap = baseDictMapService.getBaseDictMap();
        Map<Long, ProductCategory> categoryMap = dictMap.getProductCategoryMap() == null
                ? Map.of()
                : dictMap.getProductCategoryMap();
        if (!categoryMap.containsKey(dto.getCategoryId())) {
            throw new BaseException("商品分类不存在");
        }
        List<Long> categoryIdList = buildCategoryIdList(dto.getCategoryId(), categoryMap);
        List<Product> productList = productService.list(new LambdaQueryWrapper<Product>()
                .in(Product::getCategoryId, categoryIdList)
                .orderByAsc(Product::getId));
        return addProductsToTask(dto.getTaskId(), productList);
    }

    /**
     * 直接删除盘点任务及其明细，不做状态和关联校验。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeByIdDirect(Long id) {
        stockCheckTaskDetailService.remove(new LambdaQueryWrapper<StockCheckTaskDetail>()
                .eq(StockCheckTaskDetail::getTaskId, id));
        if (!removeById(id)) {
            throw new BaseException("盘点任务不存在");
        }
    }

    /**
     * 删除盘点商品，仅允许盘点中操作。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeDetailById(Long detailId) {
        StockCheckTaskDetail detail = stockCheckTaskDetailService.getById(detailId);
        if (detail == null) {
            throw new BaseException("盘点商品不存在");
        }
        getTaskChecked(detail.getTaskId(), true);
        stockCheckTaskDetailService.removeByIdChecked(detailId);
    }

    /**
     * 录入盘点数量，并同步计算差异数量和盘点结果。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateActualQty(StockCheckTaskActualQtyDTO dto) {
        StockCheckTaskDetail detail = stockCheckTaskDetailService.getById(dto.getDetailId());
        if (detail == null) {
            throw new BaseException("盘点商品不存在");
        }
        getTaskChecked(detail.getTaskId(), true);
        long snapshotQty = detail.getSnapshotQty() == null ? 0L : detail.getSnapshotQty();
        long diffQty = dto.getActualQty() - snapshotQty;
        // 盘点结果完全由差异数量推导，不额外引入独立状态字段。
        detail.setActualQty(dto.getActualQty())
                .setDiffQty(diffQty)
                .setResultType(StockCheckResultEnum.fromDiffQty(diffQty))
                .setCountTime(LocalDateTime.now())
                .setRemark(dto.getRemark());
        stockCheckTaskDetailService.updateByIdChecked(detail);
    }

    /**
     * 手动结束盘点，要求任务下所有商品都已录入盘点数量。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void finishTask(Long id) {
        StockCheckTask task = getTaskChecked(id, true);
        List<StockCheckTaskDetail> detailList = listDetailByTaskId(id);
        if (detailList.isEmpty()) {
            throw new BaseException("盘点商品不能为空");
        }
        boolean hasUncounted = detailList.stream().anyMatch(detail -> detail.getActualQty() == null);
        if (hasUncounted) {
            throw new BaseException("请先录入全部商品的盘点数量");
        }
        task.setStatus(StockCheckTaskStatusEnum.COUNTED.getCode())
                .setFinishTime(LocalDateTime.now());
        if (!updateById(task)) {
            throw new BaseException("盘点任务不存在");
        }
    }

    /**
     * 绑定人工创建的调整单，并按单据方向回填盘盈或盘亏单号。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bindAdjustOrder(Long taskId, IoOrder ioOrder, List<IoOrderDetailDTO> detailDTOList) {
        StockCheckTask task = getTaskChecked(taskId, false);
        if (!StockCheckTaskStatusEnum.COUNTED.matches(task.getStatus())) {
            throw new BaseException("盘点任务未完成盘点，无法关联调整单");
        }
        // 调整单必须和盘点差异逐商品严格一致，避免把无关单据挂到盘点任务上。
        Map<Long, Long> expectedMap = buildExpectedAdjustQtyMap(task.getId(), ioOrder.getOrderType());
        if (expectedMap.isEmpty()) {
            throw new BaseException(buildAdjustOrderMissingMessage(ioOrder.getOrderType()));
        }
        Map<Long, Long> actualMap = buildActualOrderQtyMap(detailDTOList);
        if (!expectedMap.equals(actualMap)) {
            throw new BaseException(buildAdjustOrderMismatchMessage(ioOrder.getOrderType()));
        }

        if (IoBizTypeEnum.INBOUND.matches(ioOrder.getOrderType())) {
            if (task.getProfitOrderId() != null) {
                throw new BaseException("该盘点任务已关联盘盈入库单");
            }
            task.setProfitOrderId(ioOrder.getId())
                    .setProfitOrderNo(ioOrder.getOrderNo());
        } else {
            if (task.getLossOrderId() != null) {
                throw new BaseException("该盘点任务已关联盘亏出库单");
            }
            task.setLossOrderId(ioOrder.getId())
                    .setLossOrderNo(ioOrder.getOrderNo());
        }
        // 只有存在差异且所需方向的调整单都已回填后，任务才进入已调整。
        if (shouldMarkAdjusted(task, listDetailByTaskId(task.getId()))) {
            task.setStatus(StockCheckTaskStatusEnum.ADJUSTED.getCode());
        }
        if (!updateById(task)) {
            throw new BaseException("盘点任务不存在");
        }
    }

    /**
     * 校验出入库单是否已被盘点任务引用，避免后续改单或删单导致盘点回填失真。
     */
    @Override
    public void validateOrderNotLinked(Long orderId) {
        long count = count(new LambdaQueryWrapper<StockCheckTask>()
                .and(wrapper -> wrapper.eq(StockCheckTask::getProfitOrderId, orderId)
                        .or()
                        .eq(StockCheckTask::getLossOrderId, orderId)));
        if (count > 0) {
            throw new BaseException("该出入库单已关联盘点任务，不允许修改或删除");
        }
    }

    /**
     * 构建盘点任务分页查询条件。
     */
    private LambdaQueryWrapper<StockCheckTask> buildWrapper(StockCheckTaskQuery query) {
        return new LambdaQueryWrapper<StockCheckTask>()
                .like(query.getTaskNo() != null && !query.getTaskNo().isBlank(), StockCheckTask::getTaskNo, query.getTaskNo())
                .eq(query.getStatus() != null, StockCheckTask::getStatus, query.getStatus())
                .ge(query.getTaskDateStart() != null, StockCheckTask::getTaskDate, query.getTaskDateStart())
                .le(query.getTaskDateEnd() != null, StockCheckTask::getTaskDate, query.getTaskDateEnd())
                .orderByDesc(StockCheckTask::getId);
    }

    /**
     * 写出盘点任务详情 Excel。
     */
    private void writeDetailSheet(ExcelWriter writer, StockCheckTaskVO vo) {
        writer.writeRow(Arrays.asList("任务号", vo.getTaskNo(), "盘点日期", valueToText(vo.getTaskDate())));
        writer.writeRow(Arrays.asList("状态", vo.getStatusName(), "结束时间", valueToText(vo.getFinishTime())));
        writer.writeRow(Arrays.asList("盘盈入库单号", vo.getProfitOrderNo(), "盘亏出库单号", vo.getLossOrderNo()));
        writer.writeRow(Arrays.asList("备注", vo.getRemark()));
        writer.writeRow(List.of());
        writer.writeRow(Arrays.asList("应盘数", vo.getTotalCount(), "已盘数", vo.getCountedCount(), "未盘数", vo.getUncountedCount()));
        writer.writeRow(Arrays.asList("盘盈数", vo.getProfitCount(), "盘亏数", vo.getLossCount()));
        writer.writeRow(List.of());
        writer.writeRow(List.of("商品名称", "商品编号", "商品型号", "商品分类", "商品单位", "账面数量",
                "盘点数量", "差异数量", "盘点结果", "录入时间", "备注"));
        List<StockCheckTaskDetailVO> detailList = vo.getDetailList();
        if (detailList == null || detailList.isEmpty()) {
            return;
        }
        detailList.forEach(detail -> writer.writeRow(Arrays.asList(
                detail.getProduct() == null ? null : detail.getProduct().getName(),
                detail.getProduct() == null ? null : detail.getProduct().getCode(),
                detail.getProduct() == null ? null : detail.getProduct().getModel(),
                detail.getProduct() == null ? null : detail.getProduct().getProductCategoryName(),
                detail.getProduct() == null ? null : detail.getProduct().getProductUnitName(),
                detail.getSnapshotQty(),
                detail.getActualQty(),
                detail.getDiffQty(),
                detail.getResultTypeName(),
                valueToText(detail.getCountTime()),
                detail.getRemark()
        )));
    }

    /**
     * 构建盘点任务详情 PDF 主体。
     */
    private String buildDetailPdfBody(StockCheckTaskVO vo) {
        StringBuilder html = new StringBuilder();
        html.append("<div class=\"section\">")
                .append("<div class=\"section-title\">任务信息</div>")
                .append("<table><tbody>")
                .append("<tr><th>任务号</th><td>").append(PdfExportUtil.escape(vo.getTaskNo())).append("</td>")
                .append("<th>盘点日期</th><td>").append(PdfExportUtil.escape(valueToText(vo.getTaskDate()))).append("</td></tr>")
                .append("<tr><th>状态</th><td>").append(PdfExportUtil.escape(vo.getStatusName())).append("</td>")
                .append("<th>结束时间</th><td>").append(PdfExportUtil.escape(valueToText(vo.getFinishTime()))).append("</td></tr>")
                .append("<tr><th>盘盈入库单号</th><td>").append(PdfExportUtil.escape(vo.getProfitOrderNo())).append("</td>")
                .append("<th>盘亏出库单号</th><td>").append(PdfExportUtil.escape(vo.getLossOrderNo())).append("</td></tr>")
                .append("<tr><th>备注</th><td colspan=\"3\">").append(PdfExportUtil.escape(vo.getRemark())).append("</td></tr>")
                .append("</tbody></table></div>");
        html.append("<div class=\"section\">")
                .append("<div class=\"section-title\">统计信息</div>")
                .append("<table><tbody>")
                .append("<tr><th>应盘数</th><td>").append(vo.getTotalCount() == null ? "" : vo.getTotalCount()).append("</td>")
                .append("<th>已盘数</th><td>").append(vo.getCountedCount() == null ? "" : vo.getCountedCount()).append("</td>")
                .append("<th>未盘数</th><td>").append(vo.getUncountedCount() == null ? "" : vo.getUncountedCount()).append("</td></tr>")
                .append("<tr><th>盘盈数</th><td>").append(vo.getProfitCount() == null ? "" : vo.getProfitCount()).append("</td>")
                .append("<th>盘亏数</th><td>").append(vo.getLossCount() == null ? "" : vo.getLossCount()).append("</td>")
                .append("<th></th><td></td></tr>")
                .append("</tbody></table></div>");
        html.append("<div class=\"section\">")
                .append("<div class=\"section-title\">盘点明细</div>");
        List<StockCheckTaskDetailVO> detailList = vo.getDetailList();
        if (detailList == null || detailList.isEmpty()) {
            html.append("<div class=\"empty\">暂无数据</div></div>");
            return html.toString();
        }
        html.append("<table><thead><tr>")
                .append("<th>商品名称</th><th>商品编号</th><th>商品型号</th><th>商品分类</th><th>商品单位</th>")
                .append("<th>账面数量</th><th>盘点数量</th><th>差异数量</th><th>盘点结果</th><th>录入时间</th><th>备注</th>")
                .append("</tr></thead><tbody>");
        for (StockCheckTaskDetailVO detail : detailList) {
            ProductPageVO product = detail.getProduct();
            html.append("<tr>")
                    .append("<td>").append(PdfExportUtil.escape(product == null ? null : product.getName())).append("</td>")
                    .append("<td>").append(PdfExportUtil.escape(product == null ? null : product.getCode())).append("</td>")
                    .append("<td>").append(PdfExportUtil.escape(product == null ? null : product.getModel())).append("</td>")
                    .append("<td>").append(PdfExportUtil.escape(product == null ? null : product.getProductCategoryName())).append("</td>")
                    .append("<td>").append(PdfExportUtil.escape(product == null ? null : product.getProductUnitName())).append("</td>")
                    .append("<td>").append(detail.getSnapshotQty() == null ? "" : detail.getSnapshotQty()).append("</td>")
                    .append("<td>").append(detail.getActualQty() == null ? "" : detail.getActualQty()).append("</td>")
                    .append("<td>").append(detail.getDiffQty() == null ? "" : detail.getDiffQty()).append("</td>")
                    .append("<td>").append(PdfExportUtil.escape(detail.getResultTypeName())).append("</td>")
                    .append("<td>").append(PdfExportUtil.escape(valueToText(detail.getCountTime()))).append("</td>")
                    .append("<td>").append(PdfExportUtil.escape(detail.getRemark())).append("</td>")
                    .append("</tr>");
        }
        html.append("</tbody></table></div>");
        return html.toString();
    }

    /**
     * 构建导出文件名。
     */
    private String buildExportFileName(StockCheckTaskVO vo, String suffix) {
        String taskNo = vo.getTaskNo() == null || vo.getTaskNo().isBlank() ? String.valueOf(vo.getId()) : vo.getTaskNo();
        return "盘点任务详情-" + taskNo + suffix;
    }

    /**
     * 将对象安全转换为文本。
     */
    private String valueToText(Object value) {
        if (value == null) {
            return "";
        }
        if (value instanceof LocalDate localDate) {
            return localDate.format(DATE_FORMATTER);
        }
        if (value instanceof LocalDateTime localDateTime) {
            return localDateTime.format(DATE_TIME_FORMATTER);
        }
        return value.toString();
    }

    /**
     * 查询并校验盘点任务状态。
     */
    private StockCheckTask getTaskChecked(Long taskId, boolean requireCounting) {
        StockCheckTask task = getById(taskId);
        if (task == null) {
            throw new BaseException("盘点任务不存在");
        }
        if (requireCounting && !StockCheckTaskStatusEnum.COUNTING.matches(task.getStatus())) {
            throw new BaseException("当前盘点任务不是盘点中状态");
        }
        return task;
    }

    /**
     * 查询指定任务下的盘点明细。
     */
    private List<StockCheckTaskDetail> listDetailByTaskId(Long taskId) {
        return stockCheckTaskDetailService.list(new LambdaQueryWrapper<StockCheckTaskDetail>()
                .eq(StockCheckTaskDetail::getTaskId, taskId)
                .orderByAsc(StockCheckTaskDetail::getId));
    }

    /**
     * 批量加入商品到盘点任务，已存在的商品直接跳过。
     */
    private Long addProductsToTask(Long taskId, List<Product> productList) {
        if (productList == null || productList.isEmpty()) {
            return 0L;
        }
        Set<Long> existingProductIdSet = stockCheckTaskDetailService.list(new LambdaQueryWrapper<StockCheckTaskDetail>()
                        .eq(StockCheckTaskDetail::getTaskId, taskId)
                        .select(StockCheckTaskDetail::getProductId))
                .stream()
                .map(StockCheckTaskDetail::getProductId)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        long addedCount = 0L;
        for (Product product : productList) {
            if (product == null || product.getId() == null || existingProductIdSet.contains(product.getId())) {
                continue;
            }
            StockCheckTaskDetail detail = new StockCheckTaskDetail()
                    .setTaskId(taskId)
                    .setProductId(product.getId())
                    .setSnapshotQty(product.getTotalStockQty() == null ? 0L : product.getTotalStockQty())
                    .setResultType(StockCheckResultEnum.UNCOUNTED.getCode());
            stockCheckTaskDetailService.saveChecked(detail);
            existingProductIdSet.add(product.getId());
            addedCount++;
        }
        return addedCount;
    }

    /**
     * 批量统计多个任务的盘点结果数量，供分页列表复用。
     */
    private Map<Long, StockCheckTaskStatVO> buildTaskStatMap(Collection<Long> taskIds) {
        if (taskIds == null || taskIds.isEmpty()) {
            return Collections.emptyMap();
        }
        List<StockCheckTaskDetail> detailList = stockCheckTaskDetailService.list(new LambdaQueryWrapper<StockCheckTaskDetail>()
                .in(StockCheckTaskDetail::getTaskId, taskIds)
                .orderByAsc(StockCheckTaskDetail::getId));
        if (detailList.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<Long, StockCheckTaskStatVO> statMap = new LinkedHashMap<>();
        for (StockCheckTaskDetailVO detail : buildDetailVOList(detailList)) {
            StockCheckTaskStatVO stat = statMap.computeIfAbsent(detail.getTaskId(), key -> new StockCheckTaskStatVO());
            stat.accept(detail);
        }
        return statMap;
    }

    /**
     * 构建盘点任务分页结果对象。
     */
    private StockCheckTaskPageVO buildPageVO(StockCheckTask task, StockCheckTaskStatVO stat) {
        StockCheckTaskStatVO safeStat = stat == null ? new StockCheckTaskStatVO() : stat;
        StockCheckTaskPageVO vo = new StockCheckTaskPageVO();
        vo.setId(task.getId())
                .setTaskNo(task.getTaskNo())
                .setTaskDate(task.getTaskDate())
                .setStatus(task.getStatus())
                .setFinishTime(task.getFinishTime())
                .setProfitOrderId(task.getProfitOrderId())
                .setProfitOrderNo(task.getProfitOrderNo())
                .setLossOrderId(task.getLossOrderId())
                .setLossOrderNo(task.getLossOrderNo())
                .setRemark(task.getRemark())
                .setCreateTime(task.getCreateTime())
                .setUpdateTime(task.getUpdateTime())
                .setCreateBy(task.getCreateBy())
                .setUpdateBy(task.getUpdateBy());
        vo.setStatusName(StockCheckTaskStatusEnum.getDesc(task.getStatus()))
                .setTotalCount(safeStat.getTotalCount())
                .setCountedCount(safeStat.getCountedCount())
                .setUncountedCount(safeStat.getUncountedCount())
                .setProfitCount(safeStat.getProfitCount())
                .setLossCount(safeStat.getLossCount());
        return vo;
    }

    /**
     * 构建盘点明细展示对象，并补充商品信息。
     */
    private List<StockCheckTaskDetailVO> buildDetailVOList(List<StockCheckTaskDetail> detailList) {
        if (detailList.isEmpty()) {
            return List.of();
        }
        Set<Long> productIds = detailList.stream()
                .map(StockCheckTaskDetail::getProductId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, ProductPageVO> productMap = productService.getDetailMapByIds(productIds);
        return detailList.stream()
                .map(detail -> {
                    StockCheckTaskDetailVO vo = new StockCheckTaskDetailVO();
                    vo.setId(detail.getId())
                            .setTaskId(detail.getTaskId())
                            .setProductId(detail.getProductId())
                            .setSnapshotQty(detail.getSnapshotQty())
                            .setActualQty(detail.getActualQty())
                            .setDiffQty(detail.getDiffQty())
                            .setResultType(detail.getResultType())
                            .setCountTime(detail.getCountTime())
                            .setRemark(detail.getRemark())
                            .setCreateTime(detail.getCreateTime())
                            .setUpdateTime(detail.getUpdateTime())
                            .setCreateBy(detail.getCreateBy())
                            .setUpdateBy(detail.getUpdateBy());
                    vo.setResultTypeName(StockCheckResultEnum.getDesc(detail.getResultType()))
                            .setProduct(productMap.get(detail.getProductId()));
                    return vo;
                })
                .toList();
    }

    /**
     * 汇总单个任务的盘点统计。
     */
    private StockCheckTaskStatVO buildTaskStat(List<StockCheckTaskDetail> detailList) {
        StockCheckTaskStatVO stat = new StockCheckTaskStatVO();
        buildDetailVOList(detailList).forEach(stat::accept);
        return stat;
    }

    /**
     * 递归展开商品分类，生成包含子分类的分类范围。
     */
    private List<Long> buildCategoryIdList(Long categoryId, Map<Long, ProductCategory> categoryMap) {
        if (categoryId == null) {
            return List.of();
        }
        if (categoryMap.isEmpty()) {
            return List.of(categoryId);
        }

        // 分类快捷加入沿用商品列表页语义，选中父分类时默认包含全部子分类商品。
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
     * 按调整单方向提取任务中待调整的商品数量。
     */
    private Map<Long, Long> buildExpectedAdjustQtyMap(Long taskId, Integer orderType) {
        List<StockCheckTaskDetail> detailList = listDetailByTaskId(taskId);
        return detailList.stream()
                .filter(detail -> detail.getDiffQty() != null)
                .filter(detail -> IoBizTypeEnum.INBOUND.matches(orderType)
                        ? detail.getDiffQty() > 0
                        : detail.getDiffQty() < 0)
                .collect(Collectors.groupingBy(StockCheckTaskDetail::getProductId, LinkedHashMap::new,
                        Collectors.summingLong(detail -> Math.abs(detail.getDiffQty()))));
    }

    /**
     * 将出入库单明细汇总为商品数量映射，用于和盘点差异做一一校验。
     */
    private Map<Long, Long> buildActualOrderQtyMap(List<IoOrderDetailDTO> detailDTOList) {
        return detailDTOList.stream()
                .collect(Collectors.groupingBy(IoOrderDetailDTO::getProductId, LinkedHashMap::new,
                        Collectors.summingLong(detail -> detail.getQty() == null ? 0L : detail.getQty())));
    }

    /**
     * 判断盘点任务是否满足进入已调整状态。
     */
    private boolean shouldMarkAdjusted(StockCheckTask task, List<StockCheckTaskDetail> detailList) {
        boolean hasProfit = detailList.stream().anyMatch(detail -> detail.getDiffQty() != null && detail.getDiffQty() > 0);
        boolean hasLoss = detailList.stream().anyMatch(detail -> detail.getDiffQty() != null && detail.getDiffQty() < 0);
        if (!hasProfit && !hasLoss) {
            return false;
        }
        if (hasProfit && task.getProfitOrderId() == null) {
            return false;
        }
        if (hasLoss && task.getLossOrderId() == null) {
            return false;
        }
        return true;
    }

    /**
     * 构建调整单商品数量不匹配时的错误信息。
     */
    private String buildAdjustOrderMismatchMessage(Integer orderType) {
        return IoBizTypeEnum.INBOUND.matches(orderType)
                ? "盘盈入库单商品或数量与盘点差异不一致"
                : "盘亏出库单商品或数量与盘点差异不一致";
    }

    /**
     * 构建盘点任务不存在待调整差异时的错误信息。
     */
    private String buildAdjustOrderMissingMessage(Integer orderType) {
        return IoBizTypeEnum.INBOUND.matches(orderType)
                ? "该盘点任务不存在待调整的盘盈差异"
                : "该盘点任务不存在待调整的盘亏差异";
    }

    /**
     * 生成盘点任务号。
     */
    private String generateTaskNo() {
        StockCheckTask lastTask = getOne(new LambdaQueryWrapper<StockCheckTask>()
                .likeRight(StockCheckTask::getTaskNo, TASK_NO_PREFIX)
                .orderByDesc(StockCheckTask::getId)
                .last("limit 1"), false);
        long nextNumber = 1L;
        if (lastTask != null && lastTask.getTaskNo() != null && lastTask.getTaskNo().startsWith(TASK_NO_PREFIX)) {
            nextNumber = Long.parseLong(lastTask.getTaskNo().substring(TASK_NO_PREFIX.length())) + 1;
        }
        return TASK_NO_PREFIX + String.format("%0" + TASK_NO_DIGIT_LENGTH + "d", nextNumber);
    }

}
