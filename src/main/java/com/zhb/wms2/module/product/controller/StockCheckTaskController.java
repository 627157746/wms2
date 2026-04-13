package com.zhb.wms2.module.product.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zhb.wms2.common.model.R;
import com.zhb.wms2.module.product.model.dto.StockCheckTaskActualQtyDTO;
import com.zhb.wms2.module.product.model.dto.StockCheckTaskAddCategoryDTO;
import com.zhb.wms2.module.product.model.dto.StockCheckTaskAddProductDTO;
import com.zhb.wms2.module.product.model.dto.StockCheckTaskBatchAddDTO;
import com.zhb.wms2.module.product.model.dto.StockCheckTaskCreateDTO;
import com.zhb.wms2.module.product.model.query.StockCheckTaskQuery;
import com.zhb.wms2.module.product.model.vo.StockCheckTaskPageVO;
import com.zhb.wms2.module.product.model.vo.StockCheckTaskVO;
import com.zhb.wms2.module.product.service.StockCheckTaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * 盘点任务控制器。
 *
 * @author zhb
 * @since 2026/4/12
 */
@RestController
@RequestMapping("/product/stockCheckTask")
@Tag(name = "盘点任务", description = "盘点任务管理")
@RequiredArgsConstructor
public class StockCheckTaskController {

    private final StockCheckTaskService stockCheckTaskService;

    @PostMapping
    @Operation(summary = "新增盘点任务")
    public R<Long> create(
            @Parameter(description = "盘点任务", required = true)
            @RequestBody @Validated StockCheckTaskCreateDTO dto) {
        return R.ok(stockCheckTaskService.createTask(dto));
    }

    @PostMapping("/detail")
    @Operation(summary = "盘点任务新增商品")
    public R<Long> addProduct(
            @Parameter(description = "盘点任务商品", required = true)
            @RequestBody @Validated StockCheckTaskAddProductDTO dto) {
        return R.ok(stockCheckTaskService.addProduct(dto));
    }

    @PostMapping("/detail/loadNonZeroStock")
    @Operation(summary = "快捷加入库存不为0的商品")
    public R<Long> addNonZeroStockProducts(
            @Parameter(description = "盘点任务", required = true)
            @RequestBody @Validated StockCheckTaskBatchAddDTO dto) {
        return R.ok(stockCheckTaskService.addNonZeroStockProducts(dto));
    }

    @PostMapping("/detail/loadCategory")
    @Operation(summary = "快捷加入指定分类商品")
    public R<Long> addCategoryProducts(
            @Parameter(description = "盘点任务分类", required = true)
            @RequestBody @Validated StockCheckTaskAddCategoryDTO dto) {
        return R.ok(stockCheckTaskService.addCategoryProducts(dto));
    }

    @PutMapping("/detail/actualQty")
    @Operation(summary = "录入盘点数量")
    public R<Void> updateActualQty(
            @Parameter(description = "盘点数量", required = true)
            @RequestBody @Validated StockCheckTaskActualQtyDTO dto) {
        stockCheckTaskService.updateActualQty(dto);
        return R.optOk();
    }

    @DeleteMapping("/detail/{detailId}")
    @Operation(summary = "删除盘点商品")
    public R<Void> deleteDetail(
            @Parameter(description = "盘点明细ID", required = true)
            @PathVariable @NotNull @Min(1) Long detailId) {
        stockCheckTaskService.removeDetailById(detailId);
        return R.optOk();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除盘点任务")
    public R<Void> delete(
            @Parameter(description = "盘点任务ID", required = true)
            @PathVariable @NotNull @Min(1) Long id) {
        stockCheckTaskService.removeByIdDirect(id);
        return R.optOk();
    }

    @PutMapping("/{id}/finish")
    @Operation(summary = "手动结束盘点")
    public R<Void> finish(
            @Parameter(description = "盘点任务ID", required = true)
            @PathVariable @NotNull @Min(1) Long id) {
        stockCheckTaskService.finishTask(id);
        return R.optOk();
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询盘点任务")
    public R<IPage<StockCheckTaskPageVO>> page(
            @Parameter(description = "查询条件")
            @Validated StockCheckTaskQuery query) {
        return R.ok(stockCheckTaskService.pageQuery(query));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询盘点任务详情")
    public R<StockCheckTaskVO> getById(
            @Parameter(description = "盘点任务ID", required = true)
            @PathVariable @NotNull @Min(1) Long id) {
        return R.ok(stockCheckTaskService.getDetailById(id));
    }

    @GetMapping("/{id}/export")
    @Operation(summary = "导出盘点任务详情")
    public void export(
            @Parameter(description = "盘点任务ID", required = true)
            @PathVariable @NotNull @Min(1) Long id,
            HttpServletResponse response) throws IOException {
        stockCheckTaskService.exportDetail(id, response);
    }

    @GetMapping("/{id}/exportPdf")
    @Operation(summary = "导出盘点任务详情PDF")
    public void exportPdf(
            @Parameter(description = "盘点任务ID", required = true)
            @PathVariable @NotNull @Min(1) Long id,
            HttpServletResponse response) throws IOException {
        stockCheckTaskService.exportDetailPdf(id, response);
    }
}
