package com.zhb.wms2.module.io.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhb.wms2.common.exception.BaseException;
import com.zhb.wms2.module.io.mapper.IoApplyDetailMapper;
import com.zhb.wms2.module.io.model.entity.IoApplyDetail;
import com.zhb.wms2.module.io.service.IoApplyDetailService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 入出库申请明细服务实现。
 *
 * @author zhb
 * @since 2026/3/26
 */
@Service
public class IoApplyDetailServiceImpl extends ServiceImpl<IoApplyDetailMapper, IoApplyDetail>
        implements IoApplyDetailService {

    /**
     * 批量新增申请明细。
     */
    @Override
    public void saveBatchChecked(List<IoApplyDetail> detailList) {
        // 申请明细始终按主单批量落库，避免逐条保存带来的部分成功。
        if (!super.saveBatch(detailList)) {
            throw new BaseException("申请明细新增失败");
        }
    }

    /**
     * 按申请 ID 删除明细。
     */
    @Override
    public void removeByApplyIdChecked(Long applyId) {
        // 主单重建明细时直接按申请 ID 全量删除旧数据。
        if (!super.remove(new LambdaQueryWrapper<IoApplyDetail>().eq(IoApplyDetail::getApplyId, applyId))) {
            throw new BaseException("申请明细删除失败");
        }
    }
}
