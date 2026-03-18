package com.zhb.wms2.module.base.model.query;

import com.zhb.wms2.common.model.BaseQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CustomerQuery extends BaseQuery {

    @Schema(description = "名称")
    private String name;

    @Schema(description = "手机")
    private String phone;

    @Schema(description = "适用范围：0-不限 1-出库 2-入库")
    private Integer scope;
}
