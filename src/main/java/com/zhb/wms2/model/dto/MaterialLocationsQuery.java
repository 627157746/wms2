package com.zhb.wms2.model.dto;

import com.zhb.wms2.common.BaseQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 物料位置查询条件
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "物料位置查询条件")
public class MaterialLocationsQuery extends BaseQuery {

    @Schema(description = "物料位编码")
    private String locationCode;

    @Schema(description = "排号")
    private String rowNo;

    @Schema(description = "段号")
    private String sectionNo;
}