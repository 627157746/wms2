package com.zhb.wms2.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhb.wms2.common.exception.BaseException;
import com.zhb.wms2.mapper.ProductsMapper;
import com.zhb.wms2.model.Products;
import com.zhb.wms2.model.dto.ProductsQuery;
import com.zhb.wms2.service.ProductsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 商品信息服务实现类
 */
@Service
@RequiredArgsConstructor
public class ProductsServiceImpl extends ServiceImpl<ProductsMapper, Products> implements ProductsService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addProducts(Products products) {
        // 1. 业务规则检查
        checkBusinessRulesForCreate(products);

        // 2. 数据预处理
        preprocessProductsData(products);

        // 3. 保存数据
        save(products);

        return products.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProducts(Products products) {
        // 1. 业务规则检查
        checkBusinessRulesForUpdate(products);

        // 2. 数据预处理
        preprocessProductsData(products);

        // 3. 更新数据
        updateById(products);
    }

    @Override
    public IPage<Products> queryPage(ProductsQuery query) {
        // 1. 构建查询条件
        LambdaQueryWrapper<Products> wrapper = buildQueryWrapper(query);

        // 2. 执行分页查询
        Page<Products> page = new Page<>(query.getCurrent().intValue(), query.getSize().intValue());
        return page(page, wrapper);
    }

    /**
     * 检查商品编号是否存在
     */
    public boolean isProductCodeExists(String productCode, Long excludeId) {
        if (StrUtil.isBlank(productCode)) {
            return false;
        }

        LambdaQueryWrapper<Products> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Products::getProductCode, productCode);

        if (excludeId != null) {
            wrapper.ne(Products::getId, excludeId);
        }

        return count(wrapper) > 0;
    }

    /**
     * 创建商品时的业务规则检查
     */
    private void checkBusinessRulesForCreate(Products products) {
        // 检查商品编号唯一性
        if (isProductCodeExists(products.getProductCode(), null)) {
            throw new BaseException("商品编号已存在");
        }
    }

    /**
     * 更新商品时的业务规则检查
     */
    private void checkBusinessRulesForUpdate(Products products) {
        // 检查商品ID是否存在
        if (products.getId() == null) {
            throw new BaseException("商品ID不能为空");
        }

        // 检查商品是否存在
        Products existProduct = getById(products.getId());
        if (existProduct == null) {
            throw new BaseException("商品信息不存在");
        }

        // 如果修改了商品编号，检查是否重复
        if (!existProduct.getProductCode().equals(products.getProductCode())) {
            if (isProductCodeExists(products.getProductCode(), products.getId())) {
                throw new BaseException("商品编号已存在");
            }
        }
    }

    /**
     * 商品数据预处理
     */
    private void preprocessProductsData(Products products) {
        // 去除前后空格
        if (StrUtil.isNotBlank(products.getProductCode())) {
            products.setProductCode(products.getProductCode().trim());
        }
        if (StrUtil.isNotBlank(products.getProductName())) {
            products.setProductName(products.getProductName().trim());
        }
        if (StrUtil.isNotBlank(products.getBrand())) {
            products.setBrand(products.getBrand().trim());
        }
        if (StrUtil.isNotBlank(products.getSpecification())) {
            products.setSpecification(products.getSpecification().trim());
        }
    }

    /**
     * 构建查询条件
     */
    private LambdaQueryWrapper<Products> buildQueryWrapper(ProductsQuery query) {
        LambdaQueryWrapper<Products> wrapper = new LambdaQueryWrapper<>();

        // 精确查询
        wrapper.eq(StrUtil.isNotBlank(query.getProductCode()), Products::getProductCode, query.getProductCode());

        // 模糊查询
        wrapper.like(StrUtil.isNotBlank(query.getProductName()), Products::getProductName, query.getProductName())
               .like(StrUtil.isNotBlank(query.getBrand()), Products::getBrand, query.getBrand())
               .like(StrUtil.isNotBlank(query.getSpecification()), Products::getSpecification, query.getSpecification());

        // 排序
        wrapper.orderByDesc(Products::getCreateTime);

        return wrapper;
    }
}
