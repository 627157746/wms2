package com.zhb.wms2.module.product.model.vo;

import com.zhb.wms2.module.product.model.entity.StockCheckTask;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 盘点任务分页结果。
 *
 * @author zhb
 * @since 2026/4/12
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@Schema(description = "盘点任务分页结果")
public class StockCheckTaskPageVO extends StockCheckTask {

    @Schema(description = "状态名称")
    private String statusName;

    @Schema(description = "应盘数")
    private Long totalCount;

    @Schema(description = "已盘数")
    private Long countedCount;

    @Schema(description = "未盘数")
    private Long uncountedCount;

    @Schema(description = "盘盈数")
    private Long profitCount;

    @Schema(description = "盘亏数")
    private Long lossCount;
}
