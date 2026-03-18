package com.zhb.wms2.module.base.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhb.wms2.common.constant.IoBizTypeConstant;
import com.zhb.wms2.common.exception.BaseException;
import com.zhb.wms2.module.base.mapper.CustomerMapper;
import com.zhb.wms2.module.base.model.entity.Customer;
import com.zhb.wms2.module.base.model.query.CustomerQuery;
import com.zhb.wms2.module.base.service.CustomerService;
import com.zhb.wms2.module.base.service.support.BaseDictMapStore;
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
public class CustomerServiceImpl extends ServiceImpl<CustomerMapper, Customer> implements CustomerService {

    private final OutboundApplyService outboundApplyService;
    private final IoOrderService ioOrderService;
    private final BaseDictMapStore baseDictMapStore;

    @Override
    public boolean save(Customer customer) {
        boolean saved = super.save(customer);
        if (saved) {
            baseDictMapStore.clearCustomerMap();
        }
        return saved;
    }

    @Override
    public IPage<Customer> pageQuery(CustomerQuery query) {
        LambdaQueryWrapper<Customer> wrapper = buildWrapper(query);
        return page(new Page<>(query.getCurrent(), query.getSize()), wrapper);
    }

    @Override
    public List<Customer> listAll() {
        return list(new LambdaQueryWrapper<Customer>().orderByDesc(Customer::getId));
    }

    @Override
    public void updateByIdChecked(Customer customer) {
        if (!updateById(customer)) {
            throw new BaseException("客户不存在");
        }
        baseDictMapStore.clearCustomerMap();
    }

    @Override
    public void removeByIdChecked(Long id) {
        long applyCount = outboundApplyService.count(
                new LambdaQueryWrapper<OutboundApply>().eq(OutboundApply::getCustomerId, id));
        if (applyCount > 0) {
            throw new BaseException("该客户已被出库申请使用，无法删除");
        }
        long orderCount = ioOrderService.count(
                new LambdaQueryWrapper<IoOrder>()
                        .eq(IoOrder::getCustomerId, id)
                        .eq(IoOrder::getOrderType, IoBizTypeConstant.OUTBOUND));
        if (orderCount > 0) {
            throw new BaseException("该客户已被出库单使用，无法删除");
        }
        if (!removeById(id)) {
            throw new BaseException("客户不存在");
        }
        baseDictMapStore.clearCustomerMap();
    }

    private LambdaQueryWrapper<Customer> buildWrapper(CustomerQuery query) {
        return new LambdaQueryWrapper<Customer>()
                .like(StrUtil.isNotBlank(query.getName()), Customer::getName, query.getName())
                .like(StrUtil.isNotBlank(query.getPhone()), Customer::getPhone, query.getPhone())
                .eq(query.getScope() != null, Customer::getScope, query.getScope())
                .orderByDesc(Customer::getId);
    }
}
