package com.zhb.wms2.module.product.model.vo;

import com.zhb.wms2.module.base.model.entity.Customer;
import com.zhb.wms2.module.base.model.entity.Deliveryman;
import com.zhb.wms2.module.product.model.entity.Product;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;

/**
 * 商品出入库明细结果 VO
 *
 * @author zhb
 * @since 2026/3/26
 */
@Data
@Accessors(chain = true)
@Schema(description = "商品出入库明细结果")
public class StockIoDetailVO {

    /**
     * 出入库单 ID。
     */
    @Schema(description = "出入库单ID")
    private Long orderId;

    /**
     * 单号。
     */
    @Schema(description = "单号")
    private String orderNo;

    /**
     * 单据类型。
     */
    @Schema(description = "单据类型")
    private Integer orderType;

    /**
     * 单据类型名称。
     */
    @Schema(description = "单据类型名称")
    private String orderTypeName;

    /**
     * 业务日期。
     */
    @Schema(description = "业务日期")
    private LocalDate bizDate;

    /**
     * 数量。
     */
    @Schema(description = "数量")
    private Long qty;

    /**
     * 送货员信息。
     */
    @Schema(description = "送货员信息")
    private Deliveryman deliveryman;

    /**
     * 客户信息。
     */
    @Schema(description = "客户信息")
    private Customer customer;

    /**
     * 商品信息。
     */
    @Schema(description = "商品信息")
    private Product product;

    /**
     * 该笔出入库后的库存。
     */
    @Schema(description = "该笔出入库后的库存")
    private Long currentStockQty;
}
