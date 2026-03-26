package com.zhb.wms2.module.base.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 商品分类树节点 VO
 *
 * @author zhb
 * @since 2026/3/26
 */
@Data
@Accessors(chain = true)
@Schema(description = "商品分类树节点")
public class ProductCategoryTreeVO {

    /**
     * 主键 ID。
     */
    @Schema(description = "主键ID")
    private Long id;

    /**
     * 分类名称。
     */
    @Schema(description = "分类名称")
    private String name;

    /**
     * 父级 ID。
     */
    @Schema(description = "父级ID，0为顶级")
    private Long parentId;

    /**
     * 排序值。
     */
    @Schema(description = "排序")
    private Integer sortOrder;

    /**
     * 层级。
     */
    @Schema(description = "层级，由系统计算")
    private Integer level;

    /**
     * 子分类列表。
     */
    @Schema(description = "子分类")
    private List<ProductCategoryTreeVO> children;
}
