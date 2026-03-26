package com.zhb.wms2.module.io.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "出入库单明细DTO")
@Data
public class IoOrderDetailDTO {

    @Schema(description = "商品ID")
    @NotNull(message = "商品不能为空")
    @Min(value = 1, message = "商品不能为空")
    private Long productId;

    @Schema(description = "数量")
    @NotNull(message = "数量不能为空")
    @Min(value = 1, message = "数量必须大于0")
    private Long qty;

    @Schema(description = "货位ID")
    @NotNull(message = "货位不能为空")
    @Min(value = 1, message = "货位不能为空")
    private Long locationId;

    @Schema(description = "备注")
    private String remark;
}
