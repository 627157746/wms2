package com.zhb.wms2.module.product.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zhb.wms2.common.model.R;
import com.zhb.wms2.common.validated.Save;
import com.zhb.wms2.common.validated.Update;
import com.zhb.wms2.module.product.model.entity.Product;
import com.zhb.wms2.module.product.model.query.ProductQuery;
import com.zhb.wms2.module.product.model.query.StockDistributionQuery;
import com.zhb.wms2.module.product.model.vo.ProductPageVO;
import com.zhb.wms2.module.product.model.vo.ProductStockStatVO;
import com.zhb.wms2.module.product.model.vo.ProductStockDetailVO;
import com.zhb.wms2.module.product.model.vo.StockDistributionGroupVO;
import com.zhb.wms2.module.product.service.ProductService;
import com.zhb.wms2.module.product.service.ProductStockDetailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * 商品控制器
 *
 * @author zhb
 * @since 2026/3/26
 */
@RestController
@RequestMapping("/product")
@Tag(name = "商品", description = "商品管理")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ProductStockDetailService productStockDetailService;

    @PostMapping
    @Operation(summary = "新增商品")
    public R<Long> create(
            @Parameter(description = "商品", required = true)
            @RequestBody @Validated(Save.class) Product product) {
        productService.saveChecked(product);
        return R.ok(product.getId());
    }

    @PutMapping
    @Operation(summary = "修改商品")
    public R<Void> update(
            @Parameter(description = "商品", required = true)
            @RequestBody @Validated(Update.class) Product product) {
        productService.updateByIdChecked(product);
        return R.optOk();
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询商品")
    public R<IPage<ProductPageVO>> page(
            @Parameter(description = "查询条件")
            @Validated ProductQuery query) {
        return R.ok(productService.pageQuery(query));
    }

    @GetMapping("/stockStat")
    @Operation(summary = "查询商品库存汇总")
    public R<ProductStockStatVO> stockStat(
            @Parameter(description = "查询条件")
            @Validated ProductQuery query) {
        return R.ok(productService.getStockStat(query));
    }

    @GetMapping("/export")
    @Operation(summary = "导出商品")
    public void export(
            @Parameter(description = "查询条件")
            @Validated ProductQuery query,
            HttpServletResponse response) throws IOException {
        productService.export(query, response);
    }

    @GetMapping("/exportPdf")
    @Operation(summary = "导出商品PDF")
    public void exportPdf(
            @Parameter(description = "查询条件")
            @Validated ProductQuery query,
            HttpServletResponse response) throws IOException {
        productService.exportPdf(query, response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询商品详情")
    public R<ProductPageVO> getById(
            @Parameter(description = "商品ID", required = true)
            @PathVariable @NotNull @Min(1) Long id) {
        return R.ok(productService.getDetailById(id));
    }

    @GetMapping("/stockDetail/{productId}")
    @Operation(summary = "查询商品库存明细")
    public R<List<ProductStockDetailVO>> stockDetail(
            @Parameter(description = "商品ID", required = true)
            @PathVariable @NotNull @Min(1) Long productId) {
        return R.ok(productStockDetailService.listByProductId(productId));
    }

    @GetMapping("/distribution")
    @Operation(summary = "查询库存货位分布")
    public R<List<StockDistributionGroupVO>> distribution(
            @Parameter(description = "查询条件")
            @Validated StockDistributionQuery query) {
        return R.ok(productService.listDistribution(query));
    }

    @GetMapping("/distribution/export")
    @Operation(summary = "导出库存货位分布")
    public void exportDistribution(
            @Parameter(description = "查询条件")
            @Validated StockDistributionQuery query,
            HttpServletResponse response) throws IOException {
        productService.exportDistribution(query, response);
    }

    @GetMapping("/distribution/exportPdf")
    @Operation(summary = "导出库存货位分布PDF")
    public void exportDistributionPdf(
            @Parameter(description = "查询条件")
            @Validated StockDistributionQuery query,
            HttpServletResponse response) throws IOException {
        productService.exportDistributionPdf(query, response);
    }


    @DeleteMapping("/{id}")
    @Operation(summary = "删除商品")
    public R<Void> delete(
            @Parameter(description = "商品ID", required = true)
            @PathVariable @NotNull @Min(1) Long id) {
        productService.removeByIdChecked(id);
        return R.optOk();
    }
}
