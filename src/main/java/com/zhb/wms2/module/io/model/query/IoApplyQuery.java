package com.zhb.wms2.module.io.model.query;

import com.zhb.wms2.common.model.BaseQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "出入库申请分页查询条件")
public class IoApplyQuery extends BaseQuery {

    @Schema(description = "申请单号")
    private String applyNo;

    @Schema(description = "单据类型：1-入库 2-出库")
    private Integer orderType;

    @Schema(description = "申请日期开始")
    private LocalDate applyDateStart;

    @Schema(description = "申请日期结束")
    private LocalDate applyDateEnd;

    @Schema(description = "送货员ID")
    private Long deliverymanId;

    @Schema(description = "客户ID")
    private Long customerId;

    @Schema(description = "出入库类型ID")
    private Long ioTypeId;

    @Schema(description = "审批状态：0-未审批 1-已审批")
    private Integer approveStatus;

    @Schema(description = "出入库状态：0-未执行 1-已执行")
    private Integer ioStatus;
}
