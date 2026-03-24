package com.zhb.wms2.module.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhb.wms2.module.system.model.dto.SysLoginDTO;
import com.zhb.wms2.module.system.model.entity.SysUser;
import com.zhb.wms2.module.system.model.vo.SysLoginVO;

public interface SysUserService extends IService<SysUser> {

    SysLoginVO login(SysLoginDTO loginDTO);

}
