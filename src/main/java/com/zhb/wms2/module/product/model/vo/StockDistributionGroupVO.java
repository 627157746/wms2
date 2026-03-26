package com.zhb.wms2.module.product.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 库存分布分组结果 VO
 *
 * @author zhb
 * @since 2026/3/26
 */
@Data
@Accessors(chain = true)
@Schema(description = "库存分布分组结果")
public class StockDistributionGroupVO {

    /**
     * 货位 ID。
     */
    @Schema(description = "货位ID")
    private Long locationId;

    /**
     * 货位编码。
     */
    @Schema(description = "货位编码")
    private String locationCode;

    /**
     * 分组库存合计。
     */
    @Schema(description = "分组库存合计")
    private Long totalQty;

    /**
     * 货位下商品列表。
     */
    @Schema(description = "货位下商品列表")
    private List<StockDistributionItemVO> itemList;
}
