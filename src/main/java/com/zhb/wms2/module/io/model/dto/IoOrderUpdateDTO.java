package com.zhb.wms2.module.io.model.dto;

import com.zhb.wms2.common.validated.Update;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * 出入库单修改DTO DTO
 *
 * @author zhb
 * @since 2026/3/26
 */
@Schema(description = "出入库单修改DTO")
@Data
public class IoOrderUpdateDTO {

    /**
     * 主键 ID。
     */
    @Schema(description = "主键ID")
    @NotNull(groups = Update.class, message = "ID不能为空")
    @Min(value = 1, groups = Update.class, message = "ID不能为空")
    private Long id;

    /**
     * 业务日期。
     */
    @Schema(description = "业务日期")
    @NotNull(message = "业务日期不能为空")
    private LocalDate bizDate;

    /**
     * 送货员 ID。
     */
    @Schema(description = "送货员ID")
    @NotNull(message = "送货员不能为空")
    @Min(value = 1, message = "送货员不能为空")
    private Long deliverymanId;

    /**
     * 客户 ID，仅出库使用。
     */
    @Schema(description = "客户ID，仅出库使用")
    @Min(value = 1, message = "客户不能为空")
    private Long customerId;

    /**
     * 业务员 ID。
     */
    @Schema(description = "业务员ID")
    @Min(value = 1, message = "业务员不能为空")
    private Long salesmanId;

    /**
     * 出入库类型 ID。
     */
    @Schema(description = "出入库类型ID")
    @NotNull(message = "出入库类型不能为空")
    @Min(value = 1, message = "出入库类型不能为空")
    private Long ioTypeId;

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
