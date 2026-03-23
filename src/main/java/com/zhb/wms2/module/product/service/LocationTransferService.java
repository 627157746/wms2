package com.zhb.wms2.module.product.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhb.wms2.module.product.model.dto.LocationTransferCreateDTO;
import com.zhb.wms2.module.product.model.entity.LocationTransfer;
import com.zhb.wms2.module.product.model.query.LocationTransferQuery;
import com.zhb.wms2.module.product.model.vo.LocationTransferPageVO;

public interface LocationTransferService extends IService<LocationTransfer> {

    IPage<LocationTransferPageVO> pageQuery(LocationTransferQuery query);

    Long createTransfer(LocationTransferCreateDTO dto);
}
