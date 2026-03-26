package com.zhb.wms2.module.base.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhb.wms2.module.base.model.dto.ProductCategorySortDTO;
import com.zhb.wms2.module.base.model.entity.ProductCategory;
import com.zhb.wms2.module.base.model.vo.ProductCategoryTreeVO;
import java.util.List;

/**
 * ProductCategoryService 服务接口
 *
 * @author zhb
 * @since 2026/3/26
 */
public interface ProductCategoryService extends IService<ProductCategory> {

    /**
     * 新增商品分类。
     */
    void saveChecked(ProductCategory category);

    /**
     * 查询商品分类树。
     */
    List<ProductCategoryTreeVO> tree();

    /**
     * 修改商品分类。
     */
    void updateByIdChecked(ProductCategory category);

    /**
     * 删除商品分类。
     */
    void removeByIdChecked(Long id);

    /**
     * 对同级分类重新排序。
     */
    void sortSameLevel(ProductCategorySortDTO dto);
}
