package com.zhb.wms2.module.inventory.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zhb.wms2.common.model.BaseModel;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "转货位记录")
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("location_transfer")
public class LocationTransfer extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;

    @TableField("product_id")
    @Schema(description = "商品ID")
    private Long productId;

    @TableField("from_location_id")
    @Schema(description = "原货位ID")
    private Long fromLocationId;

    @TableField("to_location_id")
    @Schema(description = "转移货位ID")
    private Long toLocationId;

    @TableField("transfer_qty")
    @Schema(description = "转移数量")
    private Long transferQty;

    @TableField("remark")
    @Schema(description = "备注")
    private String remark;
}
