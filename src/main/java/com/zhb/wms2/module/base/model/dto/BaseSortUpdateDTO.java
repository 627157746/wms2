package com.zhb.wms2.module.base.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 基础资料排序更新 DTO
 *
 * @author zhb
 * @since 2026/4/6
 */
@Schema(description = "基础资料排序更新DTO")
@Data
public class BaseSortUpdateDTO {

    /**
     * 记录 ID。
     */
    @Schema(description = "记录ID")
    @NotNull(message = "记录ID不能为空")
    @Min(value = 1, message = "记录ID不能为空")
    private Long id;

    /**
     * 排序值。
     */
    @Schema(description = "排序值")
    @NotNull(message = "排序值不能为空")
    private Integer sortOrder;
}
