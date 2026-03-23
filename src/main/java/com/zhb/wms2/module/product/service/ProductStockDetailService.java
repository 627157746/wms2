package com.zhb.wms2.module.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhb.wms2.module.product.model.entity.ProductStockDetail;
import com.zhb.wms2.module.product.model.vo.ProductStockDetailVO;

import java.util.List;

/**
 * @Author zhb
 * @Description 
 * @Date 2026/3/17 19:07
 */
public interface ProductStockDetailService extends IService<ProductStockDetail> {

    List<ProductStockDetailVO> listByProductId(Long productId);

}
