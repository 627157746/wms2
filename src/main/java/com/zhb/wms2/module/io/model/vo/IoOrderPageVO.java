package com.zhb.wms2.module.io.model.vo;

import com.zhb.wms2.module.base.model.entity.Customer;
import com.zhb.wms2.module.base.model.entity.Deliveryman;
import com.zhb.wms2.module.base.model.entity.Salesman;
import com.zhb.wms2.module.base.model.entity.Warehouse;
import com.zhb.wms2.module.io.model.entity.IoOrder;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 出入库单分页结果 VO
 *
 * @author zhb
 * @since 2026/3/26
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@Schema(description = "出入库单分页结果")
public class IoOrderPageVO extends IoOrder {

    /**
     * 单据类型名称。
     */
    @Schema(description = "单据类型名称")
    private String orderTypeName;

    /**
     * 来源申请单号。
     */
    @Schema(description = "来源申请单号")
    private String applyNo;

    /**
     * 送货员信息。
     */
    @Schema(description = "送货员信息")
    private Deliveryman deliveryman;

    /**
     * 客户信息。
     */
    @Schema(description = "客户信息")
    private Customer customer;

    /**
     * 仓库信息。
     */
    @Schema(description = "仓库信息")
    private Warehouse warehouse;

    /**
     * 业务员信息。
     */
    @Schema(description = "业务员信息")
    private Salesman salesman;

    /**
     * 出入库类型名称。
     */
    @Schema(description = "出入库类型名称")
    private String ioTypeName;

    /**
     * 拣货状态名称。
     */
    @Schema(description = "拣货状态名称")
    private String pickingStatusName;

    /**
     * 出入库单明细列表。
     */
    @Schema(description = "出入库单明细列表")
    private List<IoOrderDetailVO> detailList;
}
