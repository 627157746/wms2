package com.zhb.wms2.module.base.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;

@Data
@Schema(description = "商品分类树节点")
public class ProductCategoryTreeVO {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "分类名称")
    private String name;

    @Schema(description = "父级ID，0为顶级")
    private Long parentId;

    @Schema(description = "排序")
    private Integer sortOrder;

    @Schema(description = "层级")
    private Integer level;

    @Schema(description = "子分类")
    private List<ProductCategoryTreeVO> children;
}
