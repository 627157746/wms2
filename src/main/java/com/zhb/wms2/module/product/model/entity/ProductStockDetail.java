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

/**
 * @Author zhb
 * @Description 
 * @Date 2026/3/17 19:38
 */
/**
 * 库存明细
 */
@Schema(description="商品库存明细")
@Data
@EqualsAndHashCode(callSuper=true)
@TableName(value = "product_stock_detail")
public class ProductStockDetail extends BaseModel implements Serializable {
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
     * 货位ID
     */
    @TableField(value = "location_id")
    @Schema(description="货位ID")
    private Long locationId;

    /**
     * 库存数量
     */
    @TableField(value = "qty")
    @Schema(description="库存数量")
    private Long qty;
}
