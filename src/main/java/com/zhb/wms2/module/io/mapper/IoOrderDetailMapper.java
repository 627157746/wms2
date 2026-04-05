package com.zhb.wms2.module.io.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhb.wms2.module.io.model.dto.IoOrderDetailStockQtyDTO;
import com.zhb.wms2.module.io.model.entity.IoOrderDetail;
import com.zhb.wms2.module.product.model.query.StockIoDetailQuery;
import com.zhb.wms2.module.product.model.vo.StockIoDetailStatVO;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

/**
 * 入出库单明细 Mapper。
 *
 * @author zhb
 * @since 2026/3/26
 */
public interface IoOrderDetailMapper extends BaseMapper<IoOrderDetail> {

    /**
     * 按商品分页查询入出库单明细。
     */
    IPage<IoOrderDetail> selectPageByProductId(Page<IoOrderDetail> page,
                                               @Param("query") StockIoDetailQuery query);

    /**
     * 按商品查询全部入出库单明细。
     */
    List<IoOrderDetail> selectListByProductId(@Param("query") StockIoDetailQuery query);

    /**
     * 按商品统计入出库明细数量。
     */
    StockIoDetailStatVO selectStatByQuery(@Param("query") StockIoDetailQuery query);

    /**
     * 统计商品在指定明细之前的库存增减数量。
     */
    Long sumDeltaToDetailIdByProductId(@Param("productId") Long productId,
                                       @Param("detailId") Long detailId);

    /**
     * 按明细 ID 集合查询当前库存数量。
     */
    List<IoOrderDetailStockQtyDTO> selectCurrentStockQtyByDetailIds(@Param("detailIds") Collection<Long> detailIds);
}
