package com.zhb.wms2.module.io.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhb.wms2.module.io.mapper.IoApplyMapper;
import com.zhb.wms2.module.io.model.entity.IoApply;
import com.zhb.wms2.module.io.service.IoApplyService;
import org.springframework.stereotype.Service;

@Service
public class IoApplyServiceImpl extends ServiceImpl<IoApplyMapper, IoApply> implements IoApplyService {
}
