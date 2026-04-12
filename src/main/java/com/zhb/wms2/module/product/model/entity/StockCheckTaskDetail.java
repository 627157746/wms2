package com.zhb.wms2.module.product.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zhb.wms2.common.model.BaseModel;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 盘点任务明细。
 *
 * @author zhb
 * @since 2026/4/12
 */
@Schema(description = "盘点任务明细")
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@TableName(value = "stock_check_task_detail")
public class StockCheckTaskDetail extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;

    @TableField(value = "task_id")
    @Schema(description = "盘点任务ID")
    private Long taskId;

    @TableField(value = "product_id")
    @Schema(description = "商品ID")
    private Long productId;

    @TableField(value = "snapshot_qty")
    @Schema(description = "账面数量")
    private Long snapshotQty;

    @TableField(value = "actual_qty")
    @Schema(description = "盘点数量")
    private Long actualQty;

    @TableField(value = "diff_qty")
    @Schema(description = "差异数量")
    private Long diffQty;

    @TableField(value = "result_type")
    @Schema(description = "盘点结果：0-未盘 1-无差异 2-盘盈 3-盘亏")
    private Integer resultType;

    @TableField(value = "count_time")
    @Schema(description = "录入盘点数量时间")
    private LocalDateTime countTime;

    @TableField(value = "remark")
    @Schema(description = "备注")
    private String remark;
}
