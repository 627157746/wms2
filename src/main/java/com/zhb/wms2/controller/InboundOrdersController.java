package com.zhb.wms2.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zhb.wms2.common.R;
import com.zhb.wms2.model.dto.InboundOrderCreateDTO;
import com.zhb.wms2.model.dto.InboundOrderUpdateDTO;
import com.zhb.wms2.model.dto.InboundOrdersQuery;
import com.zhb.wms2.model.vo.InboundOrdersVO;
import com.zhb.wms2.service.InboundOrdersService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 入库单管理控制器
 */
@Tag(name = "入库单管理", description = "入库单相关接口")
@RestController
@RequestMapping("/inbound-orders")
@RequiredArgsConstructor
@Validated
public class InboundOrdersController {

    private final InboundOrdersService inboundOrdersService;

    @PostMapping
    @Operation(summary = "添加入库单信息")
    public R<Long> save(@Parameter(description = "入库单信息", required = true) @RequestBody @Validated InboundOrderCreateDTO dto) {
        Long id = inboundOrdersService.createInboundOrder(dto);
        return R.ok(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "修改入库单信息（含明细）")
    public R<Void> updateInbound(@Parameter(description = "入库单ID", required = true) @PathVariable @NotNull @Min(1) Long id, @Parameter(description = "入库单信息", required = true) @RequestBody @Validated InboundOrderUpdateDTO dto) {
        dto.setId(id);
        inboundOrdersService.updateInbound(dto);
        return R.optOk();
    }

    @GetMapping
    @Operation(summary = "分页查询入库单信息（含明细）")
    public R<IPage<InboundOrdersVO>> page(@Parameter(description = "查询条件") @Validated InboundOrdersQuery query) {
        IPage<InboundOrdersVO> result = inboundOrdersService.queryPage(query);
        return R.ok(result);
    }


}
