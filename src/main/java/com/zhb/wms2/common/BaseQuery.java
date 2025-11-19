package com.zhb.wms2.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Author zhb
 * @Description
 * @Date 2025/8/5 15:10
 */
@Data
public class BaseQuery {

    /**
     * 当前页码
     */
    @Schema(description = "当前页码", example = "1")
    private Long current = 1L;

    /**
     * 每页大小
     */
    @Schema(description = "每页大小", example = "10")
    private Long size = 10L;

}
