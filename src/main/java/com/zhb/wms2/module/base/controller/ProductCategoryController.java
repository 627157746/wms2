package com.zhb.wms2.module.base.controller;

import com.zhb.wms2.common.model.R;
import com.zhb.wms2.common.validated.Save;
import com.zhb.wms2.common.validated.Update;
import com.zhb.wms2.module.base.model.dto.ProductCategorySortDTO;
import com.zhb.wms2.module.base.model.entity.ProductCategory;
import com.zhb.wms2.module.base.model.vo.ProductCategoryTreeVO;
import com.zhb.wms2.module.base.service.ProductCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/base/productCategory")
@Tag(name = "商品分类", description = "商品分类管理")
@RequiredArgsConstructor
@Validated
public class ProductCategoryController {

    private final ProductCategoryService productCategoryService;

    @PostMapping
    @Operation(summary = "新增商品分类")
    public R<Long> create(
            @Parameter(description = "商品分类", required = true)
            @RequestBody @Validated(Save.class) ProductCategory category) {
        productCategoryService.saveChecked(category);
        return R.ok(category.getId());
    }

    @PutMapping
    @Operation(summary = "修改商品分类")
    public R<Void> update(
            @Parameter(description = "商品分类", required = true)
            @RequestBody @Validated(Update.class) ProductCategory category) {
        productCategoryService.updateByIdChecked(category);
        return R.optOk();
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询商品分类详情")
    public R<ProductCategory> getById(
            @Parameter(description = "分类ID", required = true)
            @PathVariable @NotNull @Min(1) Long id) {
        return R.ok(productCategoryService.getById(id));
    }

    @GetMapping("/tree")
    @Operation(summary = "树形查询商品分类")
    public R<List<ProductCategoryTreeVO>> tree() {
        return R.ok(productCategoryService.tree());
    }

    @PutMapping("/sort")
    @Operation(summary = "同级商品分类重新排序")
    public R<Void> sort(
            @Parameter(description = "商品分类排序参数", required = true)
            @RequestBody @Validated ProductCategorySortDTO dto) {
        productCategoryService.sortSameLevel(dto);
        return R.optOk();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除商品分类")
    public R<Void> delete(
            @Parameter(description = "分类ID", required = true)
            @PathVariable @NotNull @Min(1) Long id) {
        productCategoryService.removeByIdChecked(id);
        return R.optOk();
    }
}
