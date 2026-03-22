package com.zhb.wms2.module.inventory.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "发起转货位DTO")
@Data
public class LocationTransferCreateDTO {

    @Schema(description = "商品ID")
    @NotNull(message = "商品不能为空")
    @Min(value = 1, message = "商品不能为空")
    private Long productId;

    @Schema(description = "原货位ID，0表示无货位")
    @NotNull(message = "原货位不能为空")
    @Min(value = 0, message = "原货位不正确")
    private Long fromLocationId;

    @Schema(description = "转移货位ID，0表示无货位")
    @NotNull(message = "转移货位不能为空")
    @Min(value = 0, message = "转移货位不正确")
    private Long toLocationId;

    @Schema(description = "转移数量")
    @NotNull(message = "转移数量不能为空")
    @Min(value = 1, message = "转移数量必须大于0")
    private Long transferQty;

    @Schema(description = "备注")
    private String remark;
}
