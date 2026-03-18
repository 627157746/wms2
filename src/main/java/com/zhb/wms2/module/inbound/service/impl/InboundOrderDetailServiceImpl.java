package com.zhb.wms2.module.inbound.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhb.wms2.module.inbound.model.entity.InboundOrderDetail;
import com.zhb.wms2.module.inbound.mapper.InboundOrderDetailMapper;
import com.zhb.wms2.module.inbound.service.InboundOrderDetailService;
/**
 * @Author zhb
 * @Description 
 * @Date 2026/3/17 19:03
 */
@Service
public class InboundOrderDetailServiceImpl extends ServiceImpl<InboundOrderDetailMapper, InboundOrderDetail> implements InboundOrderDetailService{

}
