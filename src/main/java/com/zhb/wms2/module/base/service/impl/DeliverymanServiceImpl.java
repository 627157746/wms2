package com.zhb.wms2.module.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhb.wms2.common.exception.BaseException;
import com.zhb.wms2.module.base.mapper.DeliverymanMapper;
import com.zhb.wms2.module.base.model.entity.Deliveryman;
import com.zhb.wms2.module.base.model.query.DeliverymanQuery;
import com.zhb.wms2.module.base.service.DeliverymanService;
import com.zhb.wms2.module.inbound.model.entity.InboundApply;
import com.zhb.wms2.module.inbound.model.entity.InboundOrder;
import com.zhb.wms2.module.inbound.service.InboundApplyService;
import com.zhb.wms2.module.inbound.service.InboundOrderService;
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
 * @Date 2026/3/17 18:57
 */
@Service
@RequiredArgsConstructor
public class DeliverymanServiceImpl extends ServiceImpl<DeliverymanMapper, Deliveryman> implements DeliverymanService {

    private final InboundApplyService inboundApplyService;
    private final InboundOrderService inboundOrderService;
    private final OutboundApplyService outboundApplyService;
    private final OutboundOrderService outboundOrderService;

    @Override
    public IPage<Deliveryman> pageQuery(DeliverymanQuery query) {
        LambdaQueryWrapper<Deliveryman> wrapper = buildWrapper(query);
        return page(new Page<>(query.getCurrent(), query.getSize()), wrapper);
    }

    @Override
    public List<Deliveryman> listAll() {
        return list(new LambdaQueryWrapper<Deliveryman>().orderByDesc(Deliveryman::getId));
    }

    @Override
    public void removeByIdChecked(Long id) {
        long inboundApplyCount = inboundApplyService.count(
                new LambdaQueryWrapper<InboundApply>().eq(InboundApply::getDeliverymanId, id));
        if (inboundApplyCount > 0) {
            throw new BaseException("该送货员已被入库申请使用，无法删除");
        }
        long inboundOrderCount = inboundOrderService.count(
                new LambdaQueryWrapper<InboundOrder>().eq(InboundOrder::getDeliverymanId, id));
        if (inboundOrderCount > 0) {
            throw new BaseException("该送货员已被入库单使用，无法删除");
        }
        long outboundApplyCount = outboundApplyService.count(
                new LambdaQueryWrapper<OutboundApply>().eq(OutboundApply::getDeliverymanId, id));
        if (outboundApplyCount > 0) {
            throw new BaseException("该送货员已被出库申请使用，无法删除");
        }
        long outboundOrderCount = outboundOrderService.count(
                new LambdaQueryWrapper<OutboundOrder>().eq(OutboundOrder::getDeliverymanId, id));
        if (outboundOrderCount > 0) {
            throw new BaseException("该送货员已被出库单使用，无法删除");
        }
        removeById(id);
    }

    private LambdaQueryWrapper<Deliveryman> buildWrapper(DeliverymanQuery query) {
        return new LambdaQueryWrapper<Deliveryman>()
                .like(StringUtils.hasText(query.getName()), Deliveryman::getName, query.getName())
                .like(StringUtils.hasText(query.getPhone()), Deliveryman::getPhone, query.getPhone())
                .eq(query.getScope() != null, Deliveryman::getScope, query.getScope())
                .orderByDesc(Deliveryman::getId);
    }
}
