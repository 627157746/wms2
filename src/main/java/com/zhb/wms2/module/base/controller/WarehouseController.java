package com.zhb.wms2.module.base.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zhb.wms2.common.model.R;
import com.zhb.wms2.common.validated.Save;
import com.zhb.wms2.common.validated.Update;
import com.zhb.wms2.module.base.model.entity.Warehouse;
import com.zhb.wms2.module.base.model.query.WarehouseQuery;
import com.zhb.wms2.module.base.service.WarehouseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.List;
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

/**
 * 仓库控制器
 *
 * @author zhb
 * @since 2026/3/27
 */
@RestController
@RequestMapping("/base/warehouse")
@Tag(name = "仓库", description = "仓库管理")
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseService warehouseService;

    @PostMapping
    @Operation(summary = "新增仓库")
    public R<Long> create(
            @Parameter(description = "仓库", required = true)
            @RequestBody @Validated(Save.class) Warehouse warehouse) {
        warehouseService.saveChecked(warehouse);
        return R.ok(warehouse.getId());
    }

    @PutMapping
    @Operation(summary = "修改仓库")
    public R<Void> update(
            @Parameter(description = "仓库", required = true)
            @RequestBody @Validated(Update.class) Warehouse warehouse) {
        warehouseService.updateByIdChecked(warehouse);
        return R.optOk();
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询仓库详情")
    public R<Warehouse> getById(
            @Parameter(description = "仓库ID", required = true)
            @PathVariable @NotNull @Min(1) Long id) {
        return R.ok(warehouseService.getById(id));
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询仓库")
    public R<IPage<Warehouse>> page(
            @Parameter(description = "查询条件")
            @Validated WarehouseQuery query) {
        return R.ok(warehouseService.pageQuery(query));
    }

    @GetMapping("/all")
    @Operation(summary = "查询全部仓库")
    public R<List<Warehouse>> all() {
        return R.ok(warehouseService.listAll());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除仓库")
    public R<Void> delete(
            @Parameter(description = "仓库ID", required = true)
            @PathVariable @NotNull @Min(1) Long id) {
        warehouseService.removeByIdChecked(id);
        return R.optOk();
    }
}
