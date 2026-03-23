package com.zhb.wms2.module.product.model.query;

import com.zhb.wms2.common.model.BaseQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "转货位分页查询条件")
public class LocationTransferQuery extends BaseQuery {

    @Schema(description = "商品ID")
    private Long productId;

    @Schema(description = "原货位ID")
    private Long fromLocationId;

    @Schema(description = "转移货位ID")
    private Long toLocationId;
}
