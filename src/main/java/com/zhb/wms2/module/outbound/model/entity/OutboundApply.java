package com.zhb.wms2.module.outbound.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zhb.wms2.common.model.BaseModel;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author zhb
 * @Description 
 * @Date 2026/3/17 19:38
 */
/**
 * 出库申请
 */
@Schema(description="出库申请")
@Data
@EqualsAndHashCode(callSuper=true)
@TableName(value = "outbound_apply")
public class OutboundApply extends BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description="主键ID")
    private Long id;

    /**
     * 申请编号，如CS000001
     */
    @TableField(value = "apply_no")
    @Schema(description="申请编号，如CS000001")
    private String applyNo;

    /**
     * 申请日期
     */
    @TableField(value = "apply_date")
    @Schema(description="申请日期")
    private LocalDate applyDate;

    /**
     * 申请人
     */
    @TableField(value = "applicant_name")
    @Schema(description="申请人")
    private String applicantName;

    /**
     * 送货员ID
     */
    @TableField(value = "deliveryman_id")
    @Schema(description="送货员ID")
    private Long deliverymanId;

    /**
     * 客户ID
     */
    @TableField(value = "customer_id")
    @Schema(description="客户ID")
    private Long customerId;

    /**
     * 出库类型ID
     */
    @TableField(value = "outbound_type_id")
    @Schema(description="出库类型ID")
    private Long outboundTypeId;

    /**
     * 备注
     */
    @TableField(value = "remark")
    @Schema(description="备注")
    private String remark;

    /**
     * 是否审批：0-未审批 1-已审批
     */
    @TableField(value = "approve_status")
    @Schema(description="是否审批：0-未审批 1-已审批")
    private Integer approveStatus;

    /**
     * 是否出库：0-未出库 1-已出库
     */
    @TableField(value = "outbound_status")
    @Schema(description="是否出库：0-未出库 1-已出库")
    private Integer outboundStatus;

    @TableField(value = "approved_time")
    @Schema(description="")
    private LocalDateTime approvedTime;
}