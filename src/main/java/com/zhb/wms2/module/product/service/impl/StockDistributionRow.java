package com.zhb.wms2.module.product.service.impl;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 库存分布查询的中间结果行
 *
 * @author zhb
 * @since 2026/3/26
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PACKAGE)
final class StockDistributionRow {

    /**
     * 商品 ID。
     */
    private final Long productId;

    /**
     * 商品编码。
     */
    private final String productCode;

    /**
     * 商品名称。
     */
    private final String productName;

    /**
     * 商品型号。
     */
    private final String model;

    /**
     * 商品单位 ID。
     */
    private final Long unitId;

    /**
     * 商品单位名称。
     */
    private final String unitName;

    /**
     * 货位 ID。
     */
    private final Long locationId;

    /**
     * 货位编码。
     */
    private final String locationCode;

    /**
     * 货位排序值。
     */
    private final Integer locationSortOrder;

    /**
     * 当前货位下的库存数量。
     */
    private final Long qty;
}
