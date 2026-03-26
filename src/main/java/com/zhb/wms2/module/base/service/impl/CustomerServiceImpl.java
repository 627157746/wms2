package com.zhb.wms2.module.base.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhb.wms2.common.enums.IoBizTypeEnum;
import com.zhb.wms2.common.exception.BaseException;
import com.zhb.wms2.module.base.mapper.CustomerMapper;
import com.zhb.wms2.module.base.model.entity.Customer;
import com.zhb.wms2.module.base.model.query.CustomerQuery;
import com.zhb.wms2.module.base.service.CustomerService;
import com.zhb.wms2.module.base.service.support.BaseDictMapStore;
import com.zhb.wms2.module.io.model.entity.IoApply;
import com.zhb.wms2.module.io.model.entity.IoOrder;
import com.zhb.wms2.module.io.service.IoApplyService;
import com.zhb.wms2.module.io.service.IoOrderService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * CustomerServiceImpl 服务实现
 *
 * @author zhb
 * @since 2026/3/26
 */
@Service
@RequiredArgsConstructor
public class CustomerServiceImpl extends ServiceImpl<CustomerMapper, Customer> implements CustomerService {

    private final IoApplyService ioApplyService;
    private final IoOrderService ioOrderService;
    private final BaseDictMapStore baseDictMapStore;

    /**
     * 新增客户并清理字典缓存。
     */
    @Override
    public void saveChecked(Customer customer) {
        if (!super.save(customer)) {
            throw new BaseException("客户新增失败");
        }
        // 基础资料变更后立即清缓存，保证后续申请单读取到最新客户信息。
        baseDictMapStore.clearCustomerMap();
    }

    /**
     * 分页查询客户。
     */
    @Override
    public IPage<Customer> pageQuery(CustomerQuery query) {
        // 客户分页只负责基础筛选，关联展示字段由业务单据查询时再补充。
        LambdaQueryWrapper<Customer> wrapper = buildWrapper(query);
        return page(new Page<>(query.getCurrent(), query.getSize()), wrapper);
    }

    /**
     * 查询全部客户。
     */
    @Override
    public List<Customer> listAll() {
        // 下拉枚举场景直接返回全量客户列表。
        return list(new LambdaQueryWrapper<Customer>().orderByDesc(Customer::getId));
    }

    /**
     * 修改客户并清理字典缓存。
     */
    @Override
    public void updateByIdChecked(Customer customer) {
        if (!updateById(customer)) {
            throw new BaseException("客户不存在");
        }
        // 修改后清缓存，避免字典映射继续返回旧值。
        baseDictMapStore.clearCustomerMap();
    }

    /**
     * 删除客户前校验是否被出库申请或出库单引用。
     */
    @Override
    public void removeByIdChecked(Long id) {
        // 客户仅在出库业务中生效，删除前需要同时检查申请和出库单引用。
        long applyCount = ioApplyService.count(
                new LambdaQueryWrapper<IoApply>()
                        .eq(IoApply::getCustomerId, id)
                        .eq(IoApply::getOrderType, IoBizTypeEnum.OUTBOUND.getCode()));
        if (applyCount > 0) {
            throw new BaseException("该客户已被出库申请使用，无法删除");
        }
        long orderCount = ioOrderService.count(
                new LambdaQueryWrapper<IoOrder>()
                        .eq(IoOrder::getCustomerId, id)
                        .eq(IoOrder::getOrderType, IoBizTypeEnum.OUTBOUND.getCode()));
        if (orderCount > 0) {
            throw new BaseException("该客户已被出库单使用，无法删除");
        }
        if (!removeById(id)) {
            throw new BaseException("客户不存在");
        }
        // 删除后同步失效缓存，保证基础资料列表及时收敛。
        baseDictMapStore.clearCustomerMap();
    }

    /**
     * 构建客户分页查询条件。
     */
    private LambdaQueryWrapper<Customer> buildWrapper(CustomerQuery query) {
        // 查询条件保持轻量，只按名称和电话做模糊匹配。
        return new LambdaQueryWrapper<Customer>()
                .like(StrUtil.isNotBlank(query.getName()), Customer::getName, query.getName())
                .like(StrUtil.isNotBlank(query.getPhone()), Customer::getPhone, query.getPhone())
                .orderByDesc(Customer::getId);
    }
}
