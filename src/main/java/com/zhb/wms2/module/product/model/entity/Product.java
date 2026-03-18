package com.zhb.wms2.module.product.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zhb.wms2.common.model.BaseModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author zhb
 * @Description
 * @Date 2026/3/17 19:31
 */
/**
 * 商品
 */
@Schema(description="商品")
@Data
@EqualsAndHashCode(callSuper=true)
@TableName(value = "product")
public class Product extends BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description="主键ID")
    private Long id;

    /**
     * 商品名称
     */
    @TableField(value = "`name`")
    @Schema(description="商品名称")
    private String name;

    /**
     * 商品编号
     */
    @TableField(value = "code")
    @Schema(description="商品编号")
    private String code;

    /**
     * 条形码
     */
    @TableField(value = "barcode")
    @Schema(description="条形码")
    private String barcode;

    /**
     * 单位ID
     */
    @TableField(value = "unit_id")
    @Schema(description="单位ID")
    private Long unitId;

    /**
     * 分类ID
     */
    @TableField(value = "category_id")
    @Schema(description="分类ID")
    private Long categoryId;

    /**
     * 最低库存
     */
    @TableField(value = "min_stock")
    @Schema(description="最低库存")
    private BigDecimal minStock;

    /**
     * 期初库存
     */
    @TableField(value = "initial_stock")
    @Schema(description="期初库存")
    private BigDecimal initialStock;

    /**
     * 期初库存货位ID，为空表示无货位
     */
    @TableField(value = "initial_stock_location_id")
    @Schema(description="期初库存货位ID，为空表示无货位")
    private Long initialStockLocationId;

    /**
     * 备注
     */
    @TableField(value = "remark")
    @Schema(description="备注")
    private String remark;
}
