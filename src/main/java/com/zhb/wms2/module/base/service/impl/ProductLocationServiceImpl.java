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
 * ProductLocationServiceImpl 服务实现
 *
 * @author zhb
 * @since 2026/3/26
 */
@Service
@RequiredArgsConstructor
public class ProductLocationServiceImpl extends ServiceImpl<ProductLocationMapper, ProductLocation> implements ProductLocationService {

    private final IoOrderDetailService ioOrderDetailService;
    private final ProductStockDetailService productStockDetailService;
    private final ProductMapper productMapper;
    private final BaseDictMapStore baseDictMapStore;

    /**
     * 新增商品货位并校验编码唯一。
     */
    @Override
    public void saveChecked(ProductLocation location) {
        // 货位编码用于库存展示和明细录入，新增前先保证唯一。
        validateCodeUnique(location.getCode(), null);
        if (!super.save(location)) {
            throw new BaseException("商品货位新增失败");
        }
        // 货位属于全局字典，新增后立即清缓存。
        baseDictMapStore.clearProductLocationMap();
    }

    /**
     * 分页查询商品货位。
     */
    @Override
    public IPage<ProductLocation> pageQuery(ProductLocationQuery query) {
        // 货位列表只按编码过滤，其他展示由调用方自行处理。
        LambdaQueryWrapper<ProductLocation> wrapper = buildWrapper(query);
        return page(new Page<>(query.getCurrent(), query.getSize()), wrapper);
    }

    /**
     * 查询全部商品货位。
     */
    @Override
    public List<ProductLocation> listAll() {
        // 基础资料下拉直接读取全量货位。
        return list(new LambdaQueryWrapper<ProductLocation>().orderByDesc(ProductLocation::getId));
    }

    /**
     * 修改商品货位并校验编码唯一。
     */
    @Override
    public void updateByIdChecked(ProductLocation location) {
        if (getById(location.getId()) == null) {
            throw new BaseException("商品货位不存在");
        }
        // 修改时排除自身后再校验编码唯一。
        validateCodeUnique(location.getCode(), location.getId());
        if (!updateById(location)) {
            throw new BaseException("商品货位不存在");
        }
        // 修改后清缓存，保证单据录入和库存展示都拿到新编码。
        baseDictMapStore.clearProductLocationMap();
    }

    /**
     * 删除货位前校验是否被出入库记录、库存明细或商品库存引用。
     */
    @Override
    public void removeByIdChecked(Long id) {
        // 货位会被单据明细、库存明细和商品汇总字符串同时引用。
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
        if (!removeById(id)) {
            throw new BaseException("商品货位不存在");
        }
        // 删除后同步失效缓存。
        baseDictMapStore.clearProductLocationMap();
    }

    /**
     * 构建商品货位分页查询条件。
     */
    private LambdaQueryWrapper<ProductLocation> buildWrapper(ProductLocationQuery query) {
        // 货位查询保持简单，只支持按编码模糊搜索。
        return new LambdaQueryWrapper<ProductLocation>()
                .like(StrUtil.isNotBlank(query.getCode()), ProductLocation::getCode, query.getCode())
                .orderByDesc(ProductLocation::getId);
    }

    /**
     * 校验货位编码唯一。
     */
    private void validateCodeUnique(String code, Long excludeId) {
        // 编辑场景排除当前记录，避免误报重复。
        LambdaQueryWrapper<ProductLocation> wrapper = new LambdaQueryWrapper<ProductLocation>()
                .eq(ProductLocation::getCode, code)
                .ne(excludeId != null, ProductLocation::getId, excludeId);
        if (count(wrapper) > 0) {
            throw new BaseException("货位编码已存在");
        }
    }
}
