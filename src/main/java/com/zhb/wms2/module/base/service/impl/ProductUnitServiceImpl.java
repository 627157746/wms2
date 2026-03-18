package com.zhb.wms2.module.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhb.wms2.common.exception.BaseException;
import com.zhb.wms2.module.base.mapper.ProductUnitMapper;
import com.zhb.wms2.module.base.model.entity.ProductUnit;
import com.zhb.wms2.module.base.model.query.ProductUnitQuery;
import com.zhb.wms2.module.base.service.ProductUnitService;
import com.zhb.wms2.module.product.model.entity.Product;
import com.zhb.wms2.module.product.service.ProductService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @Author zhb
 * @Description
 * @Date 2026/3/17 19:02
 */
@Service
@RequiredArgsConstructor
public class ProductUnitServiceImpl extends ServiceImpl<ProductUnitMapper, ProductUnit> implements ProductUnitService {

    private final ProductService productService;

    @Override
    public IPage<ProductUnit> pageQuery(ProductUnitQuery query) {
        LambdaQueryWrapper<ProductUnit> wrapper = buildWrapper(query);
        return page(new Page<>(query.getCurrent(), query.getSize()), wrapper);
    }

    @Override
    public List<ProductUnit> listAll() {
        return list(new LambdaQueryWrapper<ProductUnit>().orderByDesc(ProductUnit::getId));
    }

    @Override
    public void removeByIdChecked(Long id) {
        long count = productService.count(new LambdaQueryWrapper<Product>().eq(Product::getUnitId, id));
        if (count > 0) {
            throw new BaseException("该单位已被商品使用，无法删除");
        }
        removeById(id);
    }

    private LambdaQueryWrapper<ProductUnit> buildWrapper(ProductUnitQuery query) {
        return new LambdaQueryWrapper<ProductUnit>()
                .like(StringUtils.hasText(query.getName()), ProductUnit::getName, query.getName())
                .orderByDesc(ProductUnit::getId);
    }
}
