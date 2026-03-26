package com.zhb.wms2.module.home.service.impl;

import com.zhb.wms2.common.enums.ApproveStatusEnum;
import com.zhb.wms2.common.enums.IoBizTypeEnum;
import com.zhb.wms2.module.home.mapper.HomeStatMapper;
import com.zhb.wms2.module.home.model.vo.HomeStatVO;
import com.zhb.wms2.module.home.service.HomeStatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * HomeStatServiceImpl 服务实现
 *
 * @author zhb
 * @since 2026/3/26
 */
@Service
@RequiredArgsConstructor
public class HomeStatServiceImpl implements HomeStatService {

    private final HomeStatMapper homeStatMapper;

    /**
     * 查询首页统计数据。
     */
    @Override
    public HomeStatVO getHomeStat() {
        // 首页统计统一以当天日期和固定状态枚举做汇总。
        return homeStatMapper.selectHomeStat(
                LocalDate.now(),
                IoBizTypeEnum.INBOUND.getCode(),
                IoBizTypeEnum.OUTBOUND.getCode(),
                ApproveStatusEnum.UNAPPROVED.getCode()
        );
    }
}
