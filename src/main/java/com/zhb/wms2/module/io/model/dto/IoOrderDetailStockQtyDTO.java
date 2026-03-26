package com.zhb.wms2.module.io.model.dto;

import lombok.Data;

/**
 * IoOrderDetailStockQtyDTO DTO
 *
 * @author zhb
 * @since 2026/3/26
 */
@Data
public class IoOrderDetailStockQtyDTO {

    /**
     * 明细 ID。
     */
    private Long detailId;

    /**
     * 当前库存数量。
     */
    private Long currentStockQty;
}
