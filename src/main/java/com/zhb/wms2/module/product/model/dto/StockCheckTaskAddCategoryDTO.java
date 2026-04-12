package com.zhb.wms2.module.product.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 盘点任务按分类加商品 DTO。
 *
 * @author zhb
 * @since 2026/4/12
 */
@Schema(description = "盘点任务按分类加商品DTO")
@Data
public class StockCheckTaskAddCategoryDTO {

    /**
     * 盘点任务ID。
     */
    @Schema(description = "盘点任务ID")
    @NotNull(message = "盘点任务不能为空")
    @Min(value = 1, message = "盘点任务不能为空")
    private Long taskId;

    /**
     * 商品分类ID。
     */
    @Schema(description = "商品分类ID")
    @NotNull(message = "商品分类不能为空")
    @Min(value = 1, message = "商品分类不能为空")
    private Long categoryId;
}
