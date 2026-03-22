package com.zhb.wms2.module.home.service.impl;

import com.zhb.wms2.common.enums.ApproveStatusEnum;
import com.zhb.wms2.common.enums.IoBizTypeEnum;
import com.zhb.wms2.module.home.mapper.HomeStatMapper;
import com.zhb.wms2.module.home.model.vo.HomeStatVO;
import com.zhb.wms2.module.home.service.HomeStatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class HomeStatServiceImpl implements HomeStatService {

    private final HomeStatMapper homeStatMapper;

    @Override
    public HomeStatVO getHomeStat() {
        return homeStatMapper.selectHomeStat(
                LocalDate.now(),
                IoBizTypeEnum.INBOUND.getCode(),
                IoBizTypeEnum.OUTBOUND.getCode(),
                ApproveStatusEnum.UNAPPROVED.getCode()
        );
    }
}
