package com.zhb.wms2.module.home.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "首页统计")
public class HomeStatVO {

    @Schema(description = "今日入库数")
    private Long todayInboundCount;

    @Schema(description = "今日出库数")
    private Long todayOutboundCount;

    @Schema(description = "库存短缺预警数")
    private Long stockShortageWarnCount;

    @Schema(description = "入库申请未审批数")
    private Long inboundApplyUnapprovedCount;

    @Schema(description = "出库申请未审批数")
    private Long outboundApplyUnapprovedCount;
}
