package com.zhb.wms2.module.base.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zhb.wms2.common.model.R;
import com.zhb.wms2.common.validated.Save;
import com.zhb.wms2.common.validated.Update;
import com.zhb.wms2.module.base.model.dto.BaseSortUpdateDTO;
import com.zhb.wms2.module.base.model.entity.Salesman;
import com.zhb.wms2.module.base.model.query.SalesmanQuery;
import com.zhb.wms2.module.base.service.SalesmanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 业务员控制器
 *
 * @author zhb
 * @since 2026/3/26
 */
@RestController
@RequestMapping("/base/salesman")
@Tag(name = "业务员", description = "业务员管理")
@RequiredArgsConstructor
public class SalesmanController {

    private final SalesmanService salesmanService;

    @PostMapping
    @Operation(summary = "新增业务员")
    public R<Long> create(
            @Parameter(description = "业务员", required = true)
            @RequestBody @Validated(Save.class) Salesman salesman) {
        salesmanService.saveChecked(salesman);
        return R.ok(salesman.getId());
    }

    @PutMapping
    @Operation(summary = "修改业务员")
    public R<Void> update(
            @Parameter(description = "业务员", required = true)
            @RequestBody @Validated(Update.class) Salesman salesman) {
        salesmanService.updateByIdChecked(salesman);
        return R.optOk();
    }

    @PutMapping("/sort")
    @Operation(summary = "批量修改业务员排序")
    public R<Void> sort(
            @Parameter(description = "排序参数", required = true)
            @RequestBody @Validated List<@Valid BaseSortUpdateDTO> dtoList) {
        salesmanService.updateSortOrderBatch(dtoList);
        return R.optOk();
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询业务员详情")
    public R<Salesman> getById(
            @Parameter(description = "业务员ID", required = true)
            @PathVariable @NotNull @Min(1) Long id) {
        return R.ok(salesmanService.getById(id));
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询业务员")
    public R<IPage<Salesman>> page(
            @Parameter(description = "查询条件")
            @Validated SalesmanQuery query) {
        return R.ok(salesmanService.pageQuery(query));
    }

    @GetMapping("/all")
    @Operation(summary = "查询全部业务员")
    public R<List<Salesman>> all() {
        return R.ok(salesmanService.listAll());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除业务员")
    public R<Void> delete(
            @Parameter(description = "业务员ID", required = true)
            @PathVariable @NotNull @Min(1) Long id) {
        salesmanService.removeByIdChecked(id);
        return R.optOk();
    }
}
