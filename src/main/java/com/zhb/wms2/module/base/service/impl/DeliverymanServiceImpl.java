package com.zhb.wms2.module.base.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhb.wms2.common.enums.IoBizTypeEnum;
import com.zhb.wms2.common.enums.ScopeEnum;
import com.zhb.wms2.common.exception.BaseException;
import com.zhb.wms2.module.base.mapper.DeliverymanMapper;
import com.zhb.wms2.module.base.model.entity.Deliveryman;
import com.zhb.wms2.module.base.model.query.DeliverymanQuery;
import com.zhb.wms2.module.base.service.DeliverymanService;
import com.zhb.wms2.module.base.service.support.BaseDictMapStore;
import com.zhb.wms2.module.io.model.entity.IoApply;
import com.zhb.wms2.module.io.model.entity.IoOrder;
import com.zhb.wms2.module.io.service.IoApplyService;
import com.zhb.wms2.module.io.service.IoOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author zhb
 * @Description
 * @Date 2026/3/17 18:57
 */
@Service
@RequiredArgsConstructor
public class DeliverymanServiceImpl extends ServiceImpl<DeliverymanMapper, Deliveryman> implements DeliverymanService {

    private final IoApplyService ioApplyService;
    private final IoOrderService ioOrderService;
    private final BaseDictMapStore baseDictMapStore;

    @Override
    public void saveChecked(Deliveryman deliveryman) {
        if (!super.save(deliveryman)) {
            throw new BaseException("送货员新增失败");
        }
        baseDictMapStore.clearDeliverymanMap();
    }

    @Override
    public IPage<Deliveryman> pageQuery(DeliverymanQuery query) {
        LambdaQueryWrapper<Deliveryman> wrapper = buildWrapper(query);
        return page(new Page<>(query.getCurrent(), query.getSize()), wrapper);
    }

    @Override
    public List<Deliveryman> listAllByScope(Integer scope) {
        LambdaQueryWrapper<Deliveryman> wrapper = new LambdaQueryWrapper<Deliveryman>()
                .orderByDesc(Deliveryman::getId);
        return list(wrapper.and(w -> w.in(Deliveryman::getScope, ScopeEnum.COMMON.getCode(), scope)));
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
        long inboundApplyCount = ioApplyService.count(
                new LambdaQueryWrapper<IoApply>()
                        .eq(IoApply::getDeliverymanId, id)
                        .eq(IoApply::getOrderType, IoBizTypeEnum.INBOUND.getCode()));
        if (inboundApplyCount > 0) {
            throw new BaseException("该送货员已被入库申请使用，无法删除");
        }
        long outboundApplyCount = ioApplyService.count(
                new LambdaQueryWrapper<IoApply>()
                        .eq(IoApply::getDeliverymanId, id)
                        .eq(IoApply::getOrderType, IoBizTypeEnum.OUTBOUND.getCode()));
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
