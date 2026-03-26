package com.zhb.wms2.module.system.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 系统登录DTO DTO
 *
 * @author zhb
 * @since 2026/3/26
 */
@Data
@Schema(description = "系统登录DTO")
public class SysLoginDTO {

    /**
     * 用户名。
     */
    @Schema(description = "用户名")
    @NotBlank(message = "用户名不能为空")
    private String username;

    /**
     * 密码。
     */
    @Schema(description = "密码")
    @NotBlank(message = "密码不能为空")
    private String password;
}
