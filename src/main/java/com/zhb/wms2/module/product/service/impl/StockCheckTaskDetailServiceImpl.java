package com.zhb.wms2.module.product.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhb.wms2.common.exception.BaseException;
import com.zhb.wms2.module.product.mapper.StockCheckTaskDetailMapper;
import com.zhb.wms2.module.product.model.entity.StockCheckTaskDetail;
import com.zhb.wms2.module.product.service.StockCheckTaskDetailService;
import org.springframework.stereotype.Service;

/**
 * 盘点任务明细服务实现。
 *
 * @author zhb
 * @since 2026/4/12
 */
@Service
public class StockCheckTaskDetailServiceImpl extends ServiceImpl<StockCheckTaskDetailMapper, StockCheckTaskDetail>
        implements StockCheckTaskDetailService {

    /**
     * 新增盘点明细。
     */
    @Override
    public void saveChecked(StockCheckTaskDetail detail) {
        if (!super.save(detail)) {
            throw new BaseException("盘点商品新增失败");
        }
    }

    /**
     * 修改盘点明细。
     */
    @Override
    public void updateByIdChecked(StockCheckTaskDetail detail) {
        if (!super.updateById(detail)) {
            throw new BaseException("盘点商品不存在");
        }
    }

    /**
     * 删除盘点明细。
     */
    @Override
    public void removeByIdChecked(Long id) {
        if (!super.removeById(id)) {
            throw new BaseException("盘点商品不存在");
        }
    }
}
