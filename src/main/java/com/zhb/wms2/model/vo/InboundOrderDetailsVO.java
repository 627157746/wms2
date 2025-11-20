package com.zhb.wms2.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 入库单明细VO
 * 包含商品信息和物料位信息
 */
@Schema(description = "入库单明细VO")
@Data
public class InboundOrderDetailsVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 明细ID
     */
    @Schema(description = "明细ID")
    private Long id;

    /**
     * 入库单ID
     */
    @Schema(description = "入库单ID")
    private Long inboundId;

    /**
     * 商品ID
     */
    @Schema(description = "商品ID")
    private Long productId;

    /**
     * 商品编号
     */
    @Schema(description = "商品编号")
    private String productCode;

    /**
     * 商品名称
     */
    @Schema(description = "商品名称")
    private String productName;

    /**
     * 商品规格
     */
    @Schema(description = "商品规格")
    private String specification;

    /**
     * 商品品牌
     */
    @Schema(description = "商品品牌")
    private String brand;

    /**
     * 物料位ID
     */
    @Schema(description = "物料位ID")
    private Long materialLocationId;

    /**
     * 物料位编码
     */
    @Schema(description = "物料位编码")
    private String locationCode;

    /**
     * 排号
     */
    @Schema(description = "排号")
    private String rowNo;

    /**
     * 段号
     */
    @Schema(description = "段号")
    private String sectionNo;

    /**
     * 数量
     */
    @Schema(description = "数量")
    private Integer quantity;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remarks;
}

