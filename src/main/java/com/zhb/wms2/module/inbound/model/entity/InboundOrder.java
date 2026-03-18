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
 * @Date 2026/3/17 19:37
 */
/**
 * 入库单
 */
@Schema(description="入库单")
@Data
@EqualsAndHashCode(callSuper=true)
@TableName(value = "inbound_order")
public class InboundOrder extends BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description="主键ID")
    private Long id;

    /**
     * 入库单号，如RK000001
     */
    @TableField(value = "order_no")
    @Schema(description="入库单号，如RK000001")
    private String orderNo;

    /**
     * 来源入库申请ID，可为空
     */
    @TableField(value = "apply_id")
    @Schema(description="来源入库申请ID，可为空")
    private Long applyId;

    /**
     * 入库日期
     */
    @TableField(value = "inbound_date")
    @Schema(description="入库日期")
    private LocalDate inboundDate;

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
}