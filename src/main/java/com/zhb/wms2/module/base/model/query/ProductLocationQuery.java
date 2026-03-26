package com.zhb.wms2.module.base.model.query;

import com.zhb.wms2.common.model.BaseQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 货位编码查询对象
 *
 * @author zhb
 * @since 2026/3/26
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ProductLocationQuery extends BaseQuery {

    /**
     * 货位编码。
     */
    @Schema(description = "货位编码")
    private String code;
}
