package com.zhb.wms2.module.product.model.query;

import com.zhb.wms2.common.model.BaseQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商品分页查询条件查询对象
 *
 * @author zhb
 * @since 2026/3/26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "商品分页查询条件")
public class ProductQuery extends BaseQuery {

    /**
     * 商品名称。
     */
    @Schema(description = "商品名称")
    private String name;

    /**
     * 商品编号。
     */
    @Schema(description = "商品编号")
    private String code;

    /**
     * 条形码。
     */
    @Schema(description = "条形码")
    private String barcode;

    /**
     * 型号。
     */
    @Schema(description = "型号")
    private String model;

    /**
     * 商品分类 ID。
     */
    @Schema(description = "商品分类ID")
    private Long categoryId;

    /**
     * 商品单位 ID。
     */
    @Schema(description = "商品单位ID")
    private Long unitId;

    /**
     * 是否包含零库存商品。
     */
    @Schema(description = "是否包含0库存，true包含，false不包含")
    private Boolean includeZeroStock;

    /**
     * 是否只查询库存短缺商品。
     */
    @Schema(description = "是否只看库存短缺商品，true只看，false查询全部")
    private Boolean onlyShortageStock;
}
