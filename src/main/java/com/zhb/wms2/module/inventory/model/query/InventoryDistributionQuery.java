package com.zhb.wms2.module.inventory.model.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "库存分布查询条件")
public class InventoryDistributionQuery {

    @Schema(description = "货位ID")
    private Long locationId;

    @Schema(description = "商品ID")
    private Long productId;
}
