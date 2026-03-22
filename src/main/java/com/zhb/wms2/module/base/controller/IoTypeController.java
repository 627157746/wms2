package com.zhb.wms2.module.base.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zhb.wms2.common.model.R;
import com.zhb.wms2.common.validated.Save;
import com.zhb.wms2.common.validated.Update;
import com.zhb.wms2.module.base.model.entity.IoType;
import com.zhb.wms2.module.base.model.query.IoTypeQuery;
import com.zhb.wms2.module.base.service.IoTypeService;
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
@RequestMapping("/base/ioType")
@Tag(name = "出入库类型", description = "出入库类型管理")
@RequiredArgsConstructor
@Validated
public class IoTypeController {

    private final IoTypeService ioTypeService;

    @PostMapping
    @Operation(summary = "新增出入库类型")
    public R<Long> create(
            @Parameter(description = "出入库类型", required = true)
            @RequestBody @Validated(Save.class) IoType ioType) {
        ioTypeService.saveChecked(ioType);
        return R.ok(ioType.getId());
    }

    @PutMapping
    @Operation(summary = "修改出入库类型")
    public R<Void> update(
            @Parameter(description = "出入库类型", required = true)
            @RequestBody @Validated(Update.class) IoType ioType) {
        ioTypeService.updateByIdChecked(ioType);
        return R.optOk();
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询出入库类型详情")
    public R<IoType> getById(
            @Parameter(description = "类型ID", required = true)
            @PathVariable @NotNull @Min(1) Long id) {
        return R.ok(ioTypeService.getById(id));
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询出入库类型")
    public R<IPage<IoType>> page(
            @Parameter(description = "查询条件")
            @Validated IoTypeQuery query) {
        return R.ok(ioTypeService.pageQuery(query));
    }

    @GetMapping("/all")
    @Operation(summary = "根据适用范围查询出入库类型")
    public R<List<IoType>> all(
            @Parameter(description = "1-入库 2-出库", required = true)
            @RequestParam @NotNull @Min(1) @Max(2) Integer scope) {
        return R.ok(ioTypeService.listAllByScope(scope));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除出入库类型")
    public R<Void> delete(
            @Parameter(description = "类型ID", required = true)
            @PathVariable @NotNull @Min(1) Long id) {
        ioTypeService.removeByIdChecked(id);
        return R.optOk();
    }
}
