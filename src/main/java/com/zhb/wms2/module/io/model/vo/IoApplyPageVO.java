package com.zhb.wms2.module.io.model.vo;

import com.zhb.wms2.module.base.model.entity.Customer;
import com.zhb.wms2.module.base.model.entity.Deliveryman;
import com.zhb.wms2.module.io.model.entity.IoApply;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "出入库申请分页结果")
public class IoApplyPageVO extends IoApply {

    @Schema(description = "单据类型名称")
    private String orderTypeName;

    @Schema(description = "送货员信息")
    private Deliveryman deliveryman;

    @Schema(description = "客户信息")
    private Customer customer;

    @Schema(description = "出入库类型名称")
    private String ioTypeName;

    @Schema(description = "审批状态名称")
    private String approveStatusName;

    @Schema(description = "出入库状态名称")
    private String ioStatusName;
}
