package com.zhb.wms2.module.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zhb.wms2.module.base.mapper.CustomerMapper;
import com.zhb.wms2.module.base.mapper.DeliverymanMapper;
import com.zhb.wms2.module.base.mapper.IoTypeMapper;
import com.zhb.wms2.module.base.mapper.ProductCategoryMapper;
import com.zhb.wms2.module.base.mapper.ProductLocationMapper;
import com.zhb.wms2.module.base.mapper.ProductUnitMapper;
import com.zhb.wms2.module.base.mapper.SalesmanMapper;
import com.zhb.wms2.module.base.model.dto.BaseDictMapDTO;
import com.zhb.wms2.module.base.model.entity.Customer;
import com.zhb.wms2.module.base.model.entity.Deliveryman;
import com.zhb.wms2.module.base.model.entity.IoType;
import com.zhb.wms2.module.base.model.entity.ProductCategory;
import com.zhb.wms2.module.base.model.entity.ProductLocation;
import com.zhb.wms2.module.base.model.entity.ProductUnit;
import com.zhb.wms2.module.base.model.entity.Salesman;
import com.zhb.wms2.module.base.service.BaseDictMapService;
import com.zhb.wms2.module.base.service.support.BaseDictMapStore;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * base 模块字典映射服务实现
 *
 * @author zhb
 * @since 2026/3/18
 */
@Service
@RequiredArgsConstructor
public class BaseDictMapServiceImpl implements BaseDictMapService {

    private final CustomerMapper customerMapper;
    private final DeliverymanMapper deliverymanMapper;
    private final SalesmanMapper salesmanMapper;
    private final IoTypeMapper ioTypeMapper;
    private final ProductCategoryMapper productCategoryMapper;
    private final ProductLocationMapper productLocationMapper;
    private final ProductUnitMapper productUnitMapper;
    private final BaseDictMapStore baseDictMapStore;

    /**
     * 汇总 base 模块所有基础资料映射。
     */
    @Override
    public BaseDictMapDTO getBaseDictMap() {
        // 统一从缓存访问层汇总基础资料，避免各业务模块重复查表。
        return new BaseDictMapDTO()
                .setCustomerMap(getCustomerMap())
                .setDeliverymanMap(getDeliverymanMap())
                .setSalesmanMap(getSalesmanMap())
                .setIoTypeMap(getIoTypeMap())
                .setProductCategoryMap(getProductCategoryMap())
                .setProductLocationMap(getProductLocationMap())
                .setProductUnitMap(getProductUnitMap());
    }

    /**
     * 获取客户字典缓存，不存在时从数据库加载。
     */
    private Map<Long, Customer> getCustomerMap() {
        Map<Long, Customer> customerMap = baseDictMapStore.getCustomerMap();
        if (customerMap != null) {
            return customerMap;
        }
        synchronized (baseDictMapStore) {
            // 双重检查避免并发场景下重复回源数据库。
            customerMap = baseDictMapStore.getCustomerMap();
            if (customerMap == null) {
                customerMap = buildMap(customerMapper.selectList(new LambdaQueryWrapper<Customer>()
                        .orderByDesc(Customer::getId)), Customer::getId);
                baseDictMapStore.setCustomerMap(customerMap);
            }
            return customerMap;
        }
    }

    /**
     * 获取送货员字典缓存，不存在时从数据库加载。
     */
    private Map<Long, Deliveryman> getDeliverymanMap() {
        Map<Long, Deliveryman> deliverymanMap = baseDictMapStore.getDeliverymanMap();
        if (deliverymanMap != null) {
            return deliverymanMap;
        }
        synchronized (baseDictMapStore) {
            // 双重检查避免并发场景下重复回源数据库。
            deliverymanMap = baseDictMapStore.getDeliverymanMap();
            if (deliverymanMap == null) {
                deliverymanMap = buildMap(deliverymanMapper.selectList(new LambdaQueryWrapper<Deliveryman>()
                        .orderByDesc(Deliveryman::getId)), Deliveryman::getId);
                baseDictMapStore.setDeliverymanMap(deliverymanMap);
            }
            return deliverymanMap;
        }
    }

    /**
     * 获取业务员字典缓存，不存在时从数据库加载。
     */
    private Map<Long, Salesman> getSalesmanMap() {
        Map<Long, Salesman> salesmanMap = baseDictMapStore.getSalesmanMap();
        if (salesmanMap != null) {
            return salesmanMap;
        }
        synchronized (baseDictMapStore) {
            // 双重检查避免并发场景下重复回源数据库。
            salesmanMap = baseDictMapStore.getSalesmanMap();
            if (salesmanMap == null) {
                salesmanMap = buildMap(salesmanMapper.selectList(new LambdaQueryWrapper<Salesman>()
                        .orderByDesc(Salesman::getId)), Salesman::getId);
                baseDictMapStore.setSalesmanMap(salesmanMap);
            }
            return salesmanMap;
        }
    }

    /**
     * 获取出入库类型字典缓存，不存在时从数据库加载。
     */
    private Map<Long, IoType> getIoTypeMap() {
        Map<Long, IoType> ioTypeMap = baseDictMapStore.getIoTypeMap();
        if (ioTypeMap != null) {
            return ioTypeMap;
        }
        synchronized (baseDictMapStore) {
            // 双重检查避免并发场景下重复回源数据库。
            ioTypeMap = baseDictMapStore.getIoTypeMap();
            if (ioTypeMap == null) {
                ioTypeMap = buildMap(ioTypeMapper.selectList(new LambdaQueryWrapper<IoType>()
                        .orderByDesc(IoType::getId)), IoType::getId);
                baseDictMapStore.setIoTypeMap(ioTypeMap);
            }
            return ioTypeMap;
        }
    }

    /**
     * 获取商品分类字典缓存，不存在时从数据库加载。
     */
    private Map<Long, ProductCategory> getProductCategoryMap() {
        Map<Long, ProductCategory> productCategoryMap = baseDictMapStore.getProductCategoryMap();
        if (productCategoryMap != null) {
            return productCategoryMap;
        }
        synchronized (baseDictMapStore) {
            // 双重检查避免并发场景下重复回源数据库。
            productCategoryMap = baseDictMapStore.getProductCategoryMap();
            if (productCategoryMap == null) {
                productCategoryMap = buildMap(productCategoryMapper.selectList(new LambdaQueryWrapper<ProductCategory>()
                        .orderByDesc(ProductCategory::getId)), ProductCategory::getId);
                baseDictMapStore.setProductCategoryMap(productCategoryMap);
            }
            return productCategoryMap;
        }
    }

    /**
     * 获取商品货位字典缓存，不存在时从数据库加载。
     */
    private Map<Long, ProductLocation> getProductLocationMap() {
        Map<Long, ProductLocation> productLocationMap = baseDictMapStore.getProductLocationMap();
        if (productLocationMap != null) {
            return productLocationMap;
        }
        synchronized (baseDictMapStore) {
            // 双重检查避免并发场景下重复回源数据库。
            productLocationMap = baseDictMapStore.getProductLocationMap();
            if (productLocationMap == null) {
                productLocationMap = buildMap(productLocationMapper.selectList(new LambdaQueryWrapper<ProductLocation>()
                        .orderByDesc(ProductLocation::getId)), ProductLocation::getId);
                baseDictMapStore.setProductLocationMap(productLocationMap);
            }
            return productLocationMap;
        }
    }

    /**
     * 获取商品单位字典缓存，不存在时从数据库加载。
     */
    private Map<Long, ProductUnit> getProductUnitMap() {
        Map<Long, ProductUnit> productUnitMap = baseDictMapStore.getProductUnitMap();
        if (productUnitMap != null) {
            return productUnitMap;
        }
        synchronized (baseDictMapStore) {
            // 双重检查避免并发场景下重复回源数据库。
            productUnitMap = baseDictMapStore.getProductUnitMap();
            if (productUnitMap == null) {
                productUnitMap = buildMap(productUnitMapper.selectList(new LambdaQueryWrapper<ProductUnit>()
                        .orderByDesc(ProductUnit::getId)), ProductUnit::getId);
                baseDictMapStore.setProductUnitMap(productUnitMap);
            }
            return productUnitMap;
        }
    }

    /**
     * 按主键构建只读字典映射。
     */
    private <T> Map<Long, T> buildMap(List<T> dataList, Function<T, Long> idGetter) {
        Map<Long, T> dataMap = new LinkedHashMap<>();
        for (T data : dataList) {
            // 只收集存在主键的数据，避免异常脏数据污染缓存。
            Long id = idGetter.apply(data);
            if (id != null) {
                dataMap.put(id, data);
            }
        }
        return Collections.unmodifiableMap(dataMap);
    }
}
