package com.zhb.wms2.module.io.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhb.wms2.module.io.model.dto.IoOrderDetailStockQtyDTO;
import com.zhb.wms2.module.io.model.entity.IoOrderDetail;
import com.zhb.wms2.module.product.model.query.StockIoDetailQuery;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

public interface IoOrderDetailMapper extends BaseMapper<IoOrderDetail> {

    IPage<IoOrderDetail> selectPageByProductId(Page<IoOrderDetail> page,
                                               @Param("query") StockIoDetailQuery query);

    Long sumDeltaToDetailIdByProductId(@Param("productId") Long productId,
                                       @Param("detailId") Long detailId);

    List<IoOrderDetailStockQtyDTO> selectCurrentStockQtyByDetailIds(@Param("detailIds") Collection<Long> detailIds);
}
