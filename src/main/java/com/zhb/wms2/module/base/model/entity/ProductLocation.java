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
 * 商品货位
 *
 * @author zhb
 * @since 2026/3/26
 */
@Schema(description="商品货位")
@Data
@EqualsAndHashCode(callSuper=true)
@TableName(value = "product_location")
public class ProductLocation extends BaseModel implements Serializable {
    /**
     * 序列化版本号。
     */
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description="主键ID")
    @NotNull(groups = Update.class, message = "ID不能为空")
    private Long id;

    /**
     * 货位编码，如1-1
     */
    @TableField(value = "code")
    @Schema(description="货位编码，如1-1")
    @NotBlank(message = "货位编码不能为空")
    private String code;

    /**
     * 排序
     */
    @TableField(value = "sort_order")
    @Schema(description="排序")
    private Integer sortOrder;
}
