package com.zhb.wms2.module.base.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zhb.wms2.common.model.BaseModel;
import com.zhb.wms2.common.validated.Update;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "出入库类型")
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "io_type")
public class IoType extends BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "主键ID")
    @NotNull(groups = Update.class, message = "ID不能为空")
    private Long id;

    @TableField(value = "`name`")
    @Schema(description = "类型名称")
    @NotBlank(message = "类型名称不能为空")
    private String name;

    @TableField(value = "biz_type")
    @Schema(description = "业务类型：1-入库 2-出库")
    @NotNull(message = "业务类型不能为空")
    @Min(value = 1, message = "业务类型不正确")
    @Max(value = 2, message = "业务类型不正确")
    private Integer bizType;
}
