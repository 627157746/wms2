package com.zhb.wms2.module.base.service.support;

import com.zhb.wms2.module.base.model.entity.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * base 模块字典映射缓存
 *
 * @author zhb
 * @since 2026/3/18
 */
@Setter
@Getter
@Component
public class BaseDictMapStore {

    private volatile Map<Long, Customer> customerMap;

    private volatile Map<Long, Deliveryman> deliverymanMap;

    private volatile Map<Long, IoType> ioTypeMap;

    private volatile Map<Long, ProductCategory> productCategoryMap;

    private volatile Map<Long, ProductLocation> productLocationMap;

    private volatile Map<Long, ProductUnit> productUnitMap;

    public void clearCustomerMap() {
        this.customerMap = null;
    }

    public void clearDeliverymanMap() {
        this.deliverymanMap = null;
    }

    public void clearIoTypeMap() {
        this.ioTypeMap = null;
    }

    public void clearProductCategoryMap() {
        this.productCategoryMap = null;
    }

    public void clearProductLocationMap() {
        this.productLocationMap = null;
    }

    public void clearProductUnitMap() {
        this.productUnitMap = null;
    }
}
