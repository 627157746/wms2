package com.zhb.wms2.module.base.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zhb.wms2.common.model.R;
import com.zhb.wms2.common.validated.Save;
import com.zhb.wms2.common.validated.Update;
import com.zhb.wms2.module.base.model.entity.ProductUnit;
import com.zhb.wms2.module.base.model.query.ProductUnitQuery;
import com.zhb.wms2.module.base.service.ProductUnitService;
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
@RequestMapping("/base/productUnit")
@Tag(name = "商品单位", description = "商品单位管理")
@RequiredArgsConstructor
@Validated
public class ProductUnitController {

    private final ProductUnitService productUnitService;

    @PostMapping
    @Operation(summary = "新增商品单位")
    public R<Long> create(
            @Parameter(description = "商品单位", required = true)
            @RequestBody @Validated(Save.class) ProductUnit unit) {
        productUnitService.saveChecked(unit);
        return R.ok(unit.getId());
    }

    @PutMapping
    @Operation(summary = "修改商品单位")
    public R<Void> update(
            @Parameter(description = "商品单位", required = true)
            @RequestBody @Validated(Update.class) ProductUnit unit) {
        productUnitService.updateByIdChecked(unit);
        return R.optOk();
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询商品单位详情")
    public R<ProductUnit> getById(
            @Parameter(description = "单位ID", required = true)
            @PathVariable @NotNull @Min(1) Long id) {
        return R.ok(productUnitService.getById(id));
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询商品单位")
    public R<IPage<ProductUnit>> page(
            @Parameter(description = "查询条件")
            @Validated ProductUnitQuery query) {
        return R.ok(productUnitService.pageQuery(query));
    }

    @GetMapping("/all")
    @Operation(summary = "查询全部商品单位")
    public R<List<ProductUnit>> all() {
        return R.ok(productUnitService.listAll());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除商品单位")
    public R<Void> delete(
            @Parameter(description = "单位ID", required = true)
            @PathVariable @NotNull @Min(1) Long id) {
        productUnitService.removeByIdChecked(id);
        return R.optOk();
    }
}
