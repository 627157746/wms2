package com.zhb.wms2.module.base.model.query;

import com.zhb.wms2.common.model.BaseQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 仓库查询对象
 *
 * @author zhb
 * @since 2026/3/27
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WarehouseQuery extends BaseQuery {

    /**
     * 仓库名称。
     */
    @Schema(description = "仓库名称")
    private String name;
}
