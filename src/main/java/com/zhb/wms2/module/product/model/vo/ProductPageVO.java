package com.zhb.wms2.module.product.model.vo;

import com.zhb.wms2.module.product.model.entity.Product;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 商品分页结果 VO
 *
 * @author zhb
 * @since 2026/3/26
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@Schema(description = "商品分页结果")
public class ProductPageVO extends Product {

    /**
     * 商品单位名称。
     */
    @Schema(description = "商品单位名称")
    private String productUnitName;

    /**
     * 商品分类名称。
     */
    @Schema(description = "商品分类名称")
    private String productCategoryName;

    /**
     * 库存货位编码列表。
     */
    @Schema(description = "库存货位编码列表")
    private List<String> locationCodes;
}
