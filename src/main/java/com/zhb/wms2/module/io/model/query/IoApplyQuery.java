package com.zhb.wms2.module.io.model.query;

import com.zhb.wms2.common.model.BaseQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 出入库申请分页查询条件查询对象
 *
 * @author zhb
 * @since 2026/3/26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "出入库申请分页查询条件")
public class IoApplyQuery extends BaseQuery {

    /**
     * 申请单号。
     */
    @Schema(description = "申请单号")
    private String applyNo;

    /**
     * 单据类型。
     */
    @Schema(description = "单据类型：1-入库 2-出库")
    private Integer orderType;

    /**
     * 申请日期开始。
     */
    @Schema(description = "申请日期开始")
    private LocalDate applyDateStart;

    /**
     * 申请日期结束。
     */
    @Schema(description = "申请日期结束")
    private LocalDate applyDateEnd;

    /**
     * 送货员 ID。
     */
    @Schema(description = "送货员ID")
    private Long deliverymanId;

    /**
     * 客户 ID。
     */
    @Schema(description = "客户ID")
    private Long customerId;

    /**
     * 业务员 ID。
     */
    @Schema(description = "业务员ID")
    private Long salesmanId;

    /**
     * 出入库类型 ID。
     */
    @Schema(description = "出入库类型ID")
    private Long ioTypeId;

    /**
     * 审批状态。
     */
    @Schema(description = "审批状态：0-未审批 1-已审批")
    private Integer approveStatus;

    /**
     * 出入库状态。
     */
    @Schema(description = "出入库状态：0-未执行 1-已执行")
    private Integer ioStatus;
}
