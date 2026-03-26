package com.zhb.wms2.module.home.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 首页统计 VO
 *
 * @author zhb
 * @since 2026/3/26
 */
@Data
@Schema(description = "首页统计")
public class HomeStatVO {

    /**
     * 今日入库数。
     */
    @Schema(description = "今日入库数")
    private Long todayInboundCount;

    /**
     * 今日出库数。
     */
    @Schema(description = "今日出库数")
    private Long todayOutboundCount;

    /**
     * 库存短缺预警数。
     */
    @Schema(description = "库存短缺预警数")
    private Long stockShortageWarnCount;

    /**
     * 入库申请未审批数。
     */
    @Schema(description = "入库申请未审批数")
    private Long inboundApplyUnapprovedCount;

    /**
     * 出库申请未审批数。
     */
    @Schema(description = "出库申请未审批数")
    private Long outboundApplyUnapprovedCount;
}
