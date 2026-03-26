package com.zhb.wms2.module.product.model.query;

import com.zhb.wms2.common.model.BaseQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 转货位分页查询条件查询对象
 *
 * @author zhb
 * @since 2026/3/26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "转货位分页查询条件")
public class LocationTransferQuery extends BaseQuery {

    /**
     * 商品 ID。
     */
    @Schema(description = "商品ID")
    private Long productId;

    /**
     * 原货位 ID。
     */
    @Schema(description = "原货位ID")
    private Long fromLocationId;

    /**
     * 转移货位 ID。
     */
    @Schema(description = "转移货位ID")
    private Long toLocationId;
}
