package com.zhb.wms2.common.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * BaseSortQuery 模型
 *
 * @author zhb
 * @since 2026/3/26
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
