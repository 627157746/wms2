package com.zhb.wms2.module.home.mapper;

import com.zhb.wms2.module.home.model.vo.HomeStatVO;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;

/**
 * HomeStatMapper Mapper
 *
 * @author zhb
 * @since 2026/3/26
 */
public interface HomeStatMapper {

    /**
     * 查询首页看板统计数据。
     */
    HomeStatVO selectHomeStat(@Param("today") LocalDate today,
                              @Param("inboundType") Integer inboundType,
                              @Param("outboundType") Integer outboundType,
                              @Param("unapprovedStatus") Integer unapprovedStatus);
}
