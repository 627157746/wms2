package com.zhb.wms2.module.base.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zhb.wms2.common.model.R;
import com.zhb.wms2.common.validated.Save;
import com.zhb.wms2.common.validated.Update;
import com.zhb.wms2.module.base.model.entity.Deliveryman;
import com.zhb.wms2.module.base.model.query.DeliverymanQuery;
import com.zhb.wms2.module.base.service.DeliverymanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/base/deliveryman")
@Tag(name = "送货员", description = "送货员管理")
@RequiredArgsConstructor
@Validated
public class DeliverymanController {

    private final DeliverymanService deliverymanService;

    @PostMapping
    @Operation(summary = "新增送货员")
    public R<Long> create(
            @Parameter(description = "送货员", required = true)
            @RequestBody @Validated(Save.class) Deliveryman deliveryman) {
        deliverymanService.saveChecked(deliveryman);
        return R.ok(deliveryman.getId());
    }

    @PutMapping
    @Operation(summary = "修改送货员")
    public R<Void> update(
            @Parameter(description = "送货员", required = true)
            @RequestBody @Validated(Update.class) Deliveryman deliveryman) {
        deliverymanService.updateByIdChecked(deliveryman);
        return R.optOk();
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询送货员详情")
    public R<Deliveryman> getById(
            @Parameter(description = "送货员ID", required = true)
            @PathVariable @NotNull @Min(1) Long id) {
        return R.ok(deliverymanService.getById(id));
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询送货员")
    public R<IPage<Deliveryman>> page(
            @Parameter(description = "查询条件")
            @Validated DeliverymanQuery query) {
        return R.ok(deliverymanService.pageQuery(query));
    }

    @GetMapping("/all")
    @Operation(summary = "根据适用范围查询送货员")
    public R<List<Deliveryman>> all(
            @Parameter(description = "1-入库 2-出库", required = true)
            @RequestParam @NotNull @Min(1) @Max(2) Integer scope) {
        return R.ok(deliverymanService.listAllByScope(scope));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除送货员")
    public R<Void> delete(
            @Parameter(description = "送货员ID", required = true)
            @PathVariable @NotNull @Min(1) Long id) {
        deliverymanService.removeByIdChecked(id);
        return R.optOk();
    }
}
