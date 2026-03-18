package com.zhb.wms2.module.base.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhb.wms2.module.base.model.entity.OutboundType;
import com.zhb.wms2.module.base.model.query.OutboundTypeQuery;
import java.util.List;
    /**
 * @Author zhb
 * @Description 
 * @Date 2026/3/17 18:58
 */
public interface OutboundTypeService extends IService<OutboundType> {

    IPage<OutboundType> pageQuery(OutboundTypeQuery query);

    List<OutboundType> listAll();

    void removeByIdChecked(Long id);
}
