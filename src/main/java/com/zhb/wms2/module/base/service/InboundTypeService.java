package com.zhb.wms2.module.base.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhb.wms2.module.base.model.entity.InboundType;
import com.zhb.wms2.module.base.model.query.InboundTypeQuery;
import java.util.List;
    /**
 * @Author zhb
 * @Description 
 * @Date 2026/3/17 18:58
 */
public interface InboundTypeService extends IService<InboundType> {

    IPage<InboundType> pageQuery(InboundTypeQuery query);

    List<InboundType> listAll();

    void removeByIdChecked(Long id);
}
