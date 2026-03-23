package com.zhb.wms2.module.product.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "库存分布分组结果")
public class StockDistributionGroupVO {

    @Schema(description = "货位ID")
    private Long locationId;

    @Schema(description = "货位编码")
    private String locationCode;

    @Schema(description = "分组库存合计")
    private Long totalQty;

    @Schema(description = "货位下商品列表")
    private List<StockDistributionItemVO> itemList;
}
