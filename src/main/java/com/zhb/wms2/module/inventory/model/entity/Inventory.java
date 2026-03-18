package com.zhb.wms2.module.inventory.model.entity;

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
 * @Date 2026/3/17 19:38
 */
/**
 * 库存主表
 */
@Schema(description="库存主表")
@Data
@EqualsAndHashCode(callSuper=true)
@TableName(value = "inventory")
public class Inventory extends BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description="主键ID")
    private Long id;

    /**
     * 商品ID
     */
    @TableField(value = "product_id")
    @Schema(description="商品ID")
    private Long productId;

    /**
     * 总库存数
     */
    @TableField(value = "total_qty")
    @Schema(description="总库存数")
    private BigDecimal totalQty;

    /**
     * 货位IDs，逗号分隔
     */
    @TableField(value = "location_ids")
    @Schema(description="货位IDs，逗号分隔")
    private String locationIds;
}