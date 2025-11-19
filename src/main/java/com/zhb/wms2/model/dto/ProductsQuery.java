package com.zhb.wms2.model.dto;

import com.zhb.wms2.common.BaseQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author zhb
 * @Description 商品信息查询条件
 * @Date 2025/11/19 16:19
 */
@Data
@EqualsAndHashCode(callSuper=true)
@Schema(description="商品信息查询条件")
public class ProductsQuery extends BaseQuery {

    /**
     * 商品编号
     */
    @Schema(description="商品编号")
    private String productCode;

    /**
     * 商品名称
     */
    @Schema(description="商品名称")
    private String productName;

    /**
     * 商品品牌
     */
    @Schema(description="商品品牌")
    private String brand;

    /**
     * 商品规格
     */
    @Schema(description="商品规格")
    private String specification;

}
