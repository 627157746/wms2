package com.zhb.wms2.module.system.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "系统登录结果")
public class SysLoginVO {

    @Schema(description = "Token名称")
    private String tokenName;

    @Schema(description = "Token值")
    private String tokenValue;

    @Schema(description = "登录用户ID")
    private Long loginId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "Token剩余有效期，单位秒")
    private Long tokenTimeout;
}
