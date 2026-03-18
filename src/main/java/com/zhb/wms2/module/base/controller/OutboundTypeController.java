package com.zhb.wms2.module.base.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zhb.wms2.common.model.R;
import com.zhb.wms2.common.validated.Save;
import com.zhb.wms2.common.validated.Update;
import com.zhb.wms2.module.base.model.entity.OutboundType;
import com.zhb.wms2.module.base.model.query.OutboundTypeQuery;
import com.zhb.wms2.module.base.service.OutboundTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/base/outbound-types")
@Tag(name = "出库类型", description = "出库类型管理")
@RequiredArgsConstructor
@Validated
public class OutboundTypeController {

    private final OutboundTypeService outboundTypeService;

    @PostMapping
    @Operation(summary = "新增出库类型")
    public R<Long> save(
            @Parameter(description = "出库类型", required = true)
            @RequestBody @Validated(Save.class) OutboundType type) {
        outboundTypeService.save(type);
        return R.ok(type.getId());
    }

    @PutMapping
    @Operation(summary = "修改出库类型")
    public R<Void> update(
            @Parameter(description = "出库类型", required = true)
            @RequestBody @Validated(Update.class) OutboundType type) {
        outboundTypeService.updateById(type);
        return R.optOk();
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询出库类型详情")
    public R<OutboundType> getById(
            @Parameter(description = "类型ID", required = true)
            @PathVariable @NotNull @Min(1) Long id) {
        return R.ok(outboundTypeService.getById(id));
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询出库类型")
    public R<IPage<OutboundType>> page(
            @Parameter(description = "查询条件")
            @Validated OutboundTypeQuery query) {
        return R.ok(outboundTypeService.pageQuery(query));
    }

    @GetMapping("/all")
    @Operation(summary = "查询全部出库类型")
    public R<List<OutboundType>> all() {
        return R.ok(outboundTypeService.listAll());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除出库类型")
    public R<Void> delete(
            @Parameter(description = "类型ID", required = true)
            @PathVariable @NotNull @Min(1) Long id) {
        outboundTypeService.removeByIdChecked(id);
        return R.optOk();
    }
}
