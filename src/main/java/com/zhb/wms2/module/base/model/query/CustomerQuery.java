package com.zhb.wms2.module.base.model.query;

import com.zhb.wms2.common.model.BaseQuery;
import io.swagger.v3.oas.annotations.media.Schema;
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
public class CustomerQuery extends BaseQuery {

    /**
     * 客户名称。
     */
    @Schema(description = "名称")
    private String name;

    /**
     * 手机号。
     */
    @Schema(description = "手机")
    private String phone;
}
