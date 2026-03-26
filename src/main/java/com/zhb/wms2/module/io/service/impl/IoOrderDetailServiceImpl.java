package com.zhb.wms2.module.io.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhb.wms2.common.exception.BaseException;
import com.zhb.wms2.module.io.mapper.IoOrderDetailMapper;
import com.zhb.wms2.module.io.model.entity.IoOrderDetail;
import com.zhb.wms2.module.io.service.IoOrderDetailService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 入出库单明细服务实现。
 *
 * @author zhb
 * @since 2026/3/26
 */
@Service
public class IoOrderDetailServiceImpl extends ServiceImpl<IoOrderDetailMapper, IoOrderDetail> implements IoOrderDetailService {

    /**
     * 批量新增单据明细。
     */
    @Override
    public void saveBatchChecked(List<IoOrderDetail> detailList) {
        // 单据明细按批量方式入库，保证主单和明细写入节奏一致。
        if (!super.saveBatch(detailList)) {
            throw new BaseException("单据明细新增失败");
        }
    }

    /**
     * 修改单据明细。
     */
    @Override
    public void updateByIdChecked(IoOrderDetail detail) {
        // 明细更新主要用于货位调整后的单条同步。
        if (!super.updateById(detail)) {
            throw new BaseException("单据明细不存在");
        }
    }

    /**
     * 按单据 ID 删除明细。
     */
    @Override
    public void removeByOrderIdChecked(Long orderId) {
        // 修改或删除主单时，明细统一按单据 ID 整批清理。
        if (!super.remove(new LambdaQueryWrapper<IoOrderDetail>().eq(IoOrderDetail::getOrderId, orderId))) {
            throw new BaseException("单据明细删除失败");
        }
    }
}
