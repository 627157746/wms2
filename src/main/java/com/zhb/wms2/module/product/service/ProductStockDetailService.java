package com.zhb.wms2.module.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhb.wms2.module.product.model.entity.ProductStockDetail;
import com.zhb.wms2.module.product.model.vo.ProductStockDetailVO;

import java.util.List;

/**
 * ProductStockDetailService 服务接口
 *
 * @author zhb
 * @since 2026/3/26
 */
public interface ProductStockDetailService extends IService<ProductStockDetail> {

    /**
     * 新增库存明细。
     */
    void saveChecked(ProductStockDetail detail);

    /**
     * 修改库存明细。
     */
    void updateByIdChecked(ProductStockDetail detail);

    /**
     * 查询商品库存明细。
     */
    List<ProductStockDetailVO> listByProductId(Long productId);

    /**
     * 删除库存明细。
     */
    void removeByIdChecked(Long id);

}
