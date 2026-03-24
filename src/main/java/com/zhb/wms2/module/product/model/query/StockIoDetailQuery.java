package com.zhb.wms2.module.product.model.query;

import com.zhb.wms2.common.model.BaseQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "商品出入库明细分页查询条件")
public class StockIoDetailQuery extends BaseQuery {

    @Schema(description = "商品ID")
    @Min(value = 1, message = "商品ID必须大于0")
    private Long productId;

    @Schema(description = "业务日期开始")
    private LocalDate bizDateStart;

    @Schema(description = "业务日期结束")
    private LocalDate bizDateEnd;

    @Schema(description = "单据类型：1-入库 2-出库")
    private Integer orderType;
}
