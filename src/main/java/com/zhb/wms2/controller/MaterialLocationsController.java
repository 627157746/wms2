package com.zhb.wms2.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zhb.wms2.common.R;
import com.zhb.wms2.model.MaterialLocations;
import com.zhb.wms2.model.dto.MaterialLocationsQuery;
import com.zhb.wms2.service.MaterialLocationsService;
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
@RequestMapping("/material-locations")
@Tag(name = "物料位置管理", description = "物料位置相关接口")
@RequiredArgsConstructor
@Validated
public class MaterialLocationsController {

    private final MaterialLocationsService materialLocationsService;

    @PostMapping
    @Operation(summary = "添加物料位置信息")
    public R<Long> save(
            @Parameter(description = "物料位置信息", required = true)
            @RequestBody @Validated MaterialLocations materialLocation) {
        Long id = materialLocationsService.addMaterialLocation(materialLocation);
        return R.ok(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "修改物料位置信息")
    public R<Void> update(
            @Parameter(description = "物料位置ID", required = true)
            @PathVariable @NotNull @Min(1) Long id,
            @Parameter(description = "物料位置信息", required = true)
            @RequestBody @Validated MaterialLocations materialLocation) {
        materialLocation.setId(id);
        materialLocationsService.updateMaterialLocation(materialLocation);
        return R.optOk();
    }

    @GetMapping
    @Operation(summary = "分页查询物料位置信息")
    public R<IPage<MaterialLocations>> page(
            @Parameter(description = "查询条件")
            @Validated MaterialLocationsQuery query) {
        IPage<MaterialLocations> result = materialLocationsService.queryPage(query);
        return R.ok(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询物料位置详情")
    public R<MaterialLocations> getById(
            @Parameter(description = "物料位置ID", required = true)
            @PathVariable @NotNull @Min(1) Long id) {
        MaterialLocations materialLocation = materialLocationsService.getById(id);
        return R.ok(materialLocation);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除物料位置信息")
    public R<Void> delete(
            @Parameter(description = "物料位置ID", required = true)
            @PathVariable @NotNull @Min(1) Long id) {
        materialLocationsService.removeById(id);
        return R.optOk();
    }

    @DeleteMapping("/batch")
    @Operation(summary = "批量删除物料位置信息")
    public R<Void> batchDelete(
            @Parameter(description = "物料位置ID列表", required = true)
            @RequestBody @NotEmpty List<@NotNull @Min(1) Long> ids) {
        materialLocationsService.removeByIds(ids);
        return R.optOk();
    }
}
