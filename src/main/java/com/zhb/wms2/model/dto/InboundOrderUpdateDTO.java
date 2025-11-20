package com.zhb.wms2.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * 入库单更新DTO
 */
@Schema(description = "入库单更新DTO")
@Data
public class InboundOrderUpdateDTO {

    /**
     * 入库单ID（用于更新）
     */
    @Schema(description = "入库单ID")
    private Long id;

    /**
     * 入库类型ID
     */
    @Schema(description = "入库类型ID")
    @NotNull(message = "入库类型ID不能为空")
    @Min(value = 1, message = "入库类型ID必须大于0")
    private Integer inboundTypeId;

    /**
     * 入库日期
     */
    @Schema(description = "入库日期")
    @NotNull(message = "入库日期不能为空")
    private LocalDate inboundDate;

    /**
     * 操作员
     */
    @Schema(description = "操作员")
    @NotBlank(message = "操作员不能为空")
    @Size(max = 50, message = "操作员长度不能超过50个字符")
    private String operator;

    /**
     * 备注
     */
    @Schema(description = "备注")
    @Size(max = 500, message = "备注长度不能超过500个字符")
    private String remarks;

    /**
     * 入库明细列表（更新时会先删除原明细，再插入新明细）
     */
    @Schema(description = "入库明细列表")
    @Valid
    @NotEmpty(message = "入库明细列表不能为空")
    private List<InboundOrderDetailItemDTO> details;
}
