package com.zhb.wms2.module.base.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhb.wms2.common.exception.BaseException;
import com.zhb.wms2.module.base.mapper.DeliverymanMapper;
import com.zhb.wms2.module.base.model.entity.Deliveryman;
import com.zhb.wms2.module.base.model.query.DeliverymanQuery;
import com.zhb.wms2.module.base.service.DeliverymanService;
import com.zhb.wms2.module.base.service.support.BaseDictMapStore;
import com.zhb.wms2.module.inbound.model.entity.InboundApply;
import com.zhb.wms2.module.inbound.service.InboundApplyService;
import com.zhb.wms2.module.io.model.entity.IoOrder;
import com.zhb.wms2.module.io.service.IoOrderService;
import com.zhb.wms2.module.outbound.model.entity.OutboundApply;
import com.zhb.wms2.module.outbound.service.OutboundApplyService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @Author zhb
 * @Description
 * @Date 2026/3/17 18:57
 */
@Service
@RequiredArgsConstructor
public class DeliverymanServiceImpl extends ServiceImpl<DeliverymanMapper, Deliveryman> implements DeliverymanService {

    private final InboundApplyService inboundApplyService;
    private final OutboundApplyService outboundApplyService;
    private final IoOrderService ioOrderService;
    private final BaseDictMapStore baseDictMapStore;

    @Override
    public boolean save(Deliveryman deliveryman) {
        boolean saved = super.save(deliveryman);
        if (saved) {
            baseDictMapStore.clearDeliverymanMap();
        }
        return saved;
    }

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
    public void updateByIdChecked(Deliveryman deliveryman) {
        if (!updateById(deliveryman)) {
            throw new BaseException("送货员不存在");
        }
        baseDictMapStore.clearDeliverymanMap();
    }

    @Override
    public void removeByIdChecked(Long id) {
        long inboundApplyCount = inboundApplyService.count(
                new LambdaQueryWrapper<InboundApply>().eq(InboundApply::getDeliverymanId, id));
        if (inboundApplyCount > 0) {
            throw new BaseException("该送货员已被入库申请使用，无法删除");
        }
        long outboundApplyCount = outboundApplyService.count(
                new LambdaQueryWrapper<OutboundApply>().eq(OutboundApply::getDeliverymanId, id));
        if (outboundApplyCount > 0) {
            throw new BaseException("该送货员已被出库申请使用，无法删除");
        }
        long ioOrderCount = ioOrderService.count(
                new LambdaQueryWrapper<IoOrder>().eq(IoOrder::getDeliverymanId, id));
        if (ioOrderCount > 0) {
            throw new BaseException("该送货员已被出入库单使用，无法删除");
        }
        if (!removeById(id)) {
            throw new BaseException("送货员不存在");
        }
        baseDictMapStore.clearDeliverymanMap();
    }

    private LambdaQueryWrapper<Deliveryman> buildWrapper(DeliverymanQuery query) {
        return new LambdaQueryWrapper<Deliveryman>()
                .like(StrUtil.isNotBlank(query.getName()), Deliveryman::getName, query.getName())
                .like(StrUtil.isNotBlank(query.getPhone()), Deliveryman::getPhone, query.getPhone())
                .eq(query.getScope() != null, Deliveryman::getScope, query.getScope())
                .orderByDesc(Deliveryman::getId);
    }
}
