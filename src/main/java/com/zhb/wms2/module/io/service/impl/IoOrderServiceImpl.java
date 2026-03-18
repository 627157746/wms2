package com.zhb.wms2.module.io.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhb.wms2.module.io.mapper.IoOrderMapper;
import com.zhb.wms2.module.io.model.entity.IoOrder;
import com.zhb.wms2.module.io.service.IoOrderService;
import org.springframework.stereotype.Service;

@Service
public class IoOrderServiceImpl extends ServiceImpl<IoOrderMapper, IoOrder> implements IoOrderService {
}
