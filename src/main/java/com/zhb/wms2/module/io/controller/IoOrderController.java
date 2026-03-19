package com.zhb.wms2.module.io.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zhb.wms2.common.model.R;
import com.zhb.wms2.common.validated.Save;
import com.zhb.wms2.common.validated.Update;
import com.zhb.wms2.module.io.model.dto.IoOrderCreateDTO;
import com.zhb.wms2.module.io.model.dto.IoOrderUpdateDTO;
import com.zhb.wms2.module.io.model.entity.IoOrder;
import com.zhb.wms2.module.io.model.query.IoOrderQuery;
import com.zhb.wms2.module.io.service.IoOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/io/order")
@Tag(name = "出入库单", description = "出入库单管理")
@RequiredArgsConstructor
@Validated
public class IoOrderController {

    private final IoOrderService ioOrderService;

    @GetMapping("/page")
    @Operation(summary = "分页查询出入库单")
    public R<IPage<? extends IoOrder>> page(
            @Parameter(description = "查询条件")
            @Validated IoOrderQuery query) {
        return R.ok(ioOrderService.pageQuery(query));
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
    public R<Void> update(
            @Parameter(description = "出入库单", required = true)
            @RequestBody @Validated({Save.class, Update.class}) IoOrderUpdateDTO dto) {
        ioOrderService.updateOrder(dto);
        return R.optOk();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除出入库单")
    public R<Void> delete(
            @Parameter(description = "出入库单ID", required = true)
            @PathVariable @NotNull @Min(1) Long id) {
        ioOrderService.removeByIdChecked(id);
        return R.optOk();
    }
}
