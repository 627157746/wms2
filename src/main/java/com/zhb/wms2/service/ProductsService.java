package com.zhb.wms2.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhb.wms2.model.Products;
import com.zhb.wms2.model.dto.ProductsQuery;

/**
 * 商品信息服务接口
 *
 * @author zhb
 */
public interface ProductsService extends IService<Products> {

    /**
     * 添加商品信息
     */
    Long addProducts(Products products);

    /**
     * 修改商品信息
     */
    void updateProducts(Products products);

    /**
     * 分页查询商品信息
     */
    IPage<Products> queryPage(ProductsQuery query);

}
