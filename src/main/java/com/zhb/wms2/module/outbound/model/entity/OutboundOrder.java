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
 * 出库单
 */
@Schema(description="出库单")
@Data
@EqualsAndHashCode(callSuper=true)
@TableName(value = "outbound_order")
public class OutboundOrder extends BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description="主键ID")
    private Long id;

    /**
     * 出库单号，如CK000002
     */
    @TableField(value = "order_no")
    @Schema(description="出库单号，如CK000002")
    private String orderNo;

    /**
     * 来源出库申请ID，可为空
     */
    @TableField(value = "apply_id")
    @Schema(description="来源出库申请ID，可为空")
    private Long applyId;

    /**
     * 出库日期
     */
    @TableField(value = "outbound_date")
    @Schema(description="出库日期")
    private LocalDate outboundDate;

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
     * 拣货状态：0-未拣 1-已拣
     */
    @TableField(value = "picking_status")
    @Schema(description="拣货状态：0-未拣 1-已拣")
    private Integer pickingStatus;
}