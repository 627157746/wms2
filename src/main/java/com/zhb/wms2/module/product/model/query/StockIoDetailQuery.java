package com.zhb.wms2.module.product.model.query;

import com.zhb.wms2.common.model.BaseQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 商品出入库明细分页查询条件查询对象
 *
 * @author zhb
 * @since 2026/3/26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "商品出入库明细分页查询条件")
public class StockIoDetailQuery extends BaseQuery {

    /**
     * 商品 ID。
     */
    @Schema(description = "商品ID")
    @Min(value = 1, message = "商品ID必须大于0")
    private Long productId;

    /**
     * 业务日期开始。
     */
    @Schema(description = "业务日期开始")
    private LocalDate bizDateStart;

    /**
     * 业务日期结束。
     */
    @Schema(description = "业务日期结束")
    private LocalDate bizDateEnd;

    /**
     * 单据类型。
     */
    @Schema(description = "单据类型：1-入库 2-出库")
    private Integer orderType;
}
