package com.zhb.wms2.module.base.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhb.wms2.module.base.model.entity.Customer;
import com.zhb.wms2.module.base.model.query.CustomerQuery;
import java.util.List;
    /**
 * @Author zhb
 * @Description 
 * @Date 2026/3/17 18:57
 */
public interface CustomerService extends IService<Customer> {

    void saveChecked(Customer customer);

    IPage<Customer> pageQuery(CustomerQuery query);

    List<Customer> listAll();

    void updateByIdChecked(Customer customer);

    void removeByIdChecked(Long id);
}
