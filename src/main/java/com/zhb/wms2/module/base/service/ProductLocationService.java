package com.zhb.wms2.module.base.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhb.wms2.module.base.model.dto.BaseSortUpdateDTO;
import com.zhb.wms2.module.base.model.entity.ProductLocation;
import com.zhb.wms2.module.base.model.query.ProductLocationQuery;
import java.util.List;

/**
 * ProductLocationService 服务接口
 *
 * @author zhb
 * @since 2026/3/26
 */
public interface ProductLocationService extends IService<ProductLocation> {

    /**
     * 新增商品货位。
     */
    void saveChecked(ProductLocation location);

    /**
     * 分页查询商品货位。
     */
    IPage<ProductLocation> pageQuery(ProductLocationQuery query);

    /**
     * 查询全部商品货位。
     */
    List<ProductLocation> listAll();

    /**
     * 修改商品货位。
     */
    void updateByIdChecked(ProductLocation location);

    /**
     * 批量修改排序。
     */
    void updateSortOrderBatch(List<BaseSortUpdateDTO> dtoList);

    /**
     * 删除商品货位。
     */
    void removeByIdChecked(Long id);
}
