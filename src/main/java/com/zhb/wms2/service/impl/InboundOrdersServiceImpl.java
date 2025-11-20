package com.zhb.wms2.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhb.wms2.common.exception.BaseException;
import com.zhb.wms2.mapper.InboundOrdersMapper;
import com.zhb.wms2.model.*;
import com.zhb.wms2.model.dto.InboundOrderCreateDTO;
import com.zhb.wms2.model.dto.InboundOrderDetailItemDTO;
import com.zhb.wms2.model.dto.InboundOrderUpdateDTO;
import com.zhb.wms2.model.dto.InboundOrdersQuery;
import com.zhb.wms2.model.vo.InboundOrderDetailsVO;
import com.zhb.wms2.model.vo.InboundOrdersVO;
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
 * @Date 2025/11/19 16:18
 */
@Service
@RequiredArgsConstructor
public class InboundOrdersServiceImpl extends ServiceImpl<InboundOrdersMapper, InboundOrders> implements InboundOrdersService {

    private final InboundOrderDetailsService inboundOrderDetailsService;

    private final StoragePositionsService storagePositionsService;

    private final ProductsService productsService;

    private final MaterialLocationsService materialLocationsService;

    private final WarehouseTypesService warehouseTypesService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createInboundOrder(InboundOrderCreateDTO dto) {
        // 1. 创建入库单主表
        InboundOrders inboundOrder = new InboundOrders();

        // 生成入库单号
        String inboundCode = generateInboundCode();
        inboundOrder.setInboundCode(inboundCode);
        inboundOrder.setInboundTypeId(dto.getInboundTypeId());
        inboundOrder.setInboundDate(dto.getInboundDate());
        inboundOrder.setOperator(dto.getOperator());
        inboundOrder.setRemarks(dto.getRemarks());

        // 计算总数量
        int totalQuantity = dto.getDetails().stream()
                .mapToInt(InboundOrderDetailItemDTO::getQuantity)
                .sum();
        inboundOrder.setTotalQuantity(totalQuantity);

        // 保存入库单主表
        if (!save(inboundOrder)) {
            throw new BaseException("创建入库单失败");
        }

        // 2. 处理入库明细和库存
        List<InboundOrderDetails> detailsList = new ArrayList<>();
        for (InboundOrderDetailItemDTO detailDTO : dto.getDetails()) {
            // 2.1 更新货位库存
            updateStoragePositionStock(detailDTO.getMaterialLocationId(), detailDTO.getQuantity());

            // 2.2 创建入库明细
            InboundOrderDetails detail = new InboundOrderDetails();
            detail.setInboundId(inboundOrder.getId());
            detail.setProductId(detailDTO.getProductId());
            detail.setMaterialLocationId(detailDTO.getMaterialLocationId());
            detail.setQuantity(detailDTO.getQuantity());
            detail.setRemarks(detailDTO.getRemarks());

            detailsList.add(detail);
        }

        // 批量保存明细
        if (!inboundOrderDetailsService.saveBatch(detailsList)) {
            throw new BaseException("创建入库单明细失败");
        }

        return inboundOrder.getId();
    }

    /**
     * 更新货位库存数量（增加库存）
     * 根据 materialLocationId 查询或创建货位，并增加库存
     *
     * @param materialLocationId 物料位ID
     * @param quantity 入库数量
     */
    private void updateStoragePositionStock(Long materialLocationId, Integer quantity) {
        // 1. 根据 materialLocationId 查询货位记录
        LambdaQueryWrapper<StoragePositions> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StoragePositions::getMaterialLocationId, materialLocationId);

        StoragePositions storagePosition = storagePositionsService.getOne(wrapper);

        if (storagePosition == null) {
            // 2. 货位不存在，创建新记录
            storagePosition = new StoragePositions();
            storagePosition.setMaterialLocationId(materialLocationId);
            storagePosition.setCurrentQuantity(quantity);

            if (!storagePositionsService.save(storagePosition)) {
                throw new BaseException("创建货位库存失败");
            }
        } else {
            // 3. 货位已存在，增加库存数量
            Integer currentQuantity = storagePosition.getCurrentQuantity();
            if (currentQuantity == null) {
                currentQuantity = 0;
            }
            storagePosition.setCurrentQuantity(currentQuantity + quantity);

            if (!storagePositionsService.updateById(storagePosition)) {
                throw new BaseException("更新货位库存失败");
            }
        }
    }

    /**
     * 回滚货位库存数量（减少库存）
     * 用于修改入库单时，先回滚原明细的库存
     *
     * @param materialLocationId 物料位ID
     * @param quantity 需要减少的数量
     */
    private void rollbackStoragePositionStock(Long materialLocationId, Integer quantity) {
        // 1. 根据 materialLocationId 查询货位记录
        LambdaQueryWrapper<StoragePositions> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StoragePositions::getMaterialLocationId, materialLocationId);

        StoragePositions storagePosition = storagePositionsService.getOne(wrapper);

        if (storagePosition == null) {
            throw new BaseException("货位库存记录不存在，无法回滚");
        }

        // 2. 减少库存数量
        Integer currentQuantity = storagePosition.getCurrentQuantity();
        if (currentQuantity == null) {
            currentQuantity = 0;
        }

        Integer newQuantity = currentQuantity - quantity;

        if (newQuantity < 0) {
            throw new BaseException("库存不足，无法回滚");
        }

        storagePosition.setCurrentQuantity(newQuantity);

        if (!storagePositionsService.updateById(storagePosition)) {
            throw new BaseException("回滚货位库存失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateInbound(InboundOrderUpdateDTO dto) {
        // 1. 查询原明细，用于回滚库存
        LambdaQueryWrapper<InboundOrderDetails> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(InboundOrderDetails::getInboundId, dto.getId());
        List<InboundOrderDetails> oldDetails = inboundOrderDetailsService.list(queryWrapper);

        // 2. 回滚原明细的库存（减少库存）
        for (InboundOrderDetails oldDetail : oldDetails) {
            rollbackStoragePositionStock(oldDetail.getMaterialLocationId(), oldDetail.getQuantity());
        }

        // 3. 删除原明细
        inboundOrderDetailsService.remove(queryWrapper);

        // 4. 更新主表信息
        InboundOrders inboundOrder = new InboundOrders();
        inboundOrder.setId(dto.getId());
        inboundOrder.setInboundTypeId(dto.getInboundTypeId());
        inboundOrder.setInboundDate(dto.getInboundDate());
        inboundOrder.setOperator(dto.getOperator());
        inboundOrder.setRemarks(dto.getRemarks());

        // 重新计算总数量
        int totalQuantity = dto.getDetails().stream()
                .mapToInt(InboundOrderDetailItemDTO::getQuantity)
                .sum();
        inboundOrder.setTotalQuantity(totalQuantity);

        updateById(inboundOrder);

        // 5. 处理新明细和库存（与创建逻辑相同）
        List<InboundOrderDetails> newDetails = new ArrayList<>();
        for (InboundOrderDetailItemDTO detailDTO : dto.getDetails()) {
            // 5.1 更新货位库存
            updateStoragePositionStock(detailDTO.getMaterialLocationId(), detailDTO.getQuantity());

            // 5.2 创建入库明细
            InboundOrderDetails detail = new InboundOrderDetails();
            detail.setInboundId(dto.getId());
            detail.setProductId(detailDTO.getProductId());
            detail.setMaterialLocationId(detailDTO.getMaterialLocationId());
            detail.setQuantity(detailDTO.getQuantity());
            detail.setRemarks(detailDTO.getRemarks());

            newDetails.add(detail);
        }

        // 6. 批量保存新明细
        if (!inboundOrderDetailsService.saveBatch(newDetails)) {
            throw new BaseException("更新入库单明细失败");
        }
    }

    @Override
    public IPage<InboundOrdersVO> queryPage(InboundOrdersQuery query) {
        // 1. 构建查询条件
        LambdaQueryWrapper<InboundOrders> wrapper = buildQueryWrapper(query);

        // 2. 执行分页查询
        Page<InboundOrders> page = new Page<>(query.getCurrent(), query.getSize());
        IPage<InboundOrders> inboundOrdersPage = page(page, wrapper);

        List<InboundOrders> records = inboundOrdersPage.getRecords();
        if (records.isEmpty()) {
            return new Page<>(query.getCurrent(), query.getSize(), 0);
        }

        // 3. 批量查询所有明细
        List<Long> inboundIds = records.stream()
                .map(InboundOrders::getId)
                .collect(Collectors.toList());

        LambdaQueryWrapper<InboundOrderDetails> detailsWrapper = new LambdaQueryWrapper<>();
        detailsWrapper.in(InboundOrderDetails::getInboundId, inboundIds);
        List<InboundOrderDetails> allDetails = inboundOrderDetailsService.list(detailsWrapper);

        // 4. 按inboundId分组
        Map<Long, List<InboundOrderDetails>> detailsMap = allDetails.stream()
                .collect(Collectors.groupingBy(InboundOrderDetails::getInboundId));

        // 5. 批量查询商品和物料位信息
        List<Long> productIds = allDetails.stream()
                .map(InboundOrderDetails::getProductId)
                .distinct()
                .collect(Collectors.toList());

        List<Long> materialLocationIds = allDetails.stream()
                .map(InboundOrderDetails::getMaterialLocationId)
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

        // 6. 批量查询入库类型信息
        List<Long> inboundTypeIds = records.stream()
                .map(InboundOrders::getInboundTypeId)
                .filter(id -> id != null)
                .map(Long::valueOf)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, WarehouseTypes> warehouseTypeMap = new HashMap<>();
        if (!inboundTypeIds.isEmpty()) {
            List<WarehouseTypes> warehouseTypes = warehouseTypesService.listByIds(inboundTypeIds);
            warehouseTypeMap = warehouseTypes.stream()
                    .collect(Collectors.toMap(WarehouseTypes::getId, wt -> wt));
        }

        // 7. 转换为VO
        final Map<Long, List<InboundOrderDetails>> finalDetailsMap = detailsMap;
        final Map<Long, Products> finalProductMap = productMap;
        final Map<Long, MaterialLocations> finalMaterialLocationMap = materialLocationMap;
        final Map<Long, WarehouseTypes> finalWarehouseTypeMap = warehouseTypeMap;

        List<InboundOrdersVO> voList = records.stream()
                .map(order -> convertToVO(order, finalDetailsMap, finalProductMap, finalMaterialLocationMap, finalWarehouseTypeMap))
                .collect(Collectors.toList());

        // 8. 构建分页结果
        Page<InboundOrdersVO> voPage = new Page<>(query.getCurrent(), query.getSize(), inboundOrdersPage.getTotal());
        voPage.setRecords(voList);

        return voPage;
    }

    /**
     * 构建查询条件
     */
    private LambdaQueryWrapper<InboundOrders> buildQueryWrapper(InboundOrdersQuery query) {
        LambdaQueryWrapper<InboundOrders> wrapper = new LambdaQueryWrapper<>();

        // 精确查询
        wrapper.eq(query.getInboundTypeId() != null, InboundOrders::getInboundTypeId, query.getInboundTypeId());

        // 模糊查询
        wrapper.like(cn.hutool.core.util.StrUtil.isNotBlank(query.getInboundCode()),
                    InboundOrders::getInboundCode, query.getInboundCode())
               .like(cn.hutool.core.util.StrUtil.isNotBlank(query.getOperator()),
                    InboundOrders::getOperator, query.getOperator());

        // 时间范围查询
        wrapper.ge(query.getStartTime() != null, InboundOrders::getInboundDate, query.getStartTime())
               .le(query.getEndTime() != null, InboundOrders::getInboundDate, query.getEndTime());

        // 排序
        wrapper.orderByDesc(InboundOrders::getCreateTime);

        return wrapper;
    }

    /**
     * 生成入库单号
     */
    private String generateInboundCode() {
        // 简单的入库单号生成规则：INB + yyyyMMddHHmmss + 3位随机数
        String timePrefix = java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
                .format(java.time.LocalDateTime.now());
        int randomSuffix = (int) (Math.random() * 1000);
        return "INB" + timePrefix + String.format("%03d", randomSuffix);
    }

    /**
     * 转换为VO对象
     */
    private InboundOrdersVO convertToVO(InboundOrders inboundOrder,
                                        Map<Long, List<InboundOrderDetails>> detailsMap,
                                        Map<Long, Products> productMap,
                                        Map<Long, MaterialLocations> materialLocationMap,
                                        Map<Long, WarehouseTypes> warehouseTypeMap) {
        InboundOrdersVO vo = new InboundOrdersVO();

        // 1. 复制主单基本信息
        BeanUtil.copyProperties(inboundOrder, vo);

        // 2. 设置入库类型名称
        if (inboundOrder.getInboundTypeId() != null) {
            WarehouseTypes warehouseType = warehouseTypeMap.get(Long.valueOf(inboundOrder.getInboundTypeId()));
            if (warehouseType != null) {
                vo.setInboundTypeName(warehouseType.getTypeName());
            }
        }

        // 3. 从Map中获取明细列表
        List<InboundOrderDetails> detailsList = detailsMap.getOrDefault(inboundOrder.getId(), new ArrayList<>());

        // 4. 转换明细为VO
        List<InboundOrderDetailsVO> detailsVOList = detailsList.stream()
                .map(detail -> convertDetailToVO(detail, productMap, materialLocationMap))
                .collect(Collectors.toList());

        vo.setDetails(detailsVOList);

        return vo;
    }

    /**
     * 转换明细为VO对象
     */
    private InboundOrderDetailsVO convertDetailToVO(InboundOrderDetails detail,
                                                     Map<Long, Products> productMap,
                                                     Map<Long, MaterialLocations> materialLocationMap) {
        InboundOrderDetailsVO vo = new InboundOrderDetailsVO();

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
