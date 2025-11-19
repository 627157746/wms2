package com.zhb.wms2.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zhb.wms2.common.BaseModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

/**
 * @Author zhb
 * @Description
 * @Date 2025/11/19 16:19
 */
/**
 * 商品信息表
 */
@Schema(description="商品信息表")
@Data
@EqualsAndHashCode(callSuper=true)
@TableName(value = "products")
public class Products extends BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 商品ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description="商品ID")
    private Long id;

    /**
     * 商品编号
     */
    @TableField(value = "product_code")
    @Schema(description="商品编号")
    @NotBlank(message = "商品编号不能为空")
    @Size(max = 50, message = "商品编号长度不能超过50个字符")
    private String productCode;

    /**
     * 商品名称
     */
    @TableField(value = "product_name")
    @Schema(description="商品名称")
    @NotBlank(message = "商品名称不能为空")
    @Size(max = 100, message = "商品名称长度不能超过100个字符")
    private String productName;

    /**
     * 商品规格
     */
    @TableField(value = "specification")
    @Schema(description="商品规格")
    @NotBlank(message = "商品规格不能为空")
    @Size(max = 100, message = "商品规格长度不能超过100个字符")
    private String specification;

    /**
     * 商品品牌
     */
    @TableField(value = "brand")
    @Schema(description="商品品牌")
    @NotBlank(message = "商品品牌不能为空")
    @Size(max = 50, message = "商品品牌长度不能超过50个字符")
    private String brand;

}
