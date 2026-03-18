package com.zhb.wms2.module.base.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhb.wms2.module.base.model.entity.Deliveryman;
import com.zhb.wms2.module.base.model.query.DeliverymanQuery;
import java.util.List;

/**
 * @Author zhb
 * @Description
 * @Date 2026/3/17 18:57
 */
public interface DeliverymanService extends IService<Deliveryman> {

    IPage<Deliveryman> pageQuery(DeliverymanQuery query);

    List<Deliveryman> listAll();

    void removeByIdChecked(Long id);
}
