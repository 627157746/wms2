package com.zhb.wms2.module.base.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhb.wms2.module.base.model.entity.ProductCategory;
import com.zhb.wms2.module.base.model.query.ProductCategoryQuery;
import java.util.List;
    /**
 * @Author zhb
 * @Description 
 * @Date 2026/3/17 19:02
 */
public interface ProductCategoryService extends IService<ProductCategory> {

    IPage<ProductCategory> pageQuery(ProductCategoryQuery query);

    List<ProductCategory> listAll();

    void removeByIdChecked(Long id);
}
