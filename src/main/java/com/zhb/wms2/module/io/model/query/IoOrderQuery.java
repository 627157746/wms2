package com.zhb.wms2.module.io.model.query;

import com.zhb.wms2.common.model.BaseQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "出入库单分页查询条件")
public class IoOrderQuery extends BaseQuery {

    @Schema(description = "单号")
    private String orderNo;

    @Schema(description = "单据类型：1-入库 2-出库")
    private Integer orderType;

    @Schema(description = "业务日期开始")
    private LocalDate bizDateStart;

    @Schema(description = "业务日期结束")
    private LocalDate bizDateEnd;

    @Schema(description = "送货员ID")
    private Long deliverymanId;

    @Schema(description = "客户ID")
    private Long customerId;

    @Schema(description = "业务员ID")
    private Long salesmanId;

    @Schema(description = "出入库类型ID")
    private Long ioTypeId;

    @Schema(description = "来源申请ID")
    private Long applyId;

    @Schema(description = "拣货状态：0-未拣 1-已拣")
    private Integer pickingStatus;
}
