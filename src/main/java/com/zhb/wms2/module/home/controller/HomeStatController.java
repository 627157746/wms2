package com.zhb.wms2.module.home.controller;

import com.zhb.wms2.common.model.R;
import com.zhb.wms2.module.home.model.vo.HomeStatVO;
import com.zhb.wms2.module.home.service.HomeStatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/home/stat")
@Tag(name = "首页统计", description = "首页统计")
@RequiredArgsConstructor
public class HomeStatController {

    private final HomeStatService homeStatService;

    @GetMapping
    @Operation(summary = "获取首页统计")
    public R<HomeStatVO> getHomeStat() {
        return R.ok(homeStatService.getHomeStat());
    }
}
