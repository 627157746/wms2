package com.zhb.wms2.module.product.model.query;

import com.zhb.wms2.common.model.BaseQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "商品分页查询条件")
public class ProductQuery extends BaseQuery {

    @Schema(description = "商品名称")
    private String name;

    @Schema(description = "商品编号")
    private String code;

    @Schema(description = "商品分类ID")
    private Long categoryId;

    @Schema(description = "商品单位ID")
    private Long unitId;

    @Schema(description = "是否包含0库存，true包含，false不包含")
    private Boolean includeZeroStock;
}
