package com.zhb.wms2.module.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhb.wms2.module.system.model.dto.SysLoginDTO;
import com.zhb.wms2.module.system.model.entity.SysUser;
import com.zhb.wms2.module.system.model.vo.SysLoginVO;

/**
 * SysUserService 服务接口
 *
 * @author zhb
 * @since 2026/3/26
 */
public interface SysUserService extends IService<SysUser> {

    /**
     * 执行用户登录。
     */
    SysLoginVO login(SysLoginDTO loginDTO);

}
