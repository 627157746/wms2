package com.zhb.wms2.module.base.service;

import com.zhb.wms2.module.base.model.dto.BaseDictMapDTO;

/**
 * base 模块字典映射服务
 *
 * @author zhb
 * @since 2026/3/18
 */
public interface BaseDictMapService {

    /**
     * 获取 base 模块的基础资料映射。
     */
    BaseDictMapDTO getBaseDictMap();
}
