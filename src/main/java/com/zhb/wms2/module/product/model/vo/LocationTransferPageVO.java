package com.zhb.wms2.module.product.model.vo;

import com.zhb.wms2.module.product.model.entity.LocationTransfer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 转货位分页结果 VO
 *
 * @author zhb
 * @since 2026/3/26
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@Schema(description = "转货位分页结果")
public class LocationTransferPageVO extends LocationTransfer {

    /**
     * 商品名称。
     */
    @Schema(description = "商品名称")
    private String productName;

    /**
     * 商品编码。
     */
    @Schema(description = "商品编码")
    private String productCode;

    /**
     * 原货位编码。
     */
    @Schema(description = "原货位编码")
    private String fromLocationCode;

    /**
     * 转移货位编码。
     */
    @Schema(description = "转移货位编码")
    private String toLocationCode;
}
