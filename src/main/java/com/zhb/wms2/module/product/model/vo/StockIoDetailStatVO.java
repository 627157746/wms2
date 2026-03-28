package com.zhb.wms2.module.product.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 商品出入库明细统计 VO
 *
 * @author zhb
 * @since 2026/3/28
 */
@Data
@Accessors(chain = true)
@Schema(description = "商品出入库明细统计")
public class StockIoDetailStatVO {

    /**
     * 当前条件下的入库总数量。
     */
    @Schema(description = "当前条件下的入库总数量")
    private Long inboundQty;

    /**
     * 当前条件下的出库总数量。
     */
    @Schema(description = "当前条件下的出库总数量")
    private Long outboundQty;
}
