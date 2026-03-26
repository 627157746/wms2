package com.zhb.wms2.module.base.model.query;

import com.zhb.wms2.common.model.BaseQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 单位名称查询对象
 *
 * @author zhb
 * @since 2026/3/26
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ProductUnitQuery extends BaseQuery {

    /**
     * 单位名称。
     */
    @Schema(description = "单位名称")
    private String name;
}
