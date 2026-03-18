package com.zhb.wms2.module.inbound.model.entity;

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
 * @Date 2026/3/17 19:36
 */
/**
 * 入库申请
 */
@Schema(description="入库申请")
@Data
@EqualsAndHashCode(callSuper=true)
@TableName(value = "inbound_apply")
public class InboundApply extends BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description="主键ID")
    private Long id;

    /**
     * 申请编号，如RS000003
     */
    @TableField(value = "apply_no")
    @Schema(description="申请编号，如RS000003")
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
     * 入库类型ID
     */
    @TableField(value = "inbound_type_id")
    @Schema(description="入库类型ID")
    private Long inboundTypeId;

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
     * 是否入库：0-未入库 1-已入库
     */
    @TableField(value = "inbound_status")
    @Schema(description="是否入库：0-未入库 1-已入库")
    private Integer inboundStatus;

    @TableField(value = "approved_time")
    @Schema(description="")
    private LocalDateTime approvedTime;
}