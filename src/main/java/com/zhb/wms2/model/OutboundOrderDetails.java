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

/**
 * @Author zhb
 * @Description
 * @Date 2025/11/19 16:18
 */
/**
 * 出库单明细表
 */
@Schema(description="出库单明细表")
@Data
@EqualsAndHashCode(callSuper=true)
@TableName(value = "outbound_order_details")
public class OutboundOrderDetails extends BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 明细ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description="明细ID")
    private Long id;

    /**
     * 出库单ID
     */
    @TableField(value = "outbound_id")
    @Schema(description="出库单ID")
    private Long outboundId;

    /**
     * 商品ID
     */
    @TableField(value = "product_id")
    @Schema(description="商品ID")
    private Long productId;

    /**
     * 物料位ID
     */
    @TableField(value = "material_location_id")
    @Schema(description="物料位ID")
    private Long materialLocationId;

    /**
     * 数量
     */
    @TableField(value = "quantity")
    @Schema(description="数量")
    private Integer quantity;

    /**
     * 备注
     */
    @TableField(value = "remarks")
    @Schema(description="备注")
    private String remarks;

}
