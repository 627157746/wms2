package com.zhb.wms2.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zhb.wms2.common.R;
import com.zhb.wms2.model.WarehouseTypes;
import com.zhb.wms2.model.dto.WarehouseTypesQuery;
import com.zhb.wms2.service.WarehouseTypesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

@RestController
@RequestMapping("/warehouse-types")
@Tag(name = "出入库类型管理", description = "出入库类型字典表相关接口")
@RequiredArgsConstructor
@Validated
public class WarehouseTypesController {

    private final WarehouseTypesService warehouseTypesService;

    @PostMapping
    @Operation(summary = "添加出入库类型")
    public R<Long> save(
            @Parameter(description = "出入库类型信息", required = true)
            @RequestBody @Validated WarehouseTypes warehouseTypes) {
        Long id = warehouseTypesService.addWarehouseTypes(warehouseTypes);
        return R.ok(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "修改出入库类型")
    public R<Void> update(
            @Parameter(description = "类型ID", required = true)
            @PathVariable @NotNull @Min(1) Long id,
            @Parameter(description = "出入库类型信息", required = true)
            @RequestBody @Validated WarehouseTypes warehouseTypes) {
        warehouseTypes.setId(id);
        warehouseTypesService.updateWarehouseTypes(warehouseTypes);
        return R.optOk();
    }

    @GetMapping
    @Operation(summary = "分页查询出入库类型")
    public R<IPage<WarehouseTypes>> page(
            @Parameter(description = "查询条件")
            @Validated WarehouseTypesQuery query) {
        IPage<WarehouseTypes> result = warehouseTypesService.queryPage(query);
        return R.ok(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询出入库类型详情")
    public R<WarehouseTypes> getById(
            @Parameter(description = "类型ID", required = true)
            @PathVariable @NotNull @Min(1) Long id) {
        WarehouseTypes warehouseTypes = warehouseTypesService.getById(id);
        return R.ok(warehouseTypes);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除出入库类型")
    public R<Void> delete(
            @Parameter(description = "类型ID", required = true)
            @PathVariable @NotNull @Min(1) Long id) {
        warehouseTypesService.removeById(id);
        return R.optOk();
    }

    @DeleteMapping("/batch")
    @Operation(summary = "批量删除出入库类型")
    public R<Void> batchDelete(
            @Parameter(description = "类型ID列表", required = true)
            @RequestBody @NotEmpty List<@NotNull @Min(1) Long> ids) {
        warehouseTypesService.removeByIds(ids);
        return R.optOk();
    }
}
