package com.zhb.wms2.module.product.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 盘点任务详情。
 *
 * @author zhb
 * @since 2026/4/12
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@Schema(description = "盘点任务详情")
public class StockCheckTaskVO extends StockCheckTaskPageVO {

    @Schema(description = "盘点任务明细")
    private List<StockCheckTaskDetailVO> detailList;
}
