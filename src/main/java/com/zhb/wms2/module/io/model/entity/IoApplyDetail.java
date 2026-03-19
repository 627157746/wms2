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

@Schema(description = "出入库申请明细")
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "io_apply_detail")
public class IoApplyDetail extends BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;

    @TableField(value = "apply_id")
    @Schema(description = "出入库申请ID")
    private Long applyId;

    @TableField(value = "product_id")
    @Schema(description = "商品ID")
    private Long productId;

    @TableField(value = "qty")
    @Schema(description = "数量")
    private Long qty;
}
