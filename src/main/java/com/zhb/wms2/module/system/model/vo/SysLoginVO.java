package com.zhb.wms2.module.system.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 系统登录结果 VO
 *
 * @author zhb
 * @since 2026/3/26
 */
@Data
@Accessors(chain = true)
@Schema(description = "系统登录结果")
public class SysLoginVO {

    /**
     * Token 名称。
     */
    @Schema(description = "Token名称")
    private String tokenName;

    /**
     * Token 值。
     */
    @Schema(description = "Token值")
    private String tokenValue;

    /**
     * 登录用户 ID。
     */
    @Schema(description = "登录用户ID")
    private Long loginId;

    /**
     * 用户名。
     */
    @Schema(description = "用户名")
    private String username;

    /**
     * Token 剩余有效期，单位秒。
     */
    @Schema(description = "Token剩余有效期，单位秒")
    private Long tokenTimeout;
}
