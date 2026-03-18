package com.zhb.wms2.module.base.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zhb.wms2.common.model.R;
import com.zhb.wms2.common.validated.Save;
import com.zhb.wms2.common.validated.Update;
import com.zhb.wms2.module.base.model.entity.InboundType;
import com.zhb.wms2.module.base.model.query.InboundTypeQuery;
import com.zhb.wms2.module.base.service.InboundTypeService;
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
@RequestMapping("/base/inbound-types")
@Tag(name = "入库类型", description = "入库类型管理")
@RequiredArgsConstructor
@Validated
public class InboundTypeController {

    private final InboundTypeService inboundTypeService;

    @PostMapping
    @Operation(summary = "新增入库类型")
    public R<Long> save(
            @Parameter(description = "入库类型", required = true)
            @RequestBody @Validated(Save.class) InboundType type) {
        inboundTypeService.save(type);
        return R.ok(type.getId());
    }

    @PutMapping
    @Operation(summary = "修改入库类型")
    public R<Void> update(
            @Parameter(description = "入库类型", required = true)
            @RequestBody @Validated(Update.class) InboundType type) {
        inboundTypeService.updateById(type);
        return R.optOk();
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询入库类型详情")
    public R<InboundType> getById(
            @Parameter(description = "类型ID", required = true)
            @PathVariable @NotNull @Min(1) Long id) {
        return R.ok(inboundTypeService.getById(id));
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询入库类型")
    public R<IPage<InboundType>> page(
            @Parameter(description = "查询条件")
            @Validated InboundTypeQuery query) {
        return R.ok(inboundTypeService.pageQuery(query));
    }

    @GetMapping("/all")
    @Operation(summary = "查询全部入库类型")
    public R<List<InboundType>> all() {
        return R.ok(inboundTypeService.listAll());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除入库类型")
    public R<Void> delete(
            @Parameter(description = "类型ID", required = true)
            @PathVariable @NotNull @Min(1) Long id) {
        inboundTypeService.removeByIdChecked(id);
        return R.optOk();
    }
}
