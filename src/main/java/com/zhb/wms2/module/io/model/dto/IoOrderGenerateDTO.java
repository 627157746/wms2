package com.zhb.wms2.module.io.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * 根据申请生成出入库单DTO DTO
 *
 * @author zhb
 * @since 2026/3/26
 */
@Schema(description = "根据申请生成出入库单DTO")
@Data
public class IoOrderGenerateDTO {

    /**
     * 业务日期。
     */
    @Schema(description = "业务日期")
    @NotNull(message = "业务日期不能为空")
    private LocalDate bizDate;

    /**
     * 备注。
     */
    @Schema(description = "备注")
    private String remark;

    /**
     * 出入库明细列表。
     */
    @Schema(description = "出入库明细")
    @Valid
    @NotEmpty(message = "出入库明细不能为空")
    private List<IoOrderDetailDTO> detailList;
}
