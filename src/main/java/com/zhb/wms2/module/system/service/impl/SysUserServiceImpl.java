package com.zhb.wms2.module.system.service.impl;

import cn.dev33.satoken.secure.SaSecureUtil;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhb.wms2.common.exception.BaseException;
import com.zhb.wms2.module.system.mapper.SysUserMapper;
import com.zhb.wms2.module.system.model.dto.SysLoginDTO;
import com.zhb.wms2.module.system.model.entity.SysUser;
import com.zhb.wms2.module.system.model.vo.SysLoginVO;
import com.zhb.wms2.module.system.service.SysUserService;
import org.springframework.stereotype.Service;

/**
 * SysUserServiceImpl 服务实现
 *
 * @author zhb
 * @since 2026/3/26
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    /**
     * 校验用户名密码并返回登录结果。
     */
    @Override
    public SysLoginVO login(SysLoginDTO loginDTO) {
        // 登录只允许命中单个用户记录，避免同名数据导致鉴权歧义。
        SysUser sysUser = getOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, loginDTO.getUsername())
                .last("limit 1"));
        if (sysUser == null || !passwordMatched(sysUser.getPassword(), loginDTO.getPassword())) {
            throw new BaseException("用户名或密码错误");
        }

        // Sa-Token 登录成功后，从 tokenInfo 中回填前端所需凭证信息。
        StpUtil.login(sysUser.getId());
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();

        return new SysLoginVO()
                .setTokenName(tokenInfo.getTokenName())
                .setTokenValue(tokenInfo.getTokenValue())
                .setLoginId(sysUser.getId())
                .setUsername(sysUser.getUsername())
                .setTokenTimeout(tokenInfo.getTokenTimeout());
    }

    /**
     * 兼容明文和 MD5 密码比对。
     */
    private boolean passwordMatched(String storedPassword, String inputPassword) {
        // 兼容历史明文密码和当前 MD5 密码存量数据。
        return StrUtil.equals(storedPassword, inputPassword)
                || StrUtil.equals(storedPassword, SaSecureUtil.md5(inputPassword));
    }
}
