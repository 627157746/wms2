package com.zhb.wms2.module.io.model.dto;

import com.zhb.wms2.common.validated.Update;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 出入库申请修改DTO DTO
 *
 * @author zhb
 * @since 2026/3/26
 */
@Schema(description = "出入库申请修改DTO")
@Data
@EqualsAndHashCode(callSuper = true)
public class IoApplyUpdateDTO extends IoApplyCreateDTO {

    /**
     * 主键 ID。
     */
    @Schema(description = "主键ID")
    @NotNull(groups = Update.class, message = "ID不能为空")
    @Min(value = 1, groups = Update.class, message = "ID不能为空")
    private Long id;
}
