package com.zhb.wms2.module.product.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhb.wms2.module.io.model.dto.IoOrderDetailDTO;
import com.zhb.wms2.module.io.model.entity.IoOrder;
import com.zhb.wms2.module.product.model.dto.StockCheckTaskActualQtyDTO;
import com.zhb.wms2.module.product.model.dto.StockCheckTaskAddProductDTO;
import com.zhb.wms2.module.product.model.dto.StockCheckTaskAddCategoryDTO;
import com.zhb.wms2.module.product.model.dto.StockCheckTaskBatchAddDTO;
import com.zhb.wms2.module.product.model.dto.StockCheckTaskCreateDTO;
import com.zhb.wms2.module.product.model.entity.StockCheckTask;
import com.zhb.wms2.module.product.model.query.StockCheckTaskQuery;
import com.zhb.wms2.module.product.model.vo.StockCheckTaskPageVO;
import com.zhb.wms2.module.product.model.vo.StockCheckTaskVO;
import java.util.List;

/**
 * 盘点任务服务接口。
 *
 * @author zhb
 * @since 2026/4/12
 */
public interface StockCheckTaskService extends IService<StockCheckTask> {

    /**
     * 分页查询盘点任务。
     */
    IPage<StockCheckTaskPageVO> pageQuery(StockCheckTaskQuery query);

    /**
     * 查询盘点任务详情。
     */
    StockCheckTaskVO getDetailById(Long id);

    /**
     * 创建盘点任务。
     */
    Long createTask(StockCheckTaskCreateDTO dto);

    /**
     * 新增盘点商品。
     */
    Long addProduct(StockCheckTaskAddProductDTO dto);

    /**
     * 快捷加入库存不为0的商品。
     */
    Long addNonZeroStockProducts(StockCheckTaskBatchAddDTO dto);

    /**
     * 快捷加入指定分类商品。
     */
    Long addCategoryProducts(StockCheckTaskAddCategoryDTO dto);

    /**
     * 删除盘点任务。
     */
    void removeByIdDirect(Long id);

    /**
     * 删除盘点商品。
     */
    void removeDetailById(Long detailId);

    /**
     * 录入盘点数量。
     */
    void updateActualQty(StockCheckTaskActualQtyDTO dto);

    /**
     * 手动结束盘点。
     */
    void finishTask(Long id);

    /**
     * 绑定盘点调整单。
     */
    void bindAdjustOrder(Long taskId, IoOrder ioOrder, List<IoOrderDetailDTO> detailDTOList);

    /**
     * 校验调整单是否已被盘点任务关联。
     */
    void validateOrderNotLinked(Long orderId);
}
