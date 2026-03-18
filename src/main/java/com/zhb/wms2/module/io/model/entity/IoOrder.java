package com.zhb.wms2.module.io.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zhb.wms2.common.model.BaseModel;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.LocalDate;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "出入库单")
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "io_order")
public class IoOrder extends BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;

    @TableField(value = "order_no")
    @Schema(description = "单号")
    private String orderNo;

    @TableField(value = "order_type")
    @Schema(description = "单据类型：1-入库 2-出库")
    private Integer orderType;

    @TableField(value = "apply_id")
    @Schema(description = "来源申请ID")
    private Long applyId;

    @TableField(value = "biz_date")
    @Schema(description = "业务日期")
    private LocalDate bizDate;

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

    @TableField(value = "picking_status")
    @Schema(description = "拣货状态：0-未拣 1-已拣，仅出库使用")
    private Integer pickingStatus;
}
