package com.zhb.wms2.module.io.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhb.wms2.module.io.mapper.IoApplyDetailMapper;
import com.zhb.wms2.module.io.model.entity.IoApplyDetail;
import com.zhb.wms2.module.io.service.IoApplyDetailService;
import org.springframework.stereotype.Service;

@Service
public class IoApplyDetailServiceImpl extends ServiceImpl<IoApplyDetailMapper, IoApplyDetail>
        implements IoApplyDetailService {
}
