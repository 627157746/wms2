package com.zhb.wms2.module.product.model.vo;

import com.zhb.wms2.module.product.model.entity.ProductStockDetail;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "商品库存明细结果")
public class ProductStockDetailVO extends ProductStockDetail {

    @Schema(description = "货位编码")
    private String locationCode;
}
