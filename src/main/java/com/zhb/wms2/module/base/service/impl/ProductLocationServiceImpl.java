package com.zhb.wms2.module.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhb.wms2.common.exception.BaseException;
import com.zhb.wms2.module.base.mapper.ProductLocationMapper;
import com.zhb.wms2.module.base.model.entity.ProductLocation;
import com.zhb.wms2.module.base.model.query.ProductLocationQuery;
import com.zhb.wms2.module.base.service.ProductLocationService;
import com.zhb.wms2.module.inbound.model.entity.InboundOrderDetail;
import com.zhb.wms2.module.inbound.service.InboundOrderDetailService;
import com.zhb.wms2.module.inventory.model.entity.Inventory;
import com.zhb.wms2.module.inventory.model.entity.InventoryDetail;
import com.zhb.wms2.module.inventory.service.InventoryDetailService;
import com.zhb.wms2.module.inventory.service.InventoryService;
import com.zhb.wms2.module.outbound.model.entity.OutboundOrderDetail;
import com.zhb.wms2.module.outbound.service.OutboundOrderDetailService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @Author zhb
 * @Description
 * @Date 2026/3/17 19:02
 */
@Service
@RequiredArgsConstructor
public class ProductLocationServiceImpl extends ServiceImpl<ProductLocationMapper, ProductLocation> implements ProductLocationService {

    private final InboundOrderDetailService inboundOrderDetailService;
    private final OutboundOrderDetailService outboundOrderDetailService;
    private final InventoryDetailService inventoryDetailService;
    private final InventoryService inventoryService;

    @Override
    public IPage<ProductLocation> pageQuery(ProductLocationQuery query) {
        LambdaQueryWrapper<ProductLocation> wrapper = buildWrapper(query);
        return page(new Page<>(query.getCurrent(), query.getSize()), wrapper);
    }

    @Override
    public List<ProductLocation> listAll() {
        return list(new LambdaQueryWrapper<ProductLocation>().orderByDesc(ProductLocation::getId));
    }

    @Override
    public void removeByIdChecked(Long id) {
        long inboundCount = inboundOrderDetailService.count(
                new LambdaQueryWrapper<InboundOrderDetail>().eq(InboundOrderDetail::getLocationId, id));
        if (inboundCount > 0) {
            throw new BaseException("该货位已被入库单使用，无法删除");
        }
        long outboundCount = outboundOrderDetailService.count(
                new LambdaQueryWrapper<OutboundOrderDetail>().eq(OutboundOrderDetail::getLocationId, id));
        if (outboundCount > 0) {
            throw new BaseException("该货位已被出库单使用，无法删除");
        }
        long inventoryDetailCount = inventoryDetailService.count(
                new LambdaQueryWrapper<InventoryDetail>().eq(InventoryDetail::getLocationId, id));
        if (inventoryDetailCount > 0) {
            throw new BaseException("该货位已被库存明细使用，无法删除");
        }
        long inventoryCount = inventoryService.count(
                new LambdaQueryWrapper<Inventory>().apply("FIND_IN_SET({0}, location_ids)", id));
        if (inventoryCount > 0) {
            throw new BaseException("该货位已被库存主表使用，无法删除");
        }
        removeById(id);
    }

    private LambdaQueryWrapper<ProductLocation> buildWrapper(ProductLocationQuery query) {
        return new LambdaQueryWrapper<ProductLocation>()
                .like(StringUtils.hasText(query.getCode()), ProductLocation::getCode, query.getCode())
                .orderByDesc(ProductLocation::getId);
    }
}
