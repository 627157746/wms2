package com.zhb.wms2.module.product.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 商品库存汇总 VO
 *
 * @author zhb
 * @since 2026/3/28
 */
@Data
@Accessors(chain = true)
@Schema(description = "商品库存汇总")
public class ProductStockStatVO {

    /**
     * 当前筛选条件下的库存总数。
     */
    @Schema(description = "当前筛选条件下的库存总数")
    private Long totalStockQty;
}
