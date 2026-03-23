package com.zhb.wms2.module.product.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhb.wms2.module.product.model.entity.Product;
import com.zhb.wms2.module.product.model.query.StockDistributionQuery;
import com.zhb.wms2.module.product.model.query.ProductQuery;
import com.zhb.wms2.module.product.model.vo.StockDistributionGroupVO;
import com.zhb.wms2.module.product.model.vo.ProductPageVO;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @Author zhb
 * @Description
 * @Date 2026/3/17 19:07
 */
public interface ProductService extends IService<Product> {

    void saveChecked(Product product);

    IPage<ProductPageVO> pageQuery(ProductQuery query);

    ProductPageVO getDetailById(Long id);

    Map<Long, ProductPageVO> getDetailMapByIds(Collection<Long> ids);

    List<StockDistributionGroupVO> listDistribution(StockDistributionQuery query);

    void removeByIdChecked(Long id);

    void updateByIdChecked(Product product);

}
