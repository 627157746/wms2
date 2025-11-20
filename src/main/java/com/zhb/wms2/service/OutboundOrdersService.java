package com.zhb.wms2.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhb.wms2.model.OutboundOrders;
import com.zhb.wms2.model.dto.OutboundOrderCreateDTO;
import com.zhb.wms2.model.dto.OutboundOrderUpdateDTO;
import com.zhb.wms2.model.dto.OutboundOrdersQuery;
import com.zhb.wms2.model.vo.OutboundOrdersVO;

/**
 * @Author zhb
 * @Description
 * @Date 2025/11/19 16:19
 */
public interface OutboundOrdersService extends IService<OutboundOrders>{

    /**
     * 添加出库单信息
     */
    Long createOutboundOrder(OutboundOrderCreateDTO dto);

    /**
     * 修改出库单信息（含明细）
     */
    void updateOutbound(OutboundOrderUpdateDTO dto);

    /**
     * 分页查询出库单信息（含明细）
     */
    IPage<OutboundOrdersVO> queryPage(OutboundOrdersQuery query);

}
