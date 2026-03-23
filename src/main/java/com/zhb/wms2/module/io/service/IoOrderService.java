package com.zhb.wms2.module.io.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhb.wms2.module.product.model.query.StockIoDetailQuery;
import com.zhb.wms2.module.product.model.vo.StockIoDetailVO;
import com.zhb.wms2.module.io.model.dto.IoOrderCreateDTO;
import com.zhb.wms2.module.io.model.dto.IoOrderGenerateDTO;
import com.zhb.wms2.module.io.model.dto.IoOrderUpdateDTO;
import com.zhb.wms2.module.io.model.entity.IoOrder;
import com.zhb.wms2.module.io.model.query.IoOrderQuery;
import com.zhb.wms2.module.io.model.vo.IoOrderPageVO;

public interface IoOrderService extends IService<IoOrder> {

    IPage<IoOrderPageVO> pageQuery(IoOrderQuery query);

    IoOrderPageVO getDetailById(Long id);

    IPage<StockIoDetailVO> pageDetailByProductId(StockIoDetailQuery query);

    Long generateOrderByApply(Long applyId, IoOrderGenerateDTO dto);

    Long saveOrder(IoOrderCreateDTO dto);

    void updateOrder(IoOrderUpdateDTO dto);

    void pickById(Long id);

    void removeByIdChecked(Long id);
}
