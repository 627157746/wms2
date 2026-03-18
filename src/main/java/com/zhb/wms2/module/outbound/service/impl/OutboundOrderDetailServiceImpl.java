package com.zhb.wms2.module.outbound.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhb.wms2.module.outbound.mapper.OutboundOrderDetailMapper;
import com.zhb.wms2.module.outbound.model.entity.OutboundOrderDetail;
import com.zhb.wms2.module.outbound.service.OutboundOrderDetailService;
/**
 * @Author zhb
 * @Description 
 * @Date 2026/3/17 19:04
 */
@Service
public class OutboundOrderDetailServiceImpl extends ServiceImpl<OutboundOrderDetailMapper, OutboundOrderDetail> implements OutboundOrderDetailService{

}
