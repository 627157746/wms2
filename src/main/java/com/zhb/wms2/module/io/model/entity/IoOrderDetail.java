package com.zhb.wms2.module.io.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zhb.wms2.common.model.BaseModel;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 出入库记录
 *
 * @author zhb
 * @since 2026/3/26
 */
@Schema(description = "出入库记录")
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@TableName(value = "io_order_detail")
public class IoOrderDetail extends BaseModel implements Serializable {
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
     * 出入库单 ID。
     */
    @TableField(value = "order_id")
    @Schema(description = "出入库单ID")
    private Long orderId;

    /**
     * 单据类型。
     */
    @TableField(value = "order_type")
    @Schema(description = "单据类型：1-入库 2-出库")
    private Integer orderType;

    /**
     * 商品 ID。
     */
    @TableField(value = "product_id")
    @Schema(description = "商品ID")
    private Long productId;

    /**
     * 数量。
     */
    @TableField(value = "qty")
    @Schema(description = "数量")
    private Long qty;

    /**
     * 货位 ID。
     */
    @TableField(value = "location_id")
    @Schema(description = "货位ID")
    private Long locationId;

    /**
     * 备注。
     */
    @TableField(value = "remark")
    @Schema(description = "备注")
    private String remark;

    /**
     * 已拣数量，仅出库使用。
     */
    @TableField(value = "picked_qty")
    @Schema(description = "已拣数量，仅出库使用")
    private Long pickedQty;
}
