package com.zhb.wms2.module.product.model.vo;

import com.zhb.wms2.module.product.model.entity.LocationTransfer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "转货位分页结果")
public class LocationTransferPageVO extends LocationTransfer {

    @Schema(description = "商品名称")
    private String productName;

    @Schema(description = "商品编码")
    private String productCode;

    @Schema(description = "原货位编码")
    private String fromLocationCode;

    @Schema(description = "转移货位编码")
    private String toLocationCode;
}
