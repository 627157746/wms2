package com.zhb.wms2.module.base.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhb.wms2.module.base.model.entity.ProductCategory;
import com.zhb.wms2.module.base.model.vo.ProductCategoryTreeVO;
import java.util.List;

/**
 * @Author zhb
 * @Description
 * @Date 2026/3/17 19:02
 */
public interface ProductCategoryService extends IService<ProductCategory> {

    void saveChecked(ProductCategory category);

    List<ProductCategoryTreeVO> tree();

    void updateByIdChecked(ProductCategory category);

    void removeByIdChecked(Long id);
}
