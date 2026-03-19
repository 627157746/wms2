package com.zhb.wms2.module.product.model.vo;

import com.zhb.wms2.module.product.model.entity.Product;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "商品分页结果")
public class ProductPageVO extends Product {

    @Schema(description = "商品单位名称")
    private String productUnitName;

    @Schema(description = "商品分类名称")
    private String productCategoryName;

    @Schema(description = "期初库存货位编码")
    private String initialStockLocationCode;

    @Schema(description = "当前总库存")
    private Long totalStockQty;

    @Schema(description = "库存货位编码列表")
    private List<String> locationCodes;
}
