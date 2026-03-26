package com.zhb.wms2.module.product.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 库存分布明细项 VO
 *
 * @author zhb
 * @since 2026/3/26
 */
@Data
@Accessors(chain = true)
@Schema(description = "库存分布明细项")
public class StockDistributionItemVO {

    /**
     * 商品 ID。
     */
    @Schema(description = "商品ID")
    private Long productId;

    /**
     * 商品编号。
     */
    @Schema(description = "商品编号")
    private String productCode;

    /**
     * 商品名称。
     */
    @Schema(description = "商品名称")
    private String productName;

    /**
     * 商品型号。
     */
    @Schema(description = "商品型号")
    private String model;

    /**
     * 商品单位 ID。
     */
    @Schema(description = "商品单位ID")
    private Long unitId;

    /**
     * 商品单位名称。
     */
    @Schema(description = "商品单位名称")
    private String unitName;

    /**
     * 库存数量。
     */
    @Schema(description = "库存数量")
    private Long qty;
}
