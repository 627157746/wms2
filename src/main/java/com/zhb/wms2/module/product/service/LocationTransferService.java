package com.zhb.wms2.module.product.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhb.wms2.module.product.model.dto.LocationTransferCreateDTO;
import com.zhb.wms2.module.product.model.entity.LocationTransfer;
import com.zhb.wms2.module.product.model.query.LocationTransferQuery;
import com.zhb.wms2.module.product.model.vo.LocationTransferPageVO;

/**
 * LocationTransferService 服务接口
 *
 * @author zhb
 * @since 2026/3/26
 */
public interface LocationTransferService extends IService<LocationTransfer> {

    /**
     * 分页查询转货位记录。
     */
    IPage<LocationTransferPageVO> pageQuery(LocationTransferQuery query);

    /**
     * 发起转货位。
     */
    Long createTransfer(LocationTransferCreateDTO dto);
}
