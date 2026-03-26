package com.zhb.wms2.module.io.model.vo;

import com.zhb.wms2.module.io.model.entity.IoOrderDetail;
import com.zhb.wms2.module.product.model.vo.ProductPageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 出入库单明细结果 VO
 *
 * @author zhb
 * @since 2026/3/26
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@Schema(description = "出入库单明细结果")
public class IoOrderDetailVO extends IoOrderDetail {

    /**
     * 商品信息。
     */
    @Schema(description = "商品信息")
    private ProductPageVO product;

    /**
     * 货位编码。
     */
    @Schema(description = "货位编码")
    private String locationCode;
}
