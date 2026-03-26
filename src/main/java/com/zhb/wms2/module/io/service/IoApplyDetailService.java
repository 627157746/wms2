package com.zhb.wms2.module.io.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhb.wms2.module.io.model.entity.IoApplyDetail;

import java.util.List;

/**
 * 入出库申请明细服务接口。
 *
 * @author zhb
 * @since 2026/3/26
 */
public interface IoApplyDetailService extends IService<IoApplyDetail> {

    /**
     * 批量新增申请明细。
     */
    void saveBatchChecked(List<IoApplyDetail> detailList);

    /**
     * 按申请 ID 删除明细。
     */
    void removeByApplyIdChecked(Long applyId);
}
