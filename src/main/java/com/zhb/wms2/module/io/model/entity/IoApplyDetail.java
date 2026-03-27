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
 * 出入库申请明细
 *
 * @author zhb
 * @since 2026/3/26
 */
@Schema(description = "出入库申请明细")
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@TableName(value = "io_apply_detail")
public class IoApplyDetail extends BaseModel implements Serializable {
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
     * 出入库申请 ID。
     */
    @TableField(value = "apply_id")
    @Schema(description = "出入库申请ID")
    private Long applyId;

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
}
