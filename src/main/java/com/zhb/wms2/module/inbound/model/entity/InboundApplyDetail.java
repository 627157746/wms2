package com.zhb.wms2.module.inbound.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zhb.wms2.common.model.BaseModel;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author zhb
 * @Description 
 * @Date 2026/3/17 19:37
 */
/**
 * 入库申请明细
 */
@Schema(description="入库申请明细")
@Data
@EqualsAndHashCode(callSuper=true)
@TableName(value = "inbound_apply_detail")
public class InboundApplyDetail extends BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description="主键ID")
    private Long id;

    /**
     * 入库申请ID
     */
    @TableField(value = "apply_id")
    @Schema(description="入库申请ID")
    private Long applyId;

    /**
     * 商品ID
     */
    @TableField(value = "product_id")
    @Schema(description="商品ID")
    private Long productId;

    /**
     * 数量
     */
    @TableField(value = "qty")
    @Schema(description="数量")
    private BigDecimal qty;
}