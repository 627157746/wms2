package com.zhb.wms2.module.product.model.entity;

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
import lombok.experimental.Accessors;

/**
 * 盘点任务。
 *
 * @author zhb
 * @since 2026/4/12
 */
@Schema(description = "盘点任务")
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@TableName(value = "stock_check_task")
public class StockCheckTask extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;

    @TableField(value = "task_no")
    @Schema(description = "任务号")
    private String taskNo;

    @TableField(value = "task_date")
    @Schema(description = "盘点日期")
    private LocalDate taskDate;

    @TableField(value = "status")
    @Schema(description = "状态：1-盘点中 2-已盘点 3-已调整")
    private Integer status;

    @TableField(value = "finish_time")
    @Schema(description = "结束盘点时间")
    private LocalDateTime finishTime;

    @TableField(value = "profit_order_id")
    @Schema(description = "盘盈对应入库单ID")
    private Long profitOrderId;

    @TableField(value = "profit_order_no")
    @Schema(description = "盘盈对应入库单号")
    private String profitOrderNo;

    @TableField(value = "loss_order_id")
    @Schema(description = "盘亏对应出库单ID")
    private Long lossOrderId;

    @TableField(value = "loss_order_no")
    @Schema(description = "盘亏对应出库单号")
    private String lossOrderNo;

    @TableField(value = "remark")
    @Schema(description = "备注")
    private String remark;
}
