package com.zhb.wms2.module.base.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zhb.wms2.common.model.BaseModel;
import com.zhb.wms2.common.validated.Update;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author zhb
 * @Description 
 * @Date 2026/3/17 19:35
 */
/**
 * 商品分类
 */
@Schema(description="商品分类")
@Data
@EqualsAndHashCode(callSuper=true)
@TableName(value = "product_category")
public class ProductCategory extends BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description="主键ID")
    @NotNull(groups = Update.class, message = "ID不能为空")
    private Long id;

    /**
     * 分类名称
     */
    @TableField(value = "`name`")
    @Schema(description="分类名称")
    @NotBlank(message = "分类名称不能为空")
    private String name;

    /**
     * 父级ID，0为顶级
     */
    @TableField(value = "parent_id")
    @Schema(description="父级ID，0为顶级")
    private Long parentId;

    /**
     * 排序
     */
    @TableField(value = "sort_order")
    @Schema(description="排序")
    private Integer sortOrder;

    /**
     * 层级
     */
    @TableField(value = "`level`")
    @Schema(description="层级")
    private Integer level;
}
