package com.zhb.wms2.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

/**
 * 入库单明细项DTO
 */
@Schema(description = "入库单明细项DTO")
@Data
public class InboundOrderDetailItemDTO {

    /**
     * 商品ID
     */
    @Schema(description = "商品ID")
    @NotNull(message = "商品ID不能为空")
    @Min(value = 1, message = "商品ID必须大于0")
    private Long productId;

    /**
     * 数量
     */
    @Schema(description = "数量")
    @NotNull(message = "数量不能为空")
    @Min(value = 1, message = "数量必须大于0")
    private Integer quantity;

    /**
     * 物料位ID
     */
    @Schema(description = "物料位ID")
    @NotNull(message = "物料位ID不能为空")
    @Min(value = 1, message = "物料位ID必须大于0")
    private Long materialLocationId;

    /**
     * 备注
     */
    @Schema(description = "备注")
    @Size(max = 500, message = "备注长度不能超过500个字符")
    private String remarks;
}
