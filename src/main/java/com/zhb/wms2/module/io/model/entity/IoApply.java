package com.zhb.wms2.module.io.model.entity;

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

@Schema(description = "出入库申请")
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "io_apply")
public class IoApply extends BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;

    @TableField(value = "apply_no")
    @Schema(description = "申请单号")
    private String applyNo;

    @TableField(value = "order_type")
    @Schema(description = "单据类型：1-入库 2-出库")
    private Integer orderType;

    @TableField(value = "apply_date")
    @Schema(description = "申请日期")
    private LocalDate applyDate;

    @TableField(value = "deliveryman_id")
    @Schema(description = "送货员ID")
    private Long deliverymanId;

    @TableField(value = "customer_id")
    @Schema(description = "客户ID，仅出库使用")
    private Long customerId;

    @TableField(value = "io_type_id")
    @Schema(description = "出入库类型ID")
    private Long ioTypeId;

    @TableField(value = "remark")
    @Schema(description = "备注")
    private String remark;

    @TableField(value = "approve_status")
    @Schema(description = "审批状态：0-未审批 1-已审批")
    private Integer approveStatus;

    @TableField(value = "io_status")
    @Schema(description = "出入库状态：0-未执行 1-已执行")
    private Integer ioStatus;

    @TableField(value = "approved_time")
    @Schema(description = "审批时间")
    private LocalDateTime approvedTime;
}
