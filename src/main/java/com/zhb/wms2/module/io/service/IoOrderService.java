package com.zhb.wms2.module.io.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhb.wms2.module.inventory.model.query.InventoryIoDetailQuery;
import com.zhb.wms2.module.inventory.model.vo.InventoryIoDetailVO;
import com.zhb.wms2.module.io.model.dto.IoOrderCreateDTO;
import com.zhb.wms2.module.io.model.dto.IoOrderGenerateDTO;
import com.zhb.wms2.module.io.model.dto.IoOrderUpdateDTO;
import com.zhb.wms2.module.io.model.entity.IoOrder;
import com.zhb.wms2.module.io.model.query.IoOrderQuery;

public interface IoOrderService extends IService<IoOrder> {

    IPage<? extends IoOrder> pageQuery(IoOrderQuery query);

    IPage<InventoryIoDetailVO> pageDetailByProductId(InventoryIoDetailQuery query);

    Long generateOrderByApply(Long applyId, IoOrderGenerateDTO dto);

    Long saveOrder(IoOrderCreateDTO dto);

    void updateOrder(IoOrderUpdateDTO dto);

    void removeByIdChecked(Long id);
}
