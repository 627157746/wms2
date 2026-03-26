package com.zhb.wms2.module.product.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zhb.wms2.common.lock.MethodLock;
import com.zhb.wms2.common.model.R;
import com.zhb.wms2.module.product.model.dto.LocationTransferCreateDTO;
import com.zhb.wms2.module.product.model.query.LocationTransferQuery;
import com.zhb.wms2.module.product.model.vo.LocationTransferPageVO;
import com.zhb.wms2.module.product.service.LocationTransferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 转货位控制器
 *
 * @author zhb
 * @since 2026/3/26
 */
@RestController
@RequestMapping("/product/locationTransfer")
@Tag(name = "转货位", description = "转货位管理")
@RequiredArgsConstructor
public class LocationTransferController {

    private final LocationTransferService locationTransferService;

    @PostMapping
    @Operation(summary = "发起转货位")
    @MethodLock(name = "product:stock", key = "#p0.productId")
    public R<Long> create(
            @Parameter(description = "转货位参数", required = true)
            @RequestBody @Validated LocationTransferCreateDTO dto) {
        return R.ok(locationTransferService.createTransfer(dto));
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询转货位记录")
    public R<IPage<LocationTransferPageVO>> page(
            @Parameter(description = "查询条件")
            @Validated LocationTransferQuery query) {
        return R.ok(locationTransferService.pageQuery(query));
    }
}
