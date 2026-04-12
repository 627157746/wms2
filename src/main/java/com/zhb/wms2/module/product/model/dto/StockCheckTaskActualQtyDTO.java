package com.zhb.wms2.module.product.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 更新盘点数量 DTO。
 *
 * @author zhb
 * @since 2026/4/12
 */
@Schema(description = "更新盘点数量DTO")
@Data
public class StockCheckTaskActualQtyDTO {

    @Schema(description = "盘点明细ID")
    @NotNull(message = "盘点明细不能为空")
    @Min(value = 1, message = "盘点明细不能为空")
    private Long detailId;

    @Schema(description = "盘点数量")
    @NotNull(message = "盘点数量不能为空")
    @Min(value = 0, message = "盘点数量不能小于0")
    private Long actualQty;

    @Schema(description = "备注")
    private String remark;
}
