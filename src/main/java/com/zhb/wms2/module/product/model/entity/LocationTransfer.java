package com.zhb.wms2.module.product.model.entity;

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
 * 转货位记录
 *
 * @author zhb
 * @since 2026/3/26
 */
@Schema(description = "转货位记录")
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@TableName("location_transfer")
public class LocationTransfer extends BaseModel implements Serializable {

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
     * 商品 ID。
     */
    @TableField("product_id")
    @Schema(description = "商品ID")
    private Long productId;

    /**
     * 原货位 ID。
     */
    @TableField("from_location_id")
    @Schema(description = "原货位ID")
    private Long fromLocationId;

    /**
     * 目标货位 ID。
     */
    @TableField("to_location_id")
    @Schema(description = "转移货位ID")
    private Long toLocationId;

    /**
     * 转移数量。
     */
    @TableField("transfer_qty")
    @Schema(description = "转移数量")
    private Long transferQty;

    /**
     * 备注。
     */
    @TableField("remark")
    @Schema(description = "备注")
    private String remark;
}
