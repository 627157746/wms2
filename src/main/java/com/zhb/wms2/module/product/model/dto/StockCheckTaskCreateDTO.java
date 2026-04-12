package com.zhb.wms2.module.product.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Data;

/**
 * 新增盘点任务 DTO。
 *
 * @author zhb
 * @since 2026/4/12
 */
@Schema(description = "新增盘点任务DTO")
@Data
public class StockCheckTaskCreateDTO {

    @Schema(description = "盘点日期")
    @NotNull(message = "盘点日期不能为空")
    private LocalDate taskDate;

    @Schema(description = "备注")
    private String remark;
}
