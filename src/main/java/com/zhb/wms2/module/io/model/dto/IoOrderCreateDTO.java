package com.zhb.wms2.module.io.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "手工新增出入库单DTO")
@Data
public class IoOrderCreateDTO {

    @Schema(description = "单据类型：1-入库 2-出库")
    @NotNull(message = "单据类型不能为空")
    @Min(value = 1, message = "单据类型不正确")
    @Max(value = 2, message = "单据类型不正确")
    private Integer orderType;

    @Schema(description = "业务日期")
    @NotNull(message = "业务日期不能为空")
    private LocalDate bizDate;

    @Schema(description = "送货员ID")
    @NotNull(message = "送货员不能为空")
    @Min(value = 1, message = "送货员不能为空")
    private Long deliverymanId;

    @Schema(description = "客户ID，仅出库使用")
    @Min(value = 1, message = "客户不能为空")
    private Long customerId;

    @Schema(description = "业务员ID，仅出库使用")
    @Min(value = 1, message = "业务员不能为空")
    private Long salesmanId;

    @Schema(description = "出入库类型ID")
    @NotNull(message = "出入库类型不能为空")
    @Min(value = 1, message = "出入库类型不能为空")
    private Long ioTypeId;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "出入库明细")
    @Valid
    @NotEmpty(message = "出入库明细不能为空")
    private List<IoOrderDetailDTO> detailList;
}
