package com.zhb.wms2.module.base.model.dto;

import com.zhb.wms2.module.base.model.entity.Customer;
import com.zhb.wms2.module.base.model.entity.Deliveryman;
import com.zhb.wms2.module.base.model.entity.IoType;
import com.zhb.wms2.module.base.model.entity.ProductCategory;
import com.zhb.wms2.module.base.model.entity.ProductLocation;
import com.zhb.wms2.module.base.model.entity.ProductUnit;
import com.zhb.wms2.module.base.model.entity.Salesman;
import com.zhb.wms2.module.base.model.entity.Warehouse;
import java.util.Map;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * base 模块字典映射集合
 *
 * @author zhb
 * @since 2026/3/18
 */
@Data
@Accessors(chain = true)
public class BaseDictMapDTO {

    /**
     * 客户映射。
     */
    private Map<Long, Customer> customerMap;

    /**
     * 送货员映射。
     */
    private Map<Long, Deliveryman> deliverymanMap;

    /**
     * 业务员映射。
     */
    private Map<Long, Salesman> salesmanMap;

    /**
     * 出入库类型映射。
     */
    private Map<Long, IoType> ioTypeMap;

    /**
     * 商品分类映射。
     */
    private Map<Long, ProductCategory> productCategoryMap;

    /**
     * 商品货位映射。
     */
    private Map<Long, ProductLocation> productLocationMap;

    /**
     * 商品单位映射。
     */
    private Map<Long, ProductUnit> productUnitMap;

    /**
     * 仓库映射。
     */
    private Map<Long, Warehouse> warehouseMap;
}
