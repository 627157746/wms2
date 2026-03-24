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

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Override
    public SysLoginVO login(SysLoginDTO loginDTO) {
        SysUser sysUser = getOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, loginDTO.getUsername())
                .last("limit 1"));
        if (sysUser == null || !passwordMatched(sysUser.getPassword(), loginDTO.getPassword())) {
            throw new BaseException("用户名或密码错误");
        }

        StpUtil.login(sysUser.getId());
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();

        SysLoginVO loginVO = new SysLoginVO();
        loginVO.setTokenName(tokenInfo.getTokenName());
        loginVO.setTokenValue(tokenInfo.getTokenValue());
        loginVO.setLoginId(sysUser.getId());
        loginVO.setUsername(sysUser.getUsername());
        loginVO.setTokenTimeout(tokenInfo.getTokenTimeout());
        return loginVO;
    }

    private boolean passwordMatched(String storedPassword, String inputPassword) {
        return StrUtil.equals(storedPassword, inputPassword)
                || StrUtil.equals(storedPassword, SaSecureUtil.md5(inputPassword));
    }
}
