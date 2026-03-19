package com.zhb.wms2.module.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zhb.wms2.module.base.mapper.CustomerMapper;
import com.zhb.wms2.module.base.mapper.DeliverymanMapper;
import com.zhb.wms2.module.base.mapper.IoTypeMapper;
import com.zhb.wms2.module.base.mapper.ProductCategoryMapper;
import com.zhb.wms2.module.base.mapper.ProductLocationMapper;
import com.zhb.wms2.module.base.mapper.ProductUnitMapper;
import com.zhb.wms2.module.base.model.dto.BaseDictMapDTO;
import com.zhb.wms2.module.base.model.entity.Customer;
import com.zhb.wms2.module.base.model.entity.Deliveryman;
import com.zhb.wms2.module.base.model.entity.IoType;
import com.zhb.wms2.module.base.model.entity.ProductCategory;
import com.zhb.wms2.module.base.model.entity.ProductLocation;
import com.zhb.wms2.module.base.model.entity.ProductUnit;
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
    private final IoTypeMapper ioTypeMapper;
    private final ProductCategoryMapper productCategoryMapper;
    private final ProductLocationMapper productLocationMapper;
    private final ProductUnitMapper productUnitMapper;
    private final BaseDictMapStore baseDictMapStore;

    @Override
    public BaseDictMapDTO getBaseDictMap() {
        BaseDictMapDTO dto = new BaseDictMapDTO();
        dto.setCustomerMap(getCustomerMap());
        dto.setDeliverymanMap(getDeliverymanMap());
        dto.setIoTypeMap(getIoTypeMap());
        dto.setProductCategoryMap(getProductCategoryMap());
        dto.setProductLocationMap(getProductLocationMap());
        dto.setProductUnitMap(getProductUnitMap());
        return dto;
    }

    private Map<Long, Customer> getCustomerMap() {
        Map<Long, Customer> customerMap = baseDictMapStore.getCustomerMap();
        if (customerMap != null) {
            return customerMap;
        }
        synchronized (baseDictMapStore) {
            customerMap = baseDictMapStore.getCustomerMap();
            if (customerMap == null) {
                customerMap = buildMap(customerMapper.selectList(new LambdaQueryWrapper<Customer>()
                        .orderByDesc(Customer::getId)), Customer::getId);
                baseDictMapStore.setCustomerMap(customerMap);
            }
            return customerMap;
        }
    }

    private Map<Long, Deliveryman> getDeliverymanMap() {
        Map<Long, Deliveryman> deliverymanMap = baseDictMapStore.getDeliverymanMap();
        if (deliverymanMap != null) {
            return deliverymanMap;
        }
        synchronized (baseDictMapStore) {
            deliverymanMap = baseDictMapStore.getDeliverymanMap();
            if (deliverymanMap == null) {
                deliverymanMap = buildMap(deliverymanMapper.selectList(new LambdaQueryWrapper<Deliveryman>()
                        .orderByDesc(Deliveryman::getId)), Deliveryman::getId);
                baseDictMapStore.setDeliverymanMap(deliverymanMap);
            }
            return deliverymanMap;
        }
    }

    private Map<Long, IoType> getIoTypeMap() {
        Map<Long, IoType> ioTypeMap = baseDictMapStore.getIoTypeMap();
        if (ioTypeMap != null) {
            return ioTypeMap;
        }
        synchronized (baseDictMapStore) {
            ioTypeMap = baseDictMapStore.getIoTypeMap();
            if (ioTypeMap == null) {
                ioTypeMap = buildMap(ioTypeMapper.selectList(new LambdaQueryWrapper<IoType>()
                        .orderByDesc(IoType::getId)), IoType::getId);
                baseDictMapStore.setIoTypeMap(ioTypeMap);
            }
            return ioTypeMap;
        }
    }

    private Map<Long, ProductCategory> getProductCategoryMap() {
        Map<Long, ProductCategory> productCategoryMap = baseDictMapStore.getProductCategoryMap();
        if (productCategoryMap != null) {
            return productCategoryMap;
        }
        synchronized (baseDictMapStore) {
            productCategoryMap = baseDictMapStore.getProductCategoryMap();
            if (productCategoryMap == null) {
                productCategoryMap = buildMap(productCategoryMapper.selectList(new LambdaQueryWrapper<ProductCategory>()
                        .orderByDesc(ProductCategory::getId)), ProductCategory::getId);
                baseDictMapStore.setProductCategoryMap(productCategoryMap);
            }
            return productCategoryMap;
        }
    }

    private Map<Long, ProductLocation> getProductLocationMap() {
        Map<Long, ProductLocation> productLocationMap = baseDictMapStore.getProductLocationMap();
        if (productLocationMap != null) {
            return productLocationMap;
        }
        synchronized (baseDictMapStore) {
            productLocationMap = baseDictMapStore.getProductLocationMap();
            if (productLocationMap == null) {
                productLocationMap = buildMap(productLocationMapper.selectList(new LambdaQueryWrapper<ProductLocation>()
                        .orderByDesc(ProductLocation::getId)), ProductLocation::getId);
                baseDictMapStore.setProductLocationMap(productLocationMap);
            }
            return productLocationMap;
        }
    }

    private Map<Long, ProductUnit> getProductUnitMap() {
        Map<Long, ProductUnit> productUnitMap = baseDictMapStore.getProductUnitMap();
        if (productUnitMap != null) {
            return productUnitMap;
        }
        synchronized (baseDictMapStore) {
            productUnitMap = baseDictMapStore.getProductUnitMap();
            if (productUnitMap == null) {
                productUnitMap = buildMap(productUnitMapper.selectList(new LambdaQueryWrapper<ProductUnit>()
                        .orderByDesc(ProductUnit::getId)), ProductUnit::getId);
                baseDictMapStore.setProductUnitMap(productUnitMap);
            }
            return productUnitMap;
        }
    }

    private <T> Map<Long, T> buildMap(List<T> dataList, Function<T, Long> idGetter) {
        Map<Long, T> dataMap = new LinkedHashMap<>();
        for (T data : dataList) {
            Long id = idGetter.apply(data);
            if (id != null) {
                dataMap.put(id, data);
            }
        }
        return Collections.unmodifiableMap(dataMap);
    }
}
