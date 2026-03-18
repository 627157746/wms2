package com.zhb.wms2.module.io.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhb.wms2.module.io.mapper.IoOrderDetailMapper;
import com.zhb.wms2.module.io.model.entity.IoOrderDetail;
import com.zhb.wms2.module.io.service.IoOrderDetailService;
import org.springframework.stereotype.Service;

@Service
public class IoOrderDetailServiceImpl extends ServiceImpl<IoOrderDetailMapper, IoOrderDetail> implements IoOrderDetailService {
}
