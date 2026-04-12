package com.zhb.wms2.module.product.model.vo;

import com.zhb.wms2.module.product.model.entity.StockCheckTaskDetail;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 盘点任务明细结果。
 *
 * @author zhb
 * @since 2026/4/12
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@Schema(description = "盘点任务明细结果")
public class StockCheckTaskDetailVO extends StockCheckTaskDetail {

    @Schema(description = "盘点结果名称")
    private String resultTypeName;

    @Schema(description = "商品信息")
    private ProductPageVO product;
}
