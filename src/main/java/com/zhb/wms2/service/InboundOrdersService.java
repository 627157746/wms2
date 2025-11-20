package com.zhb.wms2.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhb.wms2.model.InboundOrders;
import com.zhb.wms2.model.dto.InboundOrderCreateDTO;
import com.zhb.wms2.model.dto.InboundOrderUpdateDTO;
import com.zhb.wms2.model.dto.InboundOrdersQuery;
import com.zhb.wms2.model.vo.InboundOrdersVO;

/**
 * @Author zhb
 * @Description
 * @Date 2025/11/19 16:18
 */
public interface InboundOrdersService extends IService<InboundOrders>{

    /**
     * 添加入库单信息
     */
    Long createInboundOrder(InboundOrderCreateDTO dto);

    /**
     * 修改入库单信息（含明细）
     */
    void updateInbound(InboundOrderUpdateDTO dto);

    /**
     * 分页查询入库单信息（含明细）
     */
    IPage<InboundOrdersVO> queryPage(InboundOrdersQuery query);

}
