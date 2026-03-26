package com.zhb.wms2.module.base.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zhb.wms2.common.model.R;
import com.zhb.wms2.common.validated.Save;
import com.zhb.wms2.common.validated.Update;
import com.zhb.wms2.module.base.model.entity.Customer;
import com.zhb.wms2.module.base.model.query.CustomerQuery;
import com.zhb.wms2.module.base.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 客户控制器
 *
 * @author zhb
 * @since 2026/3/26
 */
@RestController
@RequestMapping("/base/customer")
@Tag(name = "客户", description = "客户管理")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    @Operation(summary = "新增客户")
    public R<Long> create(
            @Parameter(description = "客户", required = true)
            @RequestBody @Validated(Save.class) Customer customer) {
        customerService.saveChecked(customer);
        return R.ok(customer.getId());
    }

    @PutMapping
    @Operation(summary = "修改客户")
    public R<Void> update(
            @Parameter(description = "客户", required = true)
            @RequestBody @Validated(Update.class) Customer customer) {
        customerService.updateByIdChecked(customer);
        return R.optOk();
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询客户详情")
    public R<Customer> getById(
            @Parameter(description = "客户ID", required = true)
            @PathVariable @NotNull @Min(1) Long id) {
        return R.ok(customerService.getById(id));
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询客户")
    public R<IPage<Customer>> page(
            @Parameter(description = "查询条件")
            @Validated CustomerQuery query) {
        return R.ok(customerService.pageQuery(query));
    }

    @GetMapping("/all")
    @Operation(summary = "查询全部客户")
    public R<List<Customer>> all() {
        return R.ok(customerService.listAll());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除客户")
    public R<Void> delete(
            @Parameter(description = "客户ID", required = true)
            @PathVariable @NotNull @Min(1) Long id) {
        customerService.removeByIdChecked(id);
        return R.optOk();
    }
}
