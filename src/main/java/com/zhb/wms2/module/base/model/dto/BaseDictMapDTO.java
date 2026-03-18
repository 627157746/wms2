package com.zhb.wms2.module.base.model.dto;

import com.zhb.wms2.module.base.model.entity.Customer;
import com.zhb.wms2.module.base.model.entity.Deliveryman;
import com.zhb.wms2.module.base.model.entity.IoType;
import com.zhb.wms2.module.base.model.entity.ProductCategory;
import com.zhb.wms2.module.base.model.entity.ProductLocation;
import com.zhb.wms2.module.base.model.entity.ProductUnit;
import java.util.Map;
import lombok.Data;

/**
 * base 模块字典映射集合
 *
 * @author zhb
 * @since 2026/3/18
 */
@Data
public class BaseDictMapDTO {

    private Map<Long, Customer> customerMap;

    private Map<Long, Deliveryman> deliverymanMap;

    private Map<Long, IoType> ioTypeMap;

    private Map<Long, ProductCategory> productCategoryMap;

    private Map<Long, ProductLocation> productLocationMap;

    private Map<Long, ProductUnit> productUnitMap;
}
