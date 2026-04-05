package com.zhb.wms2.module.product.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhb.wms2.module.product.model.entity.Product;
import com.zhb.wms2.module.product.model.query.ProductQuery;
import com.zhb.wms2.module.product.model.query.StockDistributionQuery;
import com.zhb.wms2.module.product.model.vo.StockDistributionGroupVO;
import com.zhb.wms2.module.product.model.vo.ProductPageVO;
import com.zhb.wms2.module.product.model.vo.ProductStockStatVO;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * ProductService 服务接口
 *
 * @author zhb
 * @since 2026/3/26
 */
public interface ProductService extends IService<Product> {

    /**
     * 新增商品。
     */
    void saveChecked(Product product);

    /**
     * 分页查询商品。
     */
    IPage<ProductPageVO> pageQuery(ProductQuery query);

    /**
     * 查询商品详情。
     */
    ProductPageVO getDetailById(Long id);

    /**
     * 查询商品库存汇总。
     */
    ProductStockStatVO getStockStat(ProductQuery query);

    /**
     * 批量查询商品详情映射。
     */
    Map<Long, ProductPageVO> getDetailMapByIds(Collection<Long> ids);

    /**
     * 查询商品库存分布。
     */
    List<StockDistributionGroupVO> listDistribution(StockDistributionQuery query);

    /**
     * 查询商品库存分布导出数据。
     */
    void exportDistribution(StockDistributionQuery query, HttpServletResponse response) throws IOException;

    /**
     * 删除商品。
     */
    void removeByIdChecked(Long id);

    /**
     * 修改商品。
     */
    void updateByIdChecked(Product product);

}
