package com.zhb.wms2.module.product.service.support;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.zhb.wms2.common.exception.BaseException;
import com.zhb.wms2.module.product.mapper.ProductMapper;
import com.zhb.wms2.module.product.model.entity.Product;
import com.zhb.wms2.module.product.model.entity.ProductStockDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * ProductStockSummaryService 服务
 *
 * @author zhb
 * @since 2026/3/26
 */
@Service
@RequiredArgsConstructor
public class ProductStockSummaryService {

    private final ProductMapper productMapper;

    /**
     * 根据库存明细重新汇总商品总库存和货位列表。
     */
    public void syncByDetailMap(Long productId, Map<Long, ProductStockDetail> detailMap) {
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new BaseException("商品不存在");
        }

        long totalStockQty = 0L;
        String locationIdsStr = null;
        if (detailMap != null && !detailMap.isEmpty()) {
            totalStockQty = detailMap.values().stream()
                    .map(ProductStockDetail::getQty)
                    .filter(Objects::nonNull)
                    .reduce(0L, Long::sum);
            locationIdsStr = detailMap.keySet().stream()
                    .sorted(Comparator.naturalOrder())
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));
            if (StrUtil.isBlank(locationIdsStr)) {
                locationIdsStr = null;
            }
        }

        int updated = productMapper.update(null, new LambdaUpdateWrapper<Product>()
                .eq(Product::getId, productId)
                .set(Product::getTotalStockQty, totalStockQty)
                .set(Product::getLocationIdsStr, locationIdsStr));
        if (updated == 0) {
            throw new BaseException("商品不存在");
        }
    }
}
