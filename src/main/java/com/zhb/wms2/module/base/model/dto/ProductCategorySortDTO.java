package com.zhb.wms2.module.base.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Data;

/**
 * 商品分类同级排序DTO DTO
 *
 * @author zhb
 * @since 2026/3/26
 */
@Schema(description = "商品分类同级排序DTO")
@Data
public class ProductCategorySortDTO {

    /**
     * 父级 ID。
     */
    @Schema(description = "父级ID，0为顶级")
    @NotNull(message = "父级ID不能为空")
    @Min(value = 0, message = "父级ID不能小于0")
    private Long parentId;

    /**
     * 同级分类 ID 顺序列表。
     */
    @Schema(description = "同级分类ID顺序")
    @NotEmpty(message = "分类ID列表不能为空")
    private List<@NotNull(message = "分类ID不能为空") @Min(value = 1, message = "分类ID不能为空") Long> categoryIdList;
}
