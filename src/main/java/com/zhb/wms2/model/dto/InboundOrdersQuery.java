package com.zhb.wms2.model.dto;

import com.zhb.wms2.common.BaseQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 入库单查询条件
 */
@Schema(description = "入库单查询条件")
@Data
@EqualsAndHashCode(callSuper = true)
public class InboundOrdersQuery extends BaseQuery {

    /**
     * 入库单号
     */
    @Schema(description = "入库单号")
    private String inboundCode;

    /**
     * 入库类型ID
     */
    @Schema(description = "入库类型ID")
    private Integer inboundTypeId;

    /**
     * 操作员
     */
    @Schema(description = "操作员")
    private String operator;

    /**
     * 入库开始日期
     */
    @Schema(description = "入库开始日期")
    private LocalDate startTime;

    /**
     * 入库结束日期
     */
    @Schema(description = "入库结束日期")
    private LocalDate endTime;

}
