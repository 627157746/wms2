package com.zhb.wms2.module.inventory.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhb.wms2.module.inventory.model.dto.LocationTransferCreateDTO;
import com.zhb.wms2.module.inventory.model.entity.LocationTransfer;
import com.zhb.wms2.module.inventory.model.query.LocationTransferQuery;
import com.zhb.wms2.module.inventory.model.vo.LocationTransferPageVO;

public interface LocationTransferService extends IService<LocationTransfer> {

    IPage<LocationTransferPageVO> pageQuery(LocationTransferQuery query);

    Long createTransfer(LocationTransferCreateDTO dto);
}
