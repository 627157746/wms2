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

    /**
     * 客户字典缓存。
     */
    private volatile Map<Long, Customer> customerMap;

    /**
     * 送货员字典缓存。
     */
    private volatile Map<Long, Deliveryman> deliverymanMap;

    /**
     * 业务员字典缓存。
     */
    private volatile Map<Long, Salesman> salesmanMap;

    /**
     * 出入库类型字典缓存。
     */
    private volatile Map<Long, IoType> ioTypeMap;

    /**
     * 商品分类字典缓存。
     */
    private volatile Map<Long, ProductCategory> productCategoryMap;

    /**
     * 商品货位字典缓存。
     */
    private volatile Map<Long, ProductLocation> productLocationMap;

    /**
     * 商品单位字典缓存。
     */
    private volatile Map<Long, ProductUnit> productUnitMap;

    /**
     * 清理客户字典缓存。
     */
    public void clearCustomerMap() {
        this.customerMap = null;
    }

    /**
     * 清理送货员字典缓存。
     */
    public void clearDeliverymanMap() {
        this.deliverymanMap = null;
    }

    /**
     * 清理业务员字典缓存。
     */
    public void clearSalesmanMap() {
        this.salesmanMap = null;
    }

    /**
     * 清理出入库类型字典缓存。
     */
    public void clearIoTypeMap() {
        this.ioTypeMap = null;
    }

    /**
     * 清理商品分类字典缓存。
     */
    public void clearProductCategoryMap() {
        this.productCategoryMap = null;
    }

    /**
     * 清理商品货位字典缓存。
     */
    public void clearProductLocationMap() {
        this.productLocationMap = null;
    }

    /**
     * 清理商品单位字典缓存。
     */
    public void clearProductUnitMap() {
        this.productUnitMap = null;
    }
}
