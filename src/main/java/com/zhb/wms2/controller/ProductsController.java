package com.zhb.wms2.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zhb.wms2.common.R;
import com.zhb.wms2.model.Products;
import com.zhb.wms2.model.dto.ProductsQuery;
import com.zhb.wms2.service.ProductsService;
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
@RequestMapping("/products")
@Tag(name = "商品信息管理", description = "商品信息相关接口")
@RequiredArgsConstructor
@Validated
public class ProductsController {

    private final ProductsService productsService;

    @PostMapping
    @Operation(summary = "添加商品信息")
    public R<Long> save(
            @Parameter(description = "商品信息", required = true)
            @RequestBody @Validated Products products) {
        Long id = productsService.addProducts(products);
        return R.ok(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "修改商品信息")
    public R<Void> update(
            @Parameter(description = "商品ID", required = true)
            @PathVariable @NotNull @Min(1) Long id,
            @Parameter(description = "商品信息", required = true)
            @RequestBody @Validated Products products) {
        products.setId(id);
        productsService.updateProducts(products);
        return R.optOk();
    }

    @GetMapping
    @Operation(summary = "分页查询商品信息")
    public R<IPage<Products>> page(
            @Parameter(description = "查询条件")
            @Validated ProductsQuery query) {
        IPage<Products> result = productsService.queryPage(query);
        return R.ok(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询商品详情")
    public R<Products> getById(
            @Parameter(description = "商品ID", required = true)
            @PathVariable @NotNull @Min(1) Long id) {
        Products products = productsService.getById(id);
        return R.ok(products);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除商品信息")
    public R<Void> delete(
            @Parameter(description = "商品ID", required = true)
            @PathVariable @NotNull @Min(1) Long id) {
        productsService.removeById(id);
        return R.optOk();
    }

    @DeleteMapping("/batch")
    @Operation(summary = "批量删除商品信息")
    public R<Void> batchDelete(
            @Parameter(description = "商品ID列表", required = true)
            @RequestBody @NotEmpty List<@NotNull @Min(1) Long> ids) {
        productsService.removeByIds(ids);
        return R.optOk();
    }
}