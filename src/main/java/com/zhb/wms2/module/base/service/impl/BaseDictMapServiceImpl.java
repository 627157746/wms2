package com.zhb.wms2.module.base.service.impl;

import com.zhb.wms2.module.base.model.dto.BaseDictMapDTO;
import com.zhb.wms2.module.base.model.entity.Customer;
import com.zhb.wms2.module.base.model.entity.Deliveryman;
import com.zhb.wms2.module.base.model.entity.IoType;
import com.zhb.wms2.module.base.model.entity.ProductCategory;
import com.zhb.wms2.module.base.model.entity.ProductLocation;
import com.zhb.wms2.module.base.model.entity.ProductUnit;
import com.zhb.wms2.module.base.service.BaseDictMapService;
import com.zhb.wms2.module.base.service.CustomerService;
import com.zhb.wms2.module.base.service.DeliverymanService;
import com.zhb.wms2.module.base.service.IoTypeService;
import com.zhb.wms2.module.base.service.ProductCategoryService;
import com.zhb.wms2.module.base.service.ProductLocationService;
import com.zhb.wms2.module.base.service.ProductUnitService;
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

    private final CustomerService customerService;
    private final DeliverymanService deliverymanService;
    private final IoTypeService ioTypeService;
    private final ProductCategoryService productCategoryService;
    private final ProductLocationService productLocationService;
    private final ProductUnitService productUnitService;
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
                customerMap = buildMap(customerService.listAll(), Customer::getId);
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
                deliverymanMap = buildMap(deliverymanService.listAll(), Deliveryman::getId);
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
                ioTypeMap = buildMap(ioTypeService.listAll(), IoType::getId);
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
                productCategoryMap = buildMap(productCategoryService.list(), ProductCategory::getId);
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
                productLocationMap = buildMap(productLocationService.listAll(), ProductLocation::getId);
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
                productUnitMap = buildMap(productUnitService.listAll(), ProductUnit::getId);
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
