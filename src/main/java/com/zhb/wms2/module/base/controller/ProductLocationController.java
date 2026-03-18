package com.zhb.wms2.module.base.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zhb.wms2.common.model.R;
import com.zhb.wms2.common.validated.Save;
import com.zhb.wms2.common.validated.Update;
import com.zhb.wms2.module.base.model.entity.ProductLocation;
import com.zhb.wms2.module.base.model.query.ProductLocationQuery;
import com.zhb.wms2.module.base.service.ProductLocationService;
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
@RequestMapping("/base/product-locations")
@Tag(name = "商品货位", description = "商品货位管理")
@RequiredArgsConstructor
@Validated
public class ProductLocationController {

    private final ProductLocationService productLocationService;

    @PostMapping
    @Operation(summary = "新增商品货位")
    public R<Long> save(
            @Parameter(description = "商品货位", required = true)
            @RequestBody @Validated(Save.class) ProductLocation location) {
        productLocationService.save(location);
        return R.ok(location.getId());
    }

    @PutMapping
    @Operation(summary = "修改商品货位")
    public R<Void> update(
            @Parameter(description = "商品货位", required = true)
            @RequestBody @Validated(Update.class) ProductLocation location) {
        productLocationService.updateById(location);
        return R.optOk();
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询商品货位详情")
    public R<ProductLocation> getById(
            @Parameter(description = "货位ID", required = true)
            @PathVariable @NotNull @Min(1) Long id) {
        return R.ok(productLocationService.getById(id));
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询商品货位")
    public R<IPage<ProductLocation>> page(
            @Parameter(description = "查询条件")
            @Validated ProductLocationQuery query) {
        return R.ok(productLocationService.pageQuery(query));
    }

    @GetMapping("/all")
    @Operation(summary = "查询全部商品货位")
    public R<List<ProductLocation>> all() {
        return R.ok(productLocationService.listAll());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除商品货位")
    public R<Void> delete(
            @Parameter(description = "货位ID", required = true)
            @PathVariable @NotNull @Min(1) Long id) {
        productLocationService.removeByIdChecked(id);
        return R.optOk();
    }
}
