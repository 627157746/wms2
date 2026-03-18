package com.zhb.wms2.module.io.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zhb.wms2.common.model.BaseModel;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "出入库记录")
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "io_order_detail")
public class IoOrderDetail extends BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;

    @TableField(value = "order_id")
    @Schema(description = "出入库单ID")
    private Long orderId;

    @TableField(value = "order_type")
    @Schema(description = "单据类型：1-入库 2-出库")
    private Integer orderType;

    @TableField(value = "product_id")
    @Schema(description = "商品ID")
    private Long productId;

    @TableField(value = "qty")
    @Schema(description = "数量")
    private BigDecimal qty;

    @TableField(value = "location_id")
    @Schema(description = "货位ID")
    private Long locationId;

    @TableField(value = "picked_qty")
    @Schema(description = "已拣数量，仅出库使用")
    private BigDecimal pickedQty;
}
