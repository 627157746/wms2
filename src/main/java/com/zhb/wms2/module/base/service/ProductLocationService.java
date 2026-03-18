package com.zhb.wms2.module.base.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhb.wms2.module.base.model.entity.ProductLocation;
import com.zhb.wms2.module.base.model.query.ProductLocationQuery;
import java.util.List;
    /**
 * @Author zhb
 * @Description 
 * @Date 2026/3/17 19:02
 */
public interface ProductLocationService extends IService<ProductLocation> {

    IPage<ProductLocation> pageQuery(ProductLocationQuery query);

    List<ProductLocation> listAll();

    void updateByIdChecked(ProductLocation location);

    void removeByIdChecked(Long id);
}
