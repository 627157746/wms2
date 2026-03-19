package com.zhb.wms2.module.io.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhb.wms2.module.io.model.entity.IoOrderDetail;
import org.apache.ibatis.annotations.Param;

public interface IoOrderDetailMapper extends BaseMapper<IoOrderDetail> {

    Long sumDeltaToDetailIdByProductId(@Param("productId") Long productId,
                                       @Param("detailId") Long detailId);
}
