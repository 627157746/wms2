package com.zhb.wms2.module.inventory.model.vo;

import com.zhb.wms2.module.base.model.entity.ProductLocation;
import com.zhb.wms2.module.inventory.model.entity.InventoryDetail;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "库存明细结果")
public class InventoryDetailVO extends InventoryDetail {

    @Schema(description = "货位")
    private ProductLocation location;
}
