package com.zhb.wms2.module.base.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhb.wms2.common.exception.BaseException;
import com.zhb.wms2.module.base.mapper.ProductLocationMapper;
import com.zhb.wms2.module.base.model.entity.ProductLocation;
import com.zhb.wms2.module.base.model.query.ProductLocationQuery;
import com.zhb.wms2.module.base.service.ProductLocationService;
import com.zhb.wms2.module.base.service.support.BaseDictMapStore;
import com.zhb.wms2.module.io.model.entity.IoOrderDetail;
import com.zhb.wms2.module.io.service.IoOrderDetailService;
import com.zhb.wms2.module.product.mapper.ProductMapper;
import com.zhb.wms2.module.product.model.entity.Product;
import com.zhb.wms2.module.product.model.entity.ProductStockDetail;
import com.zhb.wms2.module.product.service.ProductStockDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author zhb
 * @Description
 * @Date 2026/3/17 19:02
 */
@Service
@RequiredArgsConstructor
public class ProductLocationServiceImpl extends ServiceImpl<ProductLocationMapper, ProductLocation> implements ProductLocationService {

    private final IoOrderDetailService ioOrderDetailService;
    private final ProductStockDetailService productStockDetailService;
    private final ProductMapper productMapper;
    private final BaseDictMapStore baseDictMapStore;

    @Override
    public void saveChecked(ProductLocation location) {
        validateCodeUnique(location.getCode(), null);
        if (!super.save(location)) {
            throw new BaseException("商品货位新增失败");
        }
        baseDictMapStore.clearProductLocationMap();
    }

    @Override
    public IPage<ProductLocation> pageQuery(ProductLocationQuery query) {
        LambdaQueryWrapper<ProductLocation> wrapper = buildWrapper(query);
        return page(new Page<>(query.getCurrent(), query.getSize()), wrapper);
    }

    @Override
    public List<ProductLocation> listAll() {
        return list(new LambdaQueryWrapper<ProductLocation>().orderByDesc(ProductLocation::getId));
    }

    @Override
    public void updateByIdChecked(ProductLocation location) {
        if (getById(location.getId()) == null) {
            throw new BaseException("商品货位不存在");
        }
        validateCodeUnique(location.getCode(), location.getId());
        if (!updateById(location)) {
            throw new BaseException("商品货位不存在");
        }
        baseDictMapStore.clearProductLocationMap();
    }

    @Override
    public void removeByIdChecked(Long id) {
        long ioOrderDetailCount = ioOrderDetailService.count(
                new LambdaQueryWrapper<IoOrderDetail>().eq(IoOrderDetail::getLocationId, id));
        if (ioOrderDetailCount > 0) {
            throw new BaseException("该货位已被出入库记录使用，无法删除");
        }
        long stockDetailCount = productStockDetailService.count(
                new LambdaQueryWrapper<ProductStockDetail>().eq(ProductStockDetail::getLocationId, id));
        if (stockDetailCount > 0) {
            throw new BaseException("该货位已被库存明细使用，无法删除");
        }
        long productStockCount = productMapper.selectCount(
                new LambdaQueryWrapper<Product>().apply("FIND_IN_SET({0}, location_ids_str)", id));
        if (productStockCount > 0) {
            throw new BaseException("该货位已被商品库存使用，无法删除");
        }
        long productCount = productMapper.selectCount(
                new LambdaQueryWrapper<Product>().eq(Product::getInitialStockLocationId, id));
        if (productCount > 0) {
            throw new BaseException("该货位已被商品期初库存使用，无法删除");
        }
        if (!removeById(id)) {
            throw new BaseException("商品货位不存在");
        }
        baseDictMapStore.clearProductLocationMap();
    }

    private LambdaQueryWrapper<ProductLocation> buildWrapper(ProductLocationQuery query) {
        return new LambdaQueryWrapper<ProductLocation>()
                .like(StrUtil.isNotBlank(query.getCode()), ProductLocation::getCode, query.getCode())
                .orderByDesc(ProductLocation::getId);
    }

    private void validateCodeUnique(String code, Long excludeId) {
        LambdaQueryWrapper<ProductLocation> wrapper = new LambdaQueryWrapper<ProductLocation>()
                .eq(ProductLocation::getCode, code)
                .ne(excludeId != null, ProductLocation::getId, excludeId);
        if (count(wrapper) > 0) {
            throw new BaseException("货位编码已存在");
        }
    }
}
