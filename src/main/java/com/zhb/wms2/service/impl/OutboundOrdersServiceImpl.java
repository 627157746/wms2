package com.zhb.wms2.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhb.wms2.common.exception.BaseException;
import com.zhb.wms2.mapper.OutboundOrdersMapper;
import com.zhb.wms2.model.*;
import com.zhb.wms2.model.dto.OutboundOrderCreateDTO;
import com.zhb.wms2.model.dto.OutboundOrderDetailItemDTO;
import com.zhb.wms2.model.dto.OutboundOrderUpdateDTO;
import com.zhb.wms2.model.dto.OutboundOrdersQuery;
import com.zhb.wms2.model.vo.OutboundOrderDetailsVO;
import com.zhb.wms2.model.vo.OutboundOrdersVO;
import com.zhb.wms2.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author zhb
 * @Description
 * @Date 2025/11/19 16:19
 */
@Service
@RequiredArgsConstructor
public class OutboundOrdersServiceImpl extends ServiceImpl<OutboundOrdersMapper, OutboundOrders> implements OutboundOrdersService {

    private final OutboundOrderDetailsService outboundOrderDetailsService;

    private final StoragePositionsService storagePositionsService;

    private final ProductsService productsService;

    private final MaterialLocationsService materialLocationsService;

    private final WarehouseTypesService warehouseTypesService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createOutboundOrder(OutboundOrderCreateDTO dto) {
        // 1. 创建出库单主表
        OutboundOrders outboundOrder = new OutboundOrders();

        // 生成出库单号
        String outboundCode = generateOutboundCode();
        outboundOrder.setOutboundCode(outboundCode);
        outboundOrder.setOutboundTypeId(dto.getOutboundTypeId());
        outboundOrder.setOutboundDate(dto.getOutboundDate());
        outboundOrder.setOperator(dto.getOperator());
        outboundOrder.setRemarks(dto.getRemarks());

        // 计算总数量
        int totalQuantity = dto.getDetails().stream()
                .mapToInt(OutboundOrderDetailItemDTO::getQuantity)
                .sum();
        outboundOrder.setTotalQuantity(totalQuantity);

        // 保存出库单主表
        if (!save(outboundOrder)) {
            throw new BaseException("创建出库单失败");
        }

        // 2. 处理出库明细和库存
        List<OutboundOrderDetails> detailsList = new ArrayList<>();
        for (OutboundOrderDetailItemDTO detailDTO : dto.getDetails()) {
            // 2.1 减少货位库存
            decreaseStoragePositionStock(detailDTO.getMaterialLocationId(), detailDTO.getQuantity());

            // 2.2 创建出库明细
            OutboundOrderDetails detail = new OutboundOrderDetails();
            detail.setOutboundId(outboundOrder.getId());
            detail.setProductId(detailDTO.getProductId());
            detail.setMaterialLocationId(detailDTO.getMaterialLocationId());
            detail.setQuantity(detailDTO.getQuantity());
            detail.setRemarks(detailDTO.getRemarks());

            detailsList.add(detail);
        }

        // 批量保存明细
        if (!outboundOrderDetailsService.saveBatch(detailsList)) {
            throw new BaseException("创建出库单明细失败");
        }

        return outboundOrder.getId();
    }

    /**
     * 减少货位库存数量（出库）
     *
     * @param materialLocationId 物料位ID
     * @param quantity           出库数量
     */
    private void decreaseStoragePositionStock(Long materialLocationId, Integer quantity) {
        // 1. 根据 materialLocationId 查询货位记录
        LambdaQueryWrapper<StoragePositions> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StoragePositions::getMaterialLocationId, materialLocationId);

        StoragePositions storagePosition = storagePositionsService.getOne(wrapper);

        if (storagePosition == null) {
            throw new BaseException("货位库存记录不存在，无法出库");
        }

        // 2. 减少库存数量
        Integer currentQuantity = storagePosition.getCurrentQuantity();
        if (currentQuantity == null) {
            currentQuantity = 0;
        }

        Integer newQuantity = currentQuantity - quantity;

        if (newQuantity < 0) {
            throw new BaseException("库存不足，无法出库");
        }

        storagePosition.setCurrentQuantity(newQuantity);

        if (!storagePositionsService.updateById(storagePosition)) {
            throw new BaseException("更新货位库存失败");
        }
    }

    /**
     * 回滚货位库存数量（增加库存）
     * 用于修改出库单时，先回滚原明细的库存
     *
     * @param materialLocationId 物料位ID
     * @param quantity           需要增加的数量
     */
    private void rollbackStoragePositionStock(Long materialLocationId, Integer quantity) {
        // 1. 根据 materialLocationId 查询货位记录
        LambdaQueryWrapper<StoragePositions> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StoragePositions::getMaterialLocationId, materialLocationId);

        StoragePositions storagePosition = storagePositionsService.getOne(wrapper);

        if (storagePosition == null) {
            // 如果货位不存在，创建新记录
            storagePosition = new StoragePositions();
            storagePosition.setMaterialLocationId(materialLocationId);
            storagePosition.setCurrentQuantity(quantity);

            if (!storagePositionsService.save(storagePosition)) {
                throw new BaseException("创建货位库存失败");
            }
        } else {
            // 增加库存数量
            Integer currentQuantity = storagePosition.getCurrentQuantity();
            if (currentQuantity == null) {
                currentQuantity = 0;
            }
            storagePosition.setCurrentQuantity(currentQuantity + quantity);

            if (!storagePositionsService.updateById(storagePosition)) {
                throw new BaseException("回滚货位库存失败");
            }
        }
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateOutbound(OutboundOrderUpdateDTO dto) {
        // 1. 查询原明细，用于回滚库存
        LambdaQueryWrapper<OutboundOrderDetails> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OutboundOrderDetails::getOutboundId, dto.getId());
        List<OutboundOrderDetails> oldDetails = outboundOrderDetailsService.list(queryWrapper);

        // 2. 回滚原明细的库存（增加库存）
        for (OutboundOrderDetails oldDetail : oldDetails) {
            rollbackStoragePositionStock(oldDetail.getMaterialLocationId(), oldDetail.getQuantity());
        }

        // 3. 删除原明细
        if (!outboundOrderDetailsService.remove(queryWrapper)) {
            throw new BaseException("删除原出库单明细失败");
        }

        // 4. 更新主单信息
        OutboundOrders outboundOrder = new OutboundOrders();
        outboundOrder.setId(dto.getId());
        outboundOrder.setOutboundTypeId(dto.getOutboundTypeId());
        outboundOrder.setOutboundDate(dto.getOutboundDate());
        outboundOrder.setOperator(dto.getOperator());
        outboundOrder.setRemarks(dto.getRemarks());

        // 计算总数量
        int totalQuantity = dto.getDetails().stream()
                .mapToInt(OutboundOrderDetailItemDTO::getQuantity)
                .sum();
        outboundOrder.setTotalQuantity(totalQuantity);

        if (!updateById(outboundOrder)) {
            throw new BaseException("更新出库单失败");
        }

        // 5. 处理新明细和库存
        List<OutboundOrderDetails> detailsList = new ArrayList<>();
        for (OutboundOrderDetailItemDTO detailDTO : dto.getDetails()) {
            // 5.1 减少货位库存
            decreaseStoragePositionStock(detailDTO.getMaterialLocationId(), detailDTO.getQuantity());

            // 5.2 创建出库明细
            OutboundOrderDetails detail = new OutboundOrderDetails();
            detail.setOutboundId(dto.getId());
            detail.setProductId(detailDTO.getProductId());
            detail.setMaterialLocationId(detailDTO.getMaterialLocationId());
            detail.setQuantity(detailDTO.getQuantity());
            detail.setRemarks(detailDTO.getRemarks());

            detailsList.add(detail);
        }

        // 批量保存明细
        if (!outboundOrderDetailsService.saveBatch(detailsList)) {
            throw new BaseException("创建出库单明细失败");
        }
    }

    @Override
    public IPage<OutboundOrdersVO> queryPage(OutboundOrdersQuery query) {
        // 1. 构建查询条件
        LambdaQueryWrapper<OutboundOrders> wrapper = buildQueryWrapper(query);

        // 2. 执行分页查询
        Page<OutboundOrders> page = new Page<>(query.getCurrent(), query.getSize());
        IPage<OutboundOrders> outboundOrdersPage = page(page, wrapper);

        List<OutboundOrders> records = outboundOrdersPage.getRecords();
        if (records.isEmpty()) {
            return new Page<>(query.getCurrent(), query.getSize(), 0);
        }

        // 3. 批量查询所有明细
        List<Long> outboundIds = records.stream()
                .map(OutboundOrders::getId)
                .collect(Collectors.toList());

        LambdaQueryWrapper<OutboundOrderDetails> detailsWrapper = new LambdaQueryWrapper<>();
        detailsWrapper.in(OutboundOrderDetails::getOutboundId, outboundIds);
        List<OutboundOrderDetails> allDetails = outboundOrderDetailsService.list(detailsWrapper);

        // 4. 按 outboundId 分组
        Map<Long, List<OutboundOrderDetails>> detailsMap = allDetails.stream()
                .collect(Collectors.groupingBy(OutboundOrderDetails::getOutboundId));

        // 5. 批量查询商品和物料位信息
        List<Long> productIds = allDetails.stream()
                .map(OutboundOrderDetails::getProductId)
                .distinct()
                .collect(Collectors.toList());

        List<Long> materialLocationIds = allDetails.stream()
                .map(OutboundOrderDetails::getMaterialLocationId)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, Products> productMap = new HashMap<>();
        if (!productIds.isEmpty()) {
            List<Products> products = productsService.listByIds(productIds);
            productMap = products.stream()
                    .collect(Collectors.toMap(Products::getId, p -> p));
        }

        Map<Long, MaterialLocations> materialLocationMap = new HashMap<>();
        if (!materialLocationIds.isEmpty()) {
            List<MaterialLocations> materialLocations = materialLocationsService.listByIds(materialLocationIds);
            materialLocationMap = materialLocations.stream()
                    .collect(Collectors.toMap(MaterialLocations::getId, m -> m));
        }

        // 6. 批量查询出库类型信息
        List<Long> outboundTypeIds = records.stream()
                .map(OutboundOrders::getOutboundTypeId)
                .filter(id -> id != null)
                .map(Long::valueOf)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, WarehouseTypes> warehouseTypeMap = new HashMap<>();
        if (!outboundTypeIds.isEmpty()) {
            List<WarehouseTypes> warehouseTypes = warehouseTypesService.listByIds(outboundTypeIds);
            warehouseTypeMap = warehouseTypes.stream()
                    .collect(Collectors.toMap(WarehouseTypes::getId, wt -> wt));
        }

        // 7. 转换为VO
        final Map<Long, List<OutboundOrderDetails>> finalDetailsMap = detailsMap;
        final Map<Long, Products> finalProductMap = productMap;
        final Map<Long, MaterialLocations> finalMaterialLocationMap = materialLocationMap;
        final Map<Long, WarehouseTypes> finalWarehouseTypeMap = warehouseTypeMap;

        List<OutboundOrdersVO> voList = records.stream()
                .map(order -> convertToVO(order, finalDetailsMap, finalProductMap, finalMaterialLocationMap, finalWarehouseTypeMap))
                .collect(Collectors.toList());

        // 8. 构建分页结果
        Page<OutboundOrdersVO> voPage = new Page<>(query.getCurrent(), query.getSize(), outboundOrdersPage.getTotal());
        voPage.setRecords(voList);

        return voPage;
    }

    /**
     * 构建查询条件
     */
    private LambdaQueryWrapper<OutboundOrders> buildQueryWrapper(OutboundOrdersQuery query) {
        LambdaQueryWrapper<OutboundOrders> wrapper = new LambdaQueryWrapper<>();

        // 精确查询
        wrapper.eq(query.getOutboundCode() != null, OutboundOrders::getOutboundCode, query.getOutboundCode())
                .eq(query.getOutboundTypeId() != null, OutboundOrders::getOutboundTypeId, query.getOutboundTypeId())
                .eq(query.getOperator() != null, OutboundOrders::getOperator, query.getOperator());

        // 时间范围
        wrapper.ge(query.getStartTime() != null, OutboundOrders::getOutboundDate, query.getStartTime())
                .le(query.getEndTime() != null, OutboundOrders::getOutboundDate, query.getEndTime());

        // 排序
        wrapper.orderByDesc(OutboundOrders::getCreateTime);

        return wrapper;
    }

    /**
     * 生成出库单号
     */
    private String generateOutboundCode() {
        // 简单的出库单号生成规则：OUT + yyyyMMddHHmmss + 3位随机数
        String timePrefix = java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
                .format(java.time.LocalDateTime.now());
        int randomSuffix = (int) (Math.random() * 1000);
        return "OUT" + timePrefix + String.format("%03d", randomSuffix);
    }

    /**
     * 转换为VO对象
     */
    private OutboundOrdersVO convertToVO(OutboundOrders outboundOrder,
                                         Map<Long, List<OutboundOrderDetails>> detailsMap,
                                         Map<Long, Products> productMap,
                                         Map<Long, MaterialLocations> materialLocationMap,
                                         Map<Long, WarehouseTypes> warehouseTypeMap) {
        OutboundOrdersVO vo = new OutboundOrdersVO();

        // 1. 复制主单基本信息
        BeanUtil.copyProperties(outboundOrder, vo);

        // 2. 设置出库类型名称
        if (outboundOrder.getOutboundTypeId() != null) {
            WarehouseTypes warehouseType = warehouseTypeMap.get(Long.valueOf(outboundOrder.getOutboundTypeId()));
            if (warehouseType != null) {
                vo.setOutboundTypeName(warehouseType.getTypeName());
            }
        }

        // 3. 从Map中获取明细列表
        List<OutboundOrderDetails> detailsList = detailsMap.getOrDefault(outboundOrder.getId(), new ArrayList<>());

        // 4. 转换明细为VO
        List<OutboundOrderDetailsVO> detailsVOList = detailsList.stream()
                .map(detail -> convertDetailToVO(detail, productMap, materialLocationMap))
                .collect(Collectors.toList());

        vo.setDetails(detailsVOList);

        return vo;
    }

    /**
     * 转换明细为VO对象
     */
    private OutboundOrderDetailsVO convertDetailToVO(OutboundOrderDetails detail,
                                                     Map<Long, Products> productMap,
                                                     Map<Long, MaterialLocations> materialLocationMap) {
        OutboundOrderDetailsVO vo = new OutboundOrderDetailsVO();

        // 1. 复制明细基本信息
        BeanUtil.copyProperties(detail, vo);

        // 2. 从Map中获取商品信息
        if (detail.getProductId() != null) {
            Products product = productMap.get(detail.getProductId());
            if (product != null) {
                vo.setProductCode(product.getProductCode());
                vo.setProductName(product.getProductName());
                vo.setSpecification(product.getSpecification());
                vo.setBrand(product.getBrand());
            }
        }

        // 3. 从Map中获取物料位信息
        if (detail.getMaterialLocationId() != null) {
            MaterialLocations materialLocation = materialLocationMap.get(detail.getMaterialLocationId());
            if (materialLocation != null) {
                vo.setLocationCode(materialLocation.getLocationCode());
                vo.setRowNo(materialLocation.getRowNo());
                vo.setSectionNo(materialLocation.getSectionNo());
            }
        }

        return vo;
    }
}
