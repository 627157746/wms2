package com.zhb.wms2.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 出库单主单VO
 * 包含明细列表
 */
@Schema(description = "出库单主单VO")
@Data
public class OutboundOrdersVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 出库单ID
     */
    @Schema(description = "出库单ID")
    private Long id;

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
     * 出库类型名称
     */
    @Schema(description = "出库类型名称")
    private String outboundTypeName;

    /**
     * 出库日期
     */
    @Schema(description = "出库日期")
    private LocalDate outboundDate;

    /**
     * 总数量
     */
    @Schema(description = "总数量")
    private Integer totalQuantity;

    /**
     * 操作员
     */
    @Schema(description = "操作员")
    private String operator;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remarks;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    /**
     * 创建人
     */
    @Schema(description = "创建人")
    private String createBy;

    /**
     * 更新人
     */
    @Schema(description = "更新人")
    private String updateBy;

    /**
     * 出库单明细列表
     */
    @Schema(description = "出库单明细列表")
    private List<OutboundOrderDetailsVO> details;
}

