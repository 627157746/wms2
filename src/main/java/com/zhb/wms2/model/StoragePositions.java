package com.zhb.wms2.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zhb.wms2.common.BaseModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @Author zhb
 * @Description
 * @Date 2025/11/19 16:19
 */
/**
 * 货位信息表
 */
@Schema(description="货位信息表")
@Data
@EqualsAndHashCode(callSuper=true)
@TableName(value = "storage_positions")
public class StoragePositions extends BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 货位ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description="货位ID")
    private Long id;

    /**
     * 物料位ID
     */
    @TableField(value = "material_location_id")
    @Schema(description="物料位ID")
    private Long materialLocationId;

    /**
     * 当前存放数量
     */
    @TableField(value = "current_quantity")
    @Schema(description="当前存放数量")
    private Integer currentQuantity;

}
