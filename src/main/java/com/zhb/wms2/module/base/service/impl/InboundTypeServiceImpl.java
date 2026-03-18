package com.zhb.wms2.module.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhb.wms2.common.exception.BaseException;
import com.zhb.wms2.module.base.mapper.InboundTypeMapper;
import com.zhb.wms2.module.base.model.entity.InboundType;
import com.zhb.wms2.module.base.model.query.InboundTypeQuery;
import com.zhb.wms2.module.base.service.InboundTypeService;
import com.zhb.wms2.module.inbound.model.entity.InboundApply;
import com.zhb.wms2.module.inbound.model.entity.InboundOrder;
import com.zhb.wms2.module.inbound.service.InboundApplyService;
import com.zhb.wms2.module.inbound.service.InboundOrderService;
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
public class InboundTypeServiceImpl extends ServiceImpl<InboundTypeMapper, InboundType> implements InboundTypeService {

    private final InboundApplyService inboundApplyService;
    private final InboundOrderService inboundOrderService;

    @Override
    public IPage<InboundType> pageQuery(InboundTypeQuery query) {
        LambdaQueryWrapper<InboundType> wrapper = buildWrapper(query);
        return page(new Page<>(query.getCurrent(), query.getSize()), wrapper);
    }

    @Override
    public List<InboundType> listAll() {
        return list(new LambdaQueryWrapper<InboundType>().orderByDesc(InboundType::getId));
    }

    @Override
    public void removeByIdChecked(Long id) {
        long applyCount = inboundApplyService.count(
                new LambdaQueryWrapper<InboundApply>().eq(InboundApply::getInboundTypeId, id));
        if (applyCount > 0) {
            throw new BaseException("该入库类型已被入库申请使用，无法删除");
        }
        long orderCount = inboundOrderService.count(
                new LambdaQueryWrapper<InboundOrder>().eq(InboundOrder::getInboundTypeId, id));
        if (orderCount > 0) {
            throw new BaseException("该入库类型已被入库单使用，无法删除");
        }
        removeById(id);
    }

    private LambdaQueryWrapper<InboundType> buildWrapper(InboundTypeQuery query) {
        return new LambdaQueryWrapper<InboundType>()
                .like(StringUtils.hasText(query.getName()), InboundType::getName, query.getName())
                .orderByDesc(InboundType::getId);
    }
}
