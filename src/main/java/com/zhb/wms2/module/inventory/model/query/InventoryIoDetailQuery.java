package com.zhb.wms2.module.inventory.model.query;

import com.zhb.wms2.common.model.BaseQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "商品出入库明细分页查询条件")
public class InventoryIoDetailQuery extends BaseQuery {

    @Schema(description = "商品ID")
    @NotNull(message = "商品ID不能为空")
    @Min(value = 1, message = "商品ID不能为空")
    private Long productId;
}
