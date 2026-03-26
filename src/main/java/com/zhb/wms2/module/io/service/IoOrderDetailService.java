package com.zhb.wms2.module.io.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhb.wms2.module.io.model.entity.IoOrderDetail;

import java.util.List;

/**
 * 入出库单明细服务接口。
 *
 * @author zhb
 * @since 2026/3/26
 */
public interface IoOrderDetailService extends IService<IoOrderDetail> {

    /**
     * 批量新增单据明细。
     */
    void saveBatchChecked(List<IoOrderDetail> detailList);

    /**
     * 修改单据明细。
     */
    void updateByIdChecked(IoOrderDetail detail);

    /**
     * 按单据 ID 删除明细。
     */
    void removeByOrderIdChecked(Long orderId);
}
