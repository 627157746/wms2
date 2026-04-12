package com.zhb.wms2.module.product.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 盘点任务新增商品 DTO。
 *
 * @author zhb
 * @since 2026/4/12
 */
@Schema(description = "盘点任务新增商品DTO")
@Data
public class StockCheckTaskAddProductDTO {

    @Schema(description = "盘点任务ID")
    @NotNull(message = "盘点任务不能为空")
    @Min(value = 1, message = "盘点任务不能为空")
    private Long taskId;

    @Schema(description = "商品ID")
    @NotNull(message = "商品不能为空")
    @Min(value = 1, message = "商品不能为空")
    private Long productId;

    @Schema(description = "盘点数量")
    @Min(value = 0, message = "盘点数量不能小于0")
    private Long actualQty;

    @Schema(description = "备注")
    private String remark;
}
