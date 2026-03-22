package com.zhb.wms2.module.home.mapper;

import com.zhb.wms2.module.home.model.vo.HomeStatVO;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;

public interface HomeStatMapper {

    HomeStatVO selectHomeStat(@Param("today") LocalDate today,
                              @Param("inboundType") Integer inboundType,
                              @Param("outboundType") Integer outboundType,
                              @Param("unapprovedStatus") Integer unapprovedStatus);
}
