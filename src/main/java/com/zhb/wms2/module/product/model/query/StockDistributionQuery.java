package com.zhb.wms2.module.product.model.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 库存分布查询条件查询对象
 *
 * @author zhb
 * @since 2026/3/26
 */
@Data
@Schema(description = "库存分布查询条件")
public class StockDistributionQuery {

    /**
     * 货位 ID。
     */
    @Schema(description = "货位ID")
    private Long locationId;

    /**
     * 商品 ID。
     */
    @Schema(description = "商品ID")
    private Long productId;
}
