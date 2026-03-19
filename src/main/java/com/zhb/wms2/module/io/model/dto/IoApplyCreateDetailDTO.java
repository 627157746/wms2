package com.zhb.wms2.module.io.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "出入库申请明细新增DTO")
@Data
public class IoApplyCreateDetailDTO {

    @Schema(description = "商品ID")
    @NotNull(message = "商品不能为空")
    @Min(value = 1, message = "商品不能为空")
    private Long productId;

    @Schema(description = "数量")
    @NotNull(message = "数量不能为空")
    @Min(value = 1, message = "数量必须大于0")
    private Long qty;
}
