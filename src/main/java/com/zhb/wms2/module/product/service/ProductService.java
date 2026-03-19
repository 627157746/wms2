package com.zhb.wms2.module.product.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhb.wms2.module.product.model.entity.Product;
import com.zhb.wms2.module.product.model.query.ProductQuery;

/**
 * @Author zhb
 * @Description
 * @Date 2026/3/17 19:07
 */
public interface ProductService extends IService<Product> {

    void saveChecked(Product product);

    IPage<? extends Product> pageQuery(ProductQuery query);

    void removeByIdChecked(Long id);

    void updateByIdChecked(Product product);

}
