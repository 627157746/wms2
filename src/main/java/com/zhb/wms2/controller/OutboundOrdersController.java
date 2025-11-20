package com.zhb.wms2.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zhb.wms2.common.R;
import com.zhb.wms2.model.dto.OutboundOrderCreateDTO;
import com.zhb.wms2.model.dto.OutboundOrderUpdateDTO;
import com.zhb.wms2.model.dto.OutboundOrdersQuery;
import com.zhb.wms2.model.vo.OutboundOrdersVO;
import com.zhb.wms2.service.OutboundOrdersService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 出库单管理控制器
 */
@Tag(name = "出库单管理", description = "出库单相关接口")
@RestController
@RequestMapping("/outbound-orders")
@RequiredArgsConstructor
@Validated
public class OutboundOrdersController {

    private final OutboundOrdersService outboundOrdersService;

    @PostMapping
    @Operation(summary = "添加出库单信息")
    public R<Long> save(@Parameter(description = "出库单信息", required = true) @RequestBody @Validated OutboundOrderCreateDTO dto) {
        Long id = outboundOrdersService.createOutboundOrder(dto);
        return R.ok(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "修改出库单信息（含明细）")
    public R<Void> updateOutbound(@Parameter(description = "出库单ID", required = true) @PathVariable @NotNull @Min(1) Long id, @Parameter(description = "出库单信息", required = true) @RequestBody @Validated OutboundOrderUpdateDTO dto) {
        dto.setId(id);
        outboundOrdersService.updateOutbound(dto);
        return R.optOk();
    }

    @GetMapping
    @Operation(summary = "分页查询出库单信息（含明细）")
    public R<IPage<OutboundOrdersVO>> page(@Parameter(description = "查询条件") @Validated OutboundOrdersQuery query) {
        IPage<OutboundOrdersVO> result = outboundOrdersService.queryPage(query);
        return R.ok(result);
    }

}

