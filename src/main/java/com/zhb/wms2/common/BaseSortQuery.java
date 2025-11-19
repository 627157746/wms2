package com.zhb.wms2.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author zhb
 * @Description
 * @Date 2025/8/5 15:12
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class BaseSortQuery extends BaseQuery {

    /**
     * 排序方式
     */
    @Schema(description = "排序方式", example = "asc desc")
    private String sort;

    /**
     * 排序字段
     */
    @Schema(description = "排序字段", example = "createTime")
    private String sortField;
}
