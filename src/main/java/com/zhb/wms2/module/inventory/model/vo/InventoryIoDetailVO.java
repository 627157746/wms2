package com.zhb.wms2.module.inventory.model.vo;

import com.zhb.wms2.module.base.model.entity.Customer;
import com.zhb.wms2.module.base.model.entity.Deliveryman;
import com.zhb.wms2.module.product.model.entity.Product;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "商品出入库明细结果")
public class InventoryIoDetailVO {

    @Schema(description = "出入库单ID")
    private Long orderId;

    @Schema(description = "单号")
    private String orderNo;

    @Schema(description = "单据类型")
    private Integer orderType;

    @Schema(description = "单据类型名称")
    private String orderTypeName;

    @Schema(description = "业务日期")
    private LocalDate bizDate;

    @Schema(description = "数量")
    private Long qty;

    @Schema(description = "送货员信息")
    private Deliveryman deliveryman;

    @Schema(description = "客户信息")
    private Customer customer;

    @Schema(description = "商品信息")
    private Product product;

    @Schema(description = "该笔出入库后的库存")
    private Long currentStockQty;
}
