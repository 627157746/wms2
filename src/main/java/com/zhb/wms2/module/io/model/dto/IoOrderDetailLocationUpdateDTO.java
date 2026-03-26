package com.zhb.wms2.module.io.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 出入库单明细货位修改DTO DTO
 *
 * @author zhb
 * @since 2026/3/26
 */
@Schema(description = "出入库单明细货位修改DTO")
@Data
public class IoOrderDetailLocationUpdateDTO {

    /**
     * 明细 ID。
     */
    @Schema(description = "明细ID")
    @NotNull(message = "明细ID不能为空")
    @Min(value = 1, message = "明细ID不能为空")
    private Long detailId;

    /**
     * 货位 ID。
     */
    @Schema(description = "货位ID")
    @NotNull(message = "货位不能为空")
    @Min(value = 1, message = "货位不能为空")
    private Long locationId;
}
