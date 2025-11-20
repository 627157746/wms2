package com.zhb.wms2.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zhb.wms2.common.BaseModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @Author zhb
 * @Description
 * @Date 2025/11/19 16:19
 */
/**
 * 出库单主表
 */
@Schema(description="出库单主表")
@Data
@EqualsAndHashCode(callSuper=true)
@TableName(value = "outbound_orders")
public class OutboundOrders extends BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 出库单ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description="出库单ID")
    private Long id;

    /**
     * 出库单号
     */
    @TableField(value = "outbound_code")
    @Schema(description="出库单号")
    private String outboundCode;

    /**
     * 出库类型ID
     */
    @TableField(value = "outbound_type_id")
    @Schema(description="出库类型ID")
    private Integer outboundTypeId;

    /**
     * 出库日期
     */
    @TableField(value = "outbound_date")
    @Schema(description="出库日期")
    private LocalDate outboundDate;

    /**
     * 总数量
     */
    @TableField(value = "total_quantity")
    @Schema(description="总数量")
    private Integer totalQuantity;

    /**
     * 操作员
     */
    @TableField(value = "`operator`")
    @Schema(description="操作员")
    private String operator;

    /**
     * 备注
     */
    @TableField(value = "remarks")
    @Schema(description="备注")
    private String remarks;

}
