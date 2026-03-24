package com.zhb.wms2.module.system.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.zhb.wms2.common.model.R;
import com.zhb.wms2.module.system.model.dto.SysLoginDTO;
import com.zhb.wms2.module.system.model.vo.SysLoginVO;
import com.zhb.wms2.module.system.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/system/user")
@Tag(name = "系统用户", description = "系统用户登录登出")
@RequiredArgsConstructor
@Validated
public class SysUserController {

    private final SysUserService sysUserService;

    @PostMapping("/login")
    @Operation(summary = "登录")
    public R<SysLoginVO> login(
            @Parameter(description = "登录参数", required = true)
            @RequestBody @Valid SysLoginDTO loginDTO) {
        return R.ok(sysUserService.login(loginDTO));
    }

    @PostMapping("/logout")
    @Operation(summary = "登出")
    public R<Void> logout() {
        StpUtil.logout();
        return R.optOk();
    }
}
