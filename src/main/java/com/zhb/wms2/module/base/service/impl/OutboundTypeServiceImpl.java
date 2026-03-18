package com.zhb.wms2.module.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhb.wms2.common.exception.BaseException;
import com.zhb.wms2.module.base.mapper.OutboundTypeMapper;
import com.zhb.wms2.module.base.model.entity.OutboundType;
import com.zhb.wms2.module.base.model.query.OutboundTypeQuery;
import com.zhb.wms2.module.base.service.OutboundTypeService;
import com.zhb.wms2.module.outbound.model.entity.OutboundApply;
import com.zhb.wms2.module.outbound.model.entity.OutboundOrder;
import com.zhb.wms2.module.outbound.service.OutboundApplyService;
import com.zhb.wms2.module.outbound.service.OutboundOrderService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @Author zhb
 * @Description
 * @Date 2026/3/17 18:58
 */
@Service
@RequiredArgsConstructor
public class OutboundTypeServiceImpl extends ServiceImpl<OutboundTypeMapper, OutboundType> implements OutboundTypeService {

    private final OutboundApplyService outboundApplyService;
    private final OutboundOrderService outboundOrderService;

    @Override
    public IPage<OutboundType> pageQuery(OutboundTypeQuery query) {
        LambdaQueryWrapper<OutboundType> wrapper = buildWrapper(query);
        return page(new Page<>(query.getCurrent(), query.getSize()), wrapper);
    }

    @Override
    public List<OutboundType> listAll() {
        return list(new LambdaQueryWrapper<OutboundType>().orderByDesc(OutboundType::getId));
    }

    @Override
    public void removeByIdChecked(Long id) {
        long applyCount = outboundApplyService.count(
                new LambdaQueryWrapper<OutboundApply>().eq(OutboundApply::getOutboundTypeId, id));
        if (applyCount > 0) {
            throw new BaseException("该出库类型已被出库申请使用，无法删除");
        }
        long orderCount = outboundOrderService.count(
                new LambdaQueryWrapper<OutboundOrder>().eq(OutboundOrder::getOutboundTypeId, id));
        if (orderCount > 0) {
            throw new BaseException("该出库类型已被出库单使用，无法删除");
        }
        removeById(id);
    }

    private LambdaQueryWrapper<OutboundType> buildWrapper(OutboundTypeQuery query) {
        return new LambdaQueryWrapper<OutboundType>()
                .like(StringUtils.hasText(query.getName()), OutboundType::getName, query.getName())
                .orderByDesc(OutboundType::getId);
    }
}
