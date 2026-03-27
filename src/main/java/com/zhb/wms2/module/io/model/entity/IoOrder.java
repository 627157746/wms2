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
import lombok.experimental.Accessors;

/**
 * 出入库单
 *
 * @author zhb
 * @since 2026/3/26
 */
@Schema(description = "出入库单")
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@TableName(value = "io_order")
public class IoOrder extends BaseModel implements Serializable {
    /**
     * 序列化版本号。
     */
    private static final long serialVersionUID = 1L;

    /**
     * 主键 ID。
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;

    /**
     * 单号。
     */
    @TableField(value = "order_no")
    @Schema(description = "单号")
    private String orderNo;

    /**
     * 单据类型。
     */
    @TableField(value = "order_type")
    @Schema(description = "单据类型：1-入库 2-出库")
    private Integer orderType;

    /**
     * 来源出入库申请 ID。
     */
    @TableField(value = "apply_id")
    @Schema(description = "来源出入库申请ID")
    private Long applyId;

    /**
     * 业务日期。
     */
    @TableField(value = "biz_date")
    @Schema(description = "业务日期")
    private LocalDate bizDate;

    /**
     * 送货员 ID。
     */
    @TableField(value = "deliveryman_id")
    @Schema(description = "送货员ID")
    private Long deliverymanId;

    /**
     * 客户 ID，仅出库使用。
     */
    @TableField(value = "customer_id")
    @Schema(description = "客户ID，仅出库使用")
    private Long customerId;

    /**
     * 仓库 ID，仅入库使用。
     */
    @TableField(value = "warehouse_id")
    @Schema(description = "仓库ID，仅入库使用")
    private Long warehouseId;

    /**
     * 业务员 ID。
     */
    @TableField(value = "salesman_id")
    @Schema(description = "业务员ID")
    private Long salesmanId;

    /**
     * 出入库类型 ID。
     */
    @TableField(value = "io_type_id")
    @Schema(description = "出入库类型ID")
    private Long ioTypeId;

    /**
     * 备注。
     */
    @TableField(value = "remark")
    @Schema(description = "备注")
    private String remark;

    /**
     * 拣货状态，仅出库使用。
     */
    @TableField(value = "picking_status")
    @Schema(description = "拣货状态：0-未拣 1-已拣，仅出库使用")
    private Integer pickingStatus;
}
