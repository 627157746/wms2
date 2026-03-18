package com.zhb.wms2.module.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhb.wms2.common.exception.BaseException;
import com.zhb.wms2.module.base.mapper.CustomerMapper;
import com.zhb.wms2.module.base.model.entity.Customer;
import com.zhb.wms2.module.base.model.query.CustomerQuery;
import com.zhb.wms2.module.base.service.CustomerService;
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
public class CustomerServiceImpl extends ServiceImpl<CustomerMapper, Customer> implements CustomerService {

    private final OutboundApplyService outboundApplyService;
    private final OutboundOrderService outboundOrderService;

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
    public void removeByIdChecked(Long id) {
        long applyCount = outboundApplyService.count(
                new LambdaQueryWrapper<OutboundApply>().eq(OutboundApply::getCustomerId, id));
        if (applyCount > 0) {
            throw new BaseException("该客户已被出库申请使用，无法删除");
        }
        long orderCount = outboundOrderService.count(
                new LambdaQueryWrapper<OutboundOrder>().eq(OutboundOrder::getCustomerId, id));
        if (orderCount > 0) {
            throw new BaseException("该客户已被出库单使用，无法删除");
        }
        removeById(id);
    }

    private LambdaQueryWrapper<Customer> buildWrapper(CustomerQuery query) {
        return new LambdaQueryWrapper<Customer>()
                .like(StringUtils.hasText(query.getName()), Customer::getName, query.getName())
                .like(StringUtils.hasText(query.getPhone()), Customer::getPhone, query.getPhone())
                .eq(query.getScope() != null, Customer::getScope, query.getScope())
                .orderByDesc(Customer::getId);
    }
}
