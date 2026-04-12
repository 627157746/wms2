package com.zhb.wms2.module.product.model.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 盘点任务统计结果。
 *
 * @author zhb
 * @since 2026/4/12
 */
@Data
@Accessors(chain = true)
public class StockCheckTaskStatVO {

    /**
     * 应盘数。
     */
    private long totalCount;

    /**
     * 已盘数。
     */
    private long countedCount;

    /**
     * 未盘数。
     */
    private long uncountedCount;

    /**
     * 盘盈数。
     */
    private long profitCount;

    /**
     * 盘亏数。
     */
    private long lossCount;

    /**
     * 累加单条盘点明细的统计结果。
     */
    public void accept(StockCheckTaskDetailVO detail) {
        totalCount++;
        Integer resultType = detail.getResultType();
        if (resultType == null || resultType == 0 || detail.getActualQty() == null) {
            uncountedCount++;
            return;
        }
        countedCount++;
        if (resultType == 2) {
            profitCount++;
            return;
        }
        if (resultType == 3) {
            lossCount++;
        }
    }
}
