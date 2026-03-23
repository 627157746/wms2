package com.zhb.wms2.module.product.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "库存分布明细项")
public class StockDistributionItemVO {

    @Schema(description = "商品ID")
    private Long productId;

    @Schema(description = "商品编号")
    private String productCode;

    @Schema(description = "商品名称")
    private String productName;

    @Schema(description = "商品单位ID")
    private Long unitId;

    @Schema(description = "商品单位名称")
    private String unitName;

    @Schema(description = "库存数量")
    private Long qty;
}
