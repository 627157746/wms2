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
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import java.io.Serializable;

/**
 * 出入库类型字典表
 */
@Schema(description = "出入库类型字典表")
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "warehouse_types")
public class WarehouseTypes extends BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 类型ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "类型ID")
    private Long id;

    /**
     * 类型名称
     */
    @TableField(value = "type_name")
    @Schema(description = "类型名称")
    @NotBlank(message = "类型名称不能为空")
    @Size(max = 50, message = "类型名称长度不能超过50个字符")
    private String typeName;

    /**
     * 类型分类：1-入库，2-出库
     */
    @TableField(value = "type_category")
    @Schema(description = "类型分类：1-入库，2-出库")
    @NotNull(message = "类型分类不能为空")
    @Min(value = 1, message = "类型分类值必须为1或2")
    @Max(value = 2, message = "类型分类值必须为1或2")
    private Integer typeCategory;

    /**
     * 类型描述
     */
    @TableField(value = "description")
    @Schema(description = "类型描述")
    @Size(max = 200, message = "类型描述长度不能超过200个字符")
    private String description;

    /**
     * 排序
     */
    @TableField(value = "sort_order")
    @Schema(description = "排序")
    @Min(value = 0, message = "排序值不能小于0")
    private Integer sortOrder;
}
