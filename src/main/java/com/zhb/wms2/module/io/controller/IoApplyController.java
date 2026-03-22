package com.zhb.wms2.module.io.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zhb.wms2.common.model.R;
import com.zhb.wms2.common.validated.Save;
import com.zhb.wms2.common.validated.Update;
import com.zhb.wms2.module.io.model.dto.IoApplyCreateDTO;
import com.zhb.wms2.module.io.model.dto.IoApplyUpdateDTO;
import com.zhb.wms2.module.io.model.dto.IoOrderGenerateDTO;
import com.zhb.wms2.module.io.model.query.IoApplyQuery;
import com.zhb.wms2.module.io.model.vo.IoApplyPageVO;
import com.zhb.wms2.module.io.service.IoApplyService;
import com.zhb.wms2.module.io.service.IoOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/io/apply")
@Tag(name = "出入库申请", description = "出入库申请管理")
@RequiredArgsConstructor
@Validated
public class IoApplyController {

    private final IoApplyService ioApplyService;
    private final IoOrderService ioOrderService;

    @PostMapping
    @Operation(summary = "新增出入库申请")
    public R<Long> create(
            @Parameter(description = "出入库申请", required = true)
            @RequestBody @Validated(Save.class) IoApplyCreateDTO dto) {
        return R.ok(ioApplyService.saveApply(dto));
    }

    @PostMapping("/{id}/generateOrder")
    @Operation(summary = "根据申请生成出入库单")
    public R<Long> generateOrder(
            @Parameter(description = "出入库申请ID", required = true)
            @PathVariable @NotNull @Min(1) Long id,
            @Parameter(description = "生成参数", required = true)
            @RequestBody @Validated(Save.class) IoOrderGenerateDTO dto) {
        return R.ok(ioOrderService.generateOrderByApply(id, dto));
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询出入库申请")
    public R<IPage<IoApplyPageVO>> page(
            @Parameter(description = "查询条件")
            @Validated IoApplyQuery query) {
        return R.ok(ioApplyService.pageQuery(query));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询出入库申请详情")
    public R<IoApplyPageVO> getById(
            @Parameter(description = "出入库申请ID", required = true)
            @PathVariable @NotNull @Min(1) Long id) {
        return R.ok(ioApplyService.getDetailById(id));
    }

    @PutMapping
    @Operation(summary = "修改出入库申请")
    public R<Void> update(
            @Parameter(description = "出入库申请", required = true)
            @RequestBody @Validated(Update.class) IoApplyUpdateDTO dto) {
        ioApplyService.updateApply(dto);
        return R.optOk();
    }

    @PutMapping("/{id}/approve")
    @Operation(summary = "审批出入库申请")
    public R<Void> approve(
            @Parameter(description = "出入库申请ID", required = true)
            @PathVariable @NotNull @Min(1) Long id) {
        ioApplyService.approveById(id);
        return R.optOk();
    }

    @PutMapping("/{id}/cancelApprove")
    @Operation(summary = "取消审批出入库申请")
    public R<Void> cancelApprove(
            @Parameter(description = "出入库申请ID", required = true)
            @PathVariable @NotNull @Min(1) Long id) {
        ioApplyService.cancelApproveById(id);
        return R.optOk();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除出入库申请")
    public R<Void> delete(
            @Parameter(description = "出入库申请ID", required = true)
            @PathVariable @NotNull @Min(1) Long id) {
        ioApplyService.removeByIdChecked(id);
        return R.optOk();
    }
}
