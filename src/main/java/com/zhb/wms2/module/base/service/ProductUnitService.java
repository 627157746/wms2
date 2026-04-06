package com.zhb.wms2.module.base.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhb.wms2.module.base.model.dto.BaseSortUpdateDTO;
import com.zhb.wms2.module.base.model.entity.ProductUnit;
import com.zhb.wms2.module.base.model.query.ProductUnitQuery;
import java.util.List;

/**
 * ProductUnitService 服务接口
 *
 * @author zhb
 * @since 2026/3/26
 */
public interface ProductUnitService extends IService<ProductUnit> {

    /**
     * 新增商品单位。
     */
    void saveChecked(ProductUnit unit);

    /**
     * 分页查询商品单位。
     */
    IPage<ProductUnit> pageQuery(ProductUnitQuery query);

    /**
     * 查询全部商品单位。
     */
    List<ProductUnit> listAll();

    /**
     * 修改商品单位。
     */
    void updateByIdChecked(ProductUnit unit);

    /**
     * 批量修改排序。
     */
    void updateSortOrderBatch(List<BaseSortUpdateDTO> dtoList);

    /**
     * 删除商品单位。
     */
    void removeByIdChecked(Long id);
}
