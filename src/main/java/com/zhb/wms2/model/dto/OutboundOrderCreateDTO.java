package com.zhb.wms2.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

/**
 * 创建出库单DTO
 */
@Schema(description = "创建出库单DTO")
@Data
public class OutboundOrderCreateDTO {

    /**
     * 出库类型ID
     */
    @Schema(description = "出库类型ID")
    @NotNull(message = "出库类型ID不能为空")
    private Integer outboundTypeId;

    /**
     * 出库日期
     */
    @Schema(description = "出库日期")
    @NotNull(message = "出库日期不能为空")
    private LocalDate outboundDate;

    /**
     * 操作员
     */
    @Schema(description = "操作员")
    @Size(max = 50, message = "操作员长度不能超过50个字符")
    private String operator;

    /**
     * 备注
     */
    @Schema(description = "备注")
    @Size(max = 500, message = "备注长度不能超过500个字符")
    private String remarks;

    /**
     * 出库单明细列表
     */
    @Schema(description = "出库单明细列表")
    @NotEmpty(message = "出库单明细不能为空")
    @Valid
    private List<OutboundOrderDetailItemDTO> details;
}

