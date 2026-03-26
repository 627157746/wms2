package com.zhb.wms2.module.base.model.query;

import com.zhb.wms2.common.model.BaseQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 名称查询对象
 *
 * @author zhb
 * @since 2026/3/26
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DeliverymanQuery extends BaseQuery {

    /**
     * 送货员名称。
     */
    @Schema(description = "名称")
    private String name;

    /**
     * 手机号。
     */
    @Schema(description = "手机")
    private String phone;

    /**
     * 适用范围。
     */
    @Schema(description = "适用范围：0-不限 1-入库 2-出库")
    @Min(value = 0, message = "适用范围不正确")
    @Max(value = 2, message = "适用范围不正确")
    private Integer scope;
}
