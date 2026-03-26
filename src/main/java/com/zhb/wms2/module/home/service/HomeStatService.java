package com.zhb.wms2.module.home.service;

import com.zhb.wms2.module.home.model.vo.HomeStatVO;

/**
 * HomeStatService 服务接口
 *
 * @author zhb
 * @since 2026/3/26
 */
public interface HomeStatService {

    /**
     * 查询首页统计信息。
     */
    HomeStatVO getHomeStat();
}
