package com.zhb.wms2.module.base.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhb.wms2.module.base.model.entity.Salesman;
import com.zhb.wms2.module.base.model.query.SalesmanQuery;
import java.util.List;

public interface SalesmanService extends IService<Salesman> {

    void saveChecked(Salesman salesman);

    IPage<Salesman> pageQuery(SalesmanQuery query);

    List<Salesman> listAll();

    void updateByIdChecked(Salesman salesman);

    void removeByIdChecked(Long id);
}
