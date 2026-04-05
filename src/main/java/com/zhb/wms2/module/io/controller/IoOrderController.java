package com.zhb.wms2.module.io.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zhb.wms2.common.lock.MethodLock;
import com.zhb.wms2.common.model.R;
import com.zhb.wms2.common.validated.Save;
import com.zhb.wms2.common.validated.Update;
import com.zhb.wms2.module.io.model.dto.IoOrderCreateDTO;
import com.zhb.wms2.module.io.model.dto.IoOrderDetailLocationUpdateDTO;
import com.zhb.wms2.module.io.model.dto.IoOrderUpdateDTO;
import com.zhb.wms2.module.io.model.query.IoOrderQuery;
import com.zhb.wms2.module.io.model.vo.IoOrderPageVO;
import com.zhb.wms2.module.io.service.IoOrderService;
import com.zhb.wms2.module.product.model.query.StockIoDetailQuery;
import com.zhb.wms2.module.product.model.vo.StockIoDetailStatVO;
import com.zhb.wms2.module.product.model.vo.StockIoDetailVO;
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
 * 出入库单控制器
 *
 * @author zhb
 * @since 2026/3/26
 */
@RestController
@RequestMapping("/io/order")
@Tag(name = "出入库单", description = "出入库单管理")
@RequiredArgsConstructor
public class IoOrderController {

    private final IoOrderService ioOrderService;

    @GetMapping("/page")
    @Operation(summary = "分页查询出入库单")
    public R<IPage<IoOrderPageVO>> page(
            @Parameter(description = "查询条件")
            @Validated IoOrderQuery query) {
        return R.ok(ioOrderService.pageQuery(query));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询出入库单详情")
    public R<IoOrderPageVO> getById(
            @Parameter(description = "出入库单ID", required = true)
            @PathVariable @NotNull @Min(1) Long id) {
        return R.ok(ioOrderService.getDetailById(id));
    }

    @PostMapping
    @Operation(summary = "手工新增出入库单")
    public R<Long> create(
            @Parameter(description = "出入库单", required = true)
            @RequestBody @Validated(Save.class) IoOrderCreateDTO dto) {
        return R.ok(ioOrderService.saveOrder(dto));
    }

    @PutMapping
    @Operation(summary = "修改出入库单")
    @MethodLock(name = "stock", key = "#p0.id")
    public R<Void> update(
            @Parameter(description = "出入库单", required = true)
            @RequestBody @Validated({Save.class, Update.class}) IoOrderUpdateDTO dto) {
        ioOrderService.updateOrder(dto);
        return R.optOk();
    }

    @PutMapping("/detail/location")
    @Operation(summary = "修改出入库单明细货位")
    @MethodLock(name = "stock", key = "#p0.detailId")
    public R<Void> updateDetailLocation(
            @Parameter(description = "明细货位", required = true)
            @RequestBody @Validated IoOrderDetailLocationUpdateDTO dto) {
        ioOrderService.updateDetailLocation(dto);
        return R.optOk();
    }

    @PutMapping("/{id}/pick")
    @Operation(summary = "出库单拣货")
    @MethodLock(name = "stock", key = "#p0")
    public R<Void> pick(
            @Parameter(description = "出入库单ID", required = true)
            @PathVariable @NotNull @Min(1) Long id) {
        ioOrderService.pickById(id);
        return R.optOk();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除出入库单")
    @MethodLock(name = "stock", key = "#p0")
    public R<Void> delete(
            @Parameter(description = "出入库单ID", required = true)
            @PathVariable @NotNull @Min(1) Long id) {
        ioOrderService.removeByIdChecked(id);
        return R.optOk();
    }

    @GetMapping("/ioDetail/page")
    @Operation(summary = "分页查询商品出入库明细")
    public R<IPage<StockIoDetailVO>> ioDetailPage(
            @Parameter(description = "查询条件")
            @Validated StockIoDetailQuery query) {
        return R.ok(ioOrderService.pageDetailByProductId(query));
    }

    @GetMapping("/ioDetail/export")
    @Operation(summary = "导出商品出入库明细")
    public void ioDetailExport(
            @Parameter(description = "查询条件")
            @Validated StockIoDetailQuery query,
            HttpServletResponse response) throws IOException {
        ioOrderService.exportDetailByProductId(query, response);
    }

    @GetMapping("/ioDetail/stat")
    @Operation(summary = "统计商品出入库明细")
    public R<StockIoDetailStatVO> ioDetailStat(
            @Parameter(description = "查询条件")
            @Validated StockIoDetailQuery query) {
        return R.ok(ioOrderService.getDetailStatByProductId(query));
    }
}
