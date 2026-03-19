package com.zhb.wms2.module.base.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhb.wms2.module.base.model.entity.IoType;
import com.zhb.wms2.module.base.model.query.IoTypeQuery;
import java.util.List;

public interface IoTypeService extends IService<IoType> {

    void saveChecked(IoType ioType);

    IPage<IoType> pageQuery(IoTypeQuery query);

    List<IoType> listAll();

    void updateByIdChecked(IoType ioType);

    void removeByIdChecked(Long id);
}
