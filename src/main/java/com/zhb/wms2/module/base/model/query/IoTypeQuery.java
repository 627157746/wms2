package com.zhb.wms2.module.base.model.query;

import com.zhb.wms2.common.model.BaseQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class IoTypeQuery extends BaseQuery {

    @Schema(description = "类型名称")
    private String name;

    @Schema(description = "业务类型：1-入库 2-出库")
    @Min(value = 1, message = "业务类型不正确")
    @Max(value = 2, message = "业务类型不正确")
    private Integer bizType;
}
