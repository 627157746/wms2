package com.zhb.wms2.module.io.model.query;

import com.zhb.wms2.common.model.BaseQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 出入库单分页查询条件查询对象
 *
 * @author zhb
 * @since 2026/3/26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "出入库单分页查询条件")
public class IoOrderQuery extends BaseQuery {

    /**
     * 单号。
     */
    @Schema(description = "单号")
    private String orderNo;

    /**
     * 单据类型。
     */
    @Schema(description = "单据类型：1-入库 2-出库")
    @Min(value = 1, message = "单据类型不正确")
    @Max(value = 2, message = "单据类型不正确")
    private Integer orderType;

    /**
     * 业务日期开始。
     */
    @Schema(description = "业务日期开始")
    private LocalDate bizDateStart;

    /**
     * 业务日期结束。
     */
    @Schema(description = "业务日期结束")
    private LocalDate bizDateEnd;

    /**
     * 送货员 ID。
     */
    @Schema(description = "送货员ID")
    private Long deliverymanId;

    /**
     * 客户 ID。
     */
    @Schema(description = "客户ID")
    private Long customerId;

    /**
     * 仓库 ID。
     */
    @Schema(description = "仓库ID")
    private Long warehouseId;

    /**
     * 业务员 ID。
     */
    @Schema(description = "业务员ID")
    private Long salesmanId;

    /**
     * 出入库类型 ID。
     */
    @Schema(description = "出入库类型ID")
    private Long ioTypeId;

    /**
     * 来源申请 ID。
     */
    @Schema(description = "来源申请ID")
    private Long applyId;

    /**
     * 拣货状态。
     */
    @Schema(description = "拣货状态：0-未拣 1-已拣")
    @Min(value = 0, message = "拣货状态不正确")
    @Max(value = 1, message = "拣货状态不正确")
    private Integer pickingStatus;
}
