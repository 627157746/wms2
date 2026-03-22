package com.zhb.wms2.module.inventory.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zhb.wms2.common.model.R;
import com.zhb.wms2.module.inventory.model.query.InventoryDistributionQuery;
import com.zhb.wms2.module.inventory.model.query.InventoryIoDetailQuery;
import com.zhb.wms2.module.inventory.model.vo.InventoryDetailVO;
import com.zhb.wms2.module.inventory.model.vo.InventoryDistributionGroupVO;
import com.zhb.wms2.module.inventory.model.vo.InventoryIoDetailVO;
import com.zhb.wms2.module.inventory.service.InventoryService;
import com.zhb.wms2.module.inventory.service.InventoryDetailService;
import com.zhb.wms2.module.io.service.IoOrderService;
import com.zhb.wms2.module.product.model.query.ProductQuery;
import com.zhb.wms2.module.product.model.vo.ProductPageVO;
import com.zhb.wms2.module.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/inventory")
@Tag(name = "库存", description = "库存管理")
@RequiredArgsConstructor
@Validated
public class InventoryController {

    private final ProductService productService;
    private final InventoryService inventoryService;
    private final InventoryDetailService inventoryDetailService;
    private final IoOrderService ioOrderService;

    @GetMapping("/page")
    @Operation(summary = "分页查询库存")
    public R<IPage<ProductPageVO>> page(
            @Parameter(description = "查询条件")
            @Validated ProductQuery query) {
        if (query.getIncludeZeroStock() == null) {
            query.setIncludeZeroStock(false);
        }
        return R.ok(productService.pageQuery(query));
    }

    @GetMapping("/detail/{productId}")
    @Operation(summary = "查询商品库存明细")
    public R<List<InventoryDetailVO>> detail(
            @Parameter(description = "商品ID", required = true)
            @PathVariable @NotNull @Min(1) Long productId) {
        return R.ok(inventoryDetailService.listByProductId(productId));
    }

    @GetMapping("/distribution")
    @Operation(summary = "查询库存货位分布")
    public R<List<InventoryDistributionGroupVO>> distribution(
            @Parameter(description = "查询条件")
            @Validated InventoryDistributionQuery query) {
        return R.ok(inventoryService.listDistribution(query));
    }

    @GetMapping("/ioDetail/page")
    @Operation(summary = "分页查询商品出入库明细")
    public R<IPage<InventoryIoDetailVO>> ioDetailPage(
            @Parameter(description = "查询条件")
            @Validated InventoryIoDetailQuery query) {
        return R.ok(ioOrderService.pageDetailByProductId(query));
    }
}
