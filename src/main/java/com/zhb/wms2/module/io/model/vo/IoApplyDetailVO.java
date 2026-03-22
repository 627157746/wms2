package com.zhb.wms2.module.io.model.vo;

import com.zhb.wms2.module.io.model.entity.IoApplyDetail;
import com.zhb.wms2.module.product.model.vo.ProductPageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "出入库申请明细结果")
public class IoApplyDetailVO extends IoApplyDetail {

    @Schema(description = "商品信息")
    private ProductPageVO product;
}
