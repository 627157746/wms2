package com.zhb.wms2.module.product.model.query;

import com.zhb.wms2.common.model.BaseQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.time.LocalDate;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 盘点任务分页查询条件。
 *
 * @author zhb
 * @since 2026/4/12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "盘点任务分页查询条件")
public class StockCheckTaskQuery extends BaseQuery {

    @Schema(description = "任务号")
    private String taskNo;

    @Schema(description = "状态：1-盘点中 2-已盘点 3-已调整")
    @Min(value = 1, message = "状态不正确")
    @Max(value = 3, message = "状态不正确")
    private Integer status;

    @Schema(description = "盘点日期开始")
    private LocalDate taskDateStart;

    @Schema(description = "盘点日期结束")
    private LocalDate taskDateEnd;
}
