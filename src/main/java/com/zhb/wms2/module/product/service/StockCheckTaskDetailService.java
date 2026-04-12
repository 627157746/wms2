package com.zhb.wms2.module.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhb.wms2.module.product.model.entity.StockCheckTaskDetail;

/**
 * 盘点任务明细服务接口。
 *
 * @author zhb
 * @since 2026/4/12
 */
public interface StockCheckTaskDetailService extends IService<StockCheckTaskDetail> {

    /**
     * 新增盘点明细。
     */
    void saveChecked(StockCheckTaskDetail detail);

    /**
     * 修改盘点明细。
     */
    void updateByIdChecked(StockCheckTaskDetail detail);

    /**
     * 删除盘点明细。
     */
    void removeByIdChecked(Long id);
}
