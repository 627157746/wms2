package com.zhb.wms2.module.io.model.vo;

import com.zhb.wms2.module.base.model.entity.Customer;
import com.zhb.wms2.module.base.model.entity.Deliveryman;
import com.zhb.wms2.module.io.model.entity.IoOrder;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "出入库单分页结果")
public class IoOrderPageVO extends IoOrder {

    @Schema(description = "单据类型名称")
    private String orderTypeName;

    @Schema(description = "来源申请单号")
    private String applyNo;

    @Schema(description = "送货员信息")
    private Deliveryman deliveryman;

    @Schema(description = "客户信息")
    private Customer customer;

    @Schema(description = "出入库类型名称")
    private String ioTypeName;

    @Schema(description = "拣货状态名称")
    private String pickingStatusName;

    @Schema(description = "出入库单明细列表")
    private List<IoOrderDetailVO> detailList;
}
