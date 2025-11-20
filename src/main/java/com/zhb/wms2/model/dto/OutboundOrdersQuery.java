package com.zhb.wms2.model.dto;

import com.zhb.wms2.common.BaseQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 出库单查询条件
 */
@Schema(description = "出库单查询条件")
@Data
@EqualsAndHashCode(callSuper = true)
public class OutboundOrdersQuery extends BaseQuery {

    /**
     * 出库单号
     */
    @Schema(description = "出库单号")
    private String outboundCode;

    /**
     * 出库类型ID
     */
    @Schema(description = "出库类型ID")
    private Integer outboundTypeId;

    /**
     * 操作员
     */
    @Schema(description = "操作员")
    private String operator;

    /**
     * 出库开始日期
     */
    @Schema(description = "出库开始日期")
    private LocalDate startTime;

    /**
     * 出库结束日期
     */
    @Schema(description = "出库结束日期")
    private LocalDate endTime;

}

