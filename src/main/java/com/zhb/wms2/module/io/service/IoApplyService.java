package com.zhb.wms2.module.io.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhb.wms2.module.io.model.dto.IoApplyCreateDTO;
import com.zhb.wms2.module.io.model.dto.IoApplyUpdateDTO;
import com.zhb.wms2.module.io.model.entity.IoApply;
import com.zhb.wms2.module.io.model.query.IoApplyQuery;

public interface IoApplyService extends IService<IoApply> {

    IPage<? extends IoApply> pageQuery(IoApplyQuery query);

    Long saveApply(IoApplyCreateDTO dto);

    void updateApply(IoApplyUpdateDTO dto);

    void approveById(Long id);

    void cancelApproveById(Long id);

    void removeByIdChecked(Long id);
}
