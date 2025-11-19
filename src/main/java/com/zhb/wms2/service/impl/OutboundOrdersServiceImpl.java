package com.zhb.wms2.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhb.wms2.mapper.OutboundOrdersMapper;
import com.zhb.wms2.model.OutboundOrders;
import com.zhb.wms2.service.OutboundOrdersService;
/**
 * @Author zhb
 * @Description 
 * @Date 2025/11/19 16:19
 */
@Service
public class OutboundOrdersServiceImpl extends ServiceImpl<OutboundOrdersMapper, OutboundOrders> implements OutboundOrdersService{

}
