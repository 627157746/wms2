package com.zhb.wms2.module.base.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhb.wms2.module.base.model.entity.Customer;
import com.zhb.wms2.module.base.model.query.CustomerQuery;
import java.util.List;

/**
 * CustomerService 服务接口
 *
 * @author zhb
 * @since 2026/3/26
 */
public interface CustomerService extends IService<Customer> {

    /**
     * 新增客户。
     */
    void saveChecked(Customer customer);

    /**
     * 分页查询客户。
     */
    IPage<Customer> pageQuery(CustomerQuery query);

    /**
     * 查询全部客户。
     */
    List<Customer> listAll();

    /**
     * 修改客户。
     */
    void updateByIdChecked(Customer customer);

    /**
     * 删除客户。
     */
    void removeByIdChecked(Long id);
}
