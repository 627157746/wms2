package com.zhb.wms2.module.product.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zhb.wms2.common.model.BaseModel;
import com.zhb.wms2.common.validated.Update;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

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
    @NotNull(groups = Update.class, message = "ID不能为空")
    private Long id;

    /**
     * 商品名称
     */
    @TableField(value = "`name`")
    @Schema(description="商品名称")
    @NotBlank(message = "商品名称不能为空")
    private String name;

    /**
     * 商品编号
     */
    @TableField(value = "code")
    @Schema(description="商品编号")
    @NotBlank(message = "商品编号不能为空")
    private String code;

    /**
     * 单位ID
     */
    @TableField(value = "unit_id")
    @Schema(description="单位ID")
    @NotNull(message = "单位不能为空")
    @Min(value = 1, message = "单位不能为空")
    private Long unitId;

    /**
     * 分类ID
     */
    @TableField(value = "category_id")
    @Schema(description="分类ID")
    @Min(value = 1, message = "商品分类不正确")
    private Long categoryId;

    /**
     * 最低库存
     */
    @TableField(value = "min_stock")
    @Schema(description="最低库存")
    @Min(value = 0, message = "最低库存不能小于0")
    private Long minStock;

    /**
     * 期初库存
     */
    @TableField(value = "initial_stock")
    @Schema(description="期初库存")
    @Min(value = 0, message = "期初库存不能小于0")
    private Long initialStock;

    /**
     * 期初库存货位ID，0表示无货位
     */
    @TableField(value = "initial_stock_location_id")
    @Schema(description="期初库存货位ID，0表示无货位")
    @Min(value = 0, message = "期初库存货位不正确")
    private Long initialStockLocationId;

    /**
     * 备注
     */
    @TableField(value = "remark")
    @Schema(description="备注")
    private String remark;
}
