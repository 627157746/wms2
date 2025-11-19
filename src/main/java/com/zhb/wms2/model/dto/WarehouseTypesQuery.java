package com.zhb.wms2.model.dto;

import com.zhb.wms2.common.BaseQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author zhb
 * @Description 出入库类型查询条件
 * @Date 2025/11/19 17:30
 */
@Data
@EqualsAndHashCode(callSuper=true)
@Schema(description="出入库类型查询条件")
public class WarehouseTypesQuery extends BaseQuery {

    /**
     * 类型名称
     */
    @Schema(description="类型名称")
    private String typeName;

    /**
     * 类型分类：1-入库，2-出库
     */
    @Schema(description="类型分类：1-入库，2-出库")
    private Integer typeCategory;

}
