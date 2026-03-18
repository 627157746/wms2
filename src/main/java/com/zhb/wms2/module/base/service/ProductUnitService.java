package com.zhb.wms2.module.base.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhb.wms2.module.base.model.entity.ProductUnit;
import com.zhb.wms2.module.base.model.query.ProductUnitQuery;
import java.util.List;
    /**
 * @Author zhb
 * @Description 
 * @Date 2026/3/17 19:02
 */
public interface ProductUnitService extends IService<ProductUnit> {

    IPage<ProductUnit> pageQuery(ProductUnitQuery query);

    List<ProductUnit> listAll();

    void updateByIdChecked(ProductUnit unit);

    void removeByIdChecked(Long id);
}
