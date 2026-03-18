package com.zhb.wms2.module.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhb.wms2.common.exception.BaseException;
import com.zhb.wms2.module.base.mapper.ProductCategoryMapper;
import com.zhb.wms2.module.base.model.entity.ProductCategory;
import com.zhb.wms2.module.base.model.query.ProductCategoryQuery;
import com.zhb.wms2.module.base.service.ProductCategoryService;
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
public class ProductCategoryServiceImpl extends ServiceImpl<ProductCategoryMapper, ProductCategory> implements ProductCategoryService {

    private final ProductService productService;

    @Override
    public IPage<ProductCategory> pageQuery(ProductCategoryQuery query) {
        LambdaQueryWrapper<ProductCategory> wrapper = buildWrapper(query);
        return page(new Page<>(query.getCurrent(), query.getSize()), wrapper);
    }

    @Override
    public List<ProductCategory> listAll() {
        return list(new LambdaQueryWrapper<ProductCategory>().orderByDesc(ProductCategory::getId));
    }

    @Override
    public void removeByIdChecked(Long id) {
        long count = productService.count(new LambdaQueryWrapper<Product>().eq(Product::getCategoryId, id));
        if (count > 0) {
            throw new BaseException("该分类已被商品使用，无法删除");
        }
        removeById(id);
    }

    private LambdaQueryWrapper<ProductCategory> buildWrapper(ProductCategoryQuery query) {
        return new LambdaQueryWrapper<ProductCategory>()
                .like(StringUtils.hasText(query.getName()), ProductCategory::getName, query.getName())
                .eq(query.getParentId() != null, ProductCategory::getParentId, query.getParentId())
                .orderByDesc(ProductCategory::getId);
    }
}
