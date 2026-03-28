package com.zhb.wms2.module.io.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhb.wms2.module.product.model.query.StockIoDetailQuery;
import com.zhb.wms2.module.product.model.vo.StockIoDetailStatVO;
import com.zhb.wms2.module.product.model.vo.StockIoDetailVO;
import com.zhb.wms2.module.io.model.dto.IoOrderCreateDTO;
import com.zhb.wms2.module.io.model.dto.IoOrderDetailLocationUpdateDTO;
import com.zhb.wms2.module.io.model.dto.IoOrderGenerateDTO;
import com.zhb.wms2.module.io.model.dto.IoOrderUpdateDTO;
import com.zhb.wms2.module.io.model.entity.IoOrder;
import com.zhb.wms2.module.io.model.query.IoOrderQuery;
import com.zhb.wms2.module.io.model.vo.IoOrderPageVO;

/**
 * IoOrderService 服务接口
 *
 * @author zhb
 * @since 2026/3/26
 */
public interface IoOrderService extends IService<IoOrder> {

    /**
     * 分页查询出入库单。
     */
    IPage<IoOrderPageVO> pageQuery(IoOrderQuery query);

    /**
     * 查询出入库单详情。
     */
    IoOrderPageVO getDetailById(Long id);

    /**
     * 按商品分页查询出入库流水。
     */
    IPage<StockIoDetailVO> pageDetailByProductId(StockIoDetailQuery query);

    /**
     * 按商品统计出入库流水数量。
     */
    StockIoDetailStatVO getDetailStatByProductId(StockIoDetailQuery query);

    /**
     * 根据申请生成出入库单。
     */
    Long generateOrderByApply(Long applyId, IoOrderGenerateDTO dto);

    /**
     * 手工新增出入库单。
     */
    Long saveOrder(IoOrderCreateDTO dto);

    /**
     * 修改出入库单。
     */
    void updateOrder(IoOrderUpdateDTO dto);

    /**
     * 修改出入库明细货位。
     */
    void updateDetailLocation(IoOrderDetailLocationUpdateDTO dto);

    /**
     * 执行拣货。
     */
    void pickById(Long id);

    /**
     * 删除出入库单。
     */
    void removeByIdChecked(Long id);
}
