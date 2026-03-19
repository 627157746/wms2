package com.zhb.wms2.module.io.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhb.wms2.common.constant.IoBizTypeConstant;
import com.zhb.wms2.common.exception.BaseException;
import com.zhb.wms2.module.base.model.dto.BaseDictMapDTO;
import com.zhb.wms2.module.base.model.entity.Customer;
import com.zhb.wms2.module.base.model.entity.Deliveryman;
import com.zhb.wms2.module.base.model.entity.IoType;
import com.zhb.wms2.module.base.model.entity.ProductLocation;
import com.zhb.wms2.module.base.service.BaseDictMapService;
import com.zhb.wms2.module.inventory.model.entity.Inventory;
import com.zhb.wms2.module.inventory.model.entity.InventoryDetail;
import com.zhb.wms2.module.inventory.model.query.InventoryIoDetailQuery;
import com.zhb.wms2.module.inventory.model.vo.InventoryIoDetailVO;
import com.zhb.wms2.module.inventory.service.InventoryDetailService;
import com.zhb.wms2.module.inventory.service.InventoryService;
import com.zhb.wms2.module.io.mapper.IoApplyMapper;
import com.zhb.wms2.module.io.mapper.IoOrderDetailMapper;
import com.zhb.wms2.module.io.mapper.IoOrderMapper;
import com.zhb.wms2.module.io.model.dto.IoOrderCreateDTO;
import com.zhb.wms2.module.io.model.dto.IoOrderDetailDTO;
import com.zhb.wms2.module.io.model.dto.IoOrderGenerateDTO;
import com.zhb.wms2.module.io.model.dto.IoOrderUpdateDTO;
import com.zhb.wms2.module.io.model.entity.IoApply;
import com.zhb.wms2.module.io.model.entity.IoApplyDetail;
import com.zhb.wms2.module.io.model.entity.IoOrder;
import com.zhb.wms2.module.io.model.entity.IoOrderDetail;
import com.zhb.wms2.module.io.model.query.IoOrderQuery;
import com.zhb.wms2.module.io.model.vo.IoOrderPageVO;
import com.zhb.wms2.module.io.service.IoApplyDetailService;
import com.zhb.wms2.module.io.service.IoOrderDetailService;
import com.zhb.wms2.module.io.service.IoOrderService;
import com.zhb.wms2.module.product.mapper.ProductMapper;
import com.zhb.wms2.module.product.model.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IoOrderServiceImpl extends ServiceImpl<IoOrderMapper, IoOrder> implements IoOrderService {

    private static final int CUSTOMER_SCOPE_OUTBOUND = 1;
    private static final int DELIVERYMAN_SCOPE_OUTBOUND = 1;
    private static final int DELIVERYMAN_SCOPE_INBOUND = 2;
    private static final int COMMON_SCOPE = 0;
    private static final Long NO_LOCATION_ID = 0L;
    private static final String INBOUND_ORDER_PREFIX = "RK";
    private static final String OUTBOUND_ORDER_PREFIX = "CK";
    private static final int ORDER_NO_DIGIT_LENGTH = 6;

    private final IoApplyMapper ioApplyMapper;
    private final ProductMapper productMapper;
    private final InventoryService inventoryService;
    private final InventoryDetailService inventoryDetailService;
    private final IoApplyDetailService ioApplyDetailService;
    private final IoOrderDetailMapper ioOrderDetailMapper;
    private final IoOrderDetailService ioOrderDetailService;
    private final BaseDictMapService baseDictMapService;

    @Override
    public IPage<? extends IoOrder> pageQuery(IoOrderQuery query) {
        IPage<IoOrder> page = page(new Page<>(query.getCurrent(), query.getSize()), buildWrapper(query));
        List<IoOrder> recordList = page.getRecords();
        if (recordList.isEmpty()) {
            page.setRecords(List.of());
            return page;
        }

        BaseDictMapDTO dictMap = baseDictMapService.getBaseDictMap();
        Map<Long, Deliveryman> deliverymanMap = dictMap.getDeliverymanMap() == null
                ? Map.of() : dictMap.getDeliverymanMap();
        Map<Long, Customer> customerMap = dictMap.getCustomerMap() == null
                ? Map.of() : dictMap.getCustomerMap();
        Map<Long, IoType> ioTypeMap = dictMap.getIoTypeMap() == null
                ? Map.of() : dictMap.getIoTypeMap();
        Set<Long> applyIds = recordList.stream()
                .map(IoOrder::getApplyId)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        Map<Long, String> applyNoMap = applyIds.isEmpty()
                ? Collections.emptyMap()
                : ioApplyMapper.selectByIds(applyIds).stream()
                        .collect(Collectors.toMap(IoApply::getId, IoApply::getApplyNo,
                                (left, right) -> left, LinkedHashMap::new));
        return page.convert(ioOrder -> buildPageVO(ioOrder, deliverymanMap, customerMap, ioTypeMap, applyNoMap));
    }

    @Override
    public IPage<InventoryIoDetailVO> pageDetailByProductId(InventoryIoDetailQuery query) {
        Long productId = query.getProductId();
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new BaseException("商品不存在");
        }

        IPage<IoOrderDetail> detailPage = ioOrderDetailService.page(
                new Page<>(query.getCurrent(), query.getSize()),
                new LambdaQueryWrapper<IoOrderDetail>()
                        .eq(IoOrderDetail::getProductId, productId)
                        .orderByDesc(IoOrderDetail::getId));
        List<IoOrderDetail> detailList = detailPage.getRecords();
        if (detailList == null || detailList.isEmpty()) {
            return detailPage.convert(detail -> new InventoryIoDetailVO());
        }

        Set<Long> orderIds = detailList.stream()
                .map(IoOrderDetail::getOrderId)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        Map<Long, IoOrder> orderMap = orderIds.isEmpty() ? Map.of()
                : listByIds(orderIds).stream()
                        .collect(Collectors.toMap(IoOrder::getId, Function.identity(),
                                (left, right) -> left, LinkedHashMap::new));
        BaseDictMapDTO dictMap = baseDictMapService.getBaseDictMap();
        Map<Long, Deliveryman> deliverymanMap = dictMap.getDeliverymanMap() == null
                ? Map.of() : dictMap.getDeliverymanMap();
        Map<Long, Customer> customerMap = dictMap.getCustomerMap() == null
                ? Map.of() : dictMap.getCustomerMap();
        IoOrderDetail firstRow = detailList.getLast();
        long detailDeltaSum = Optional.ofNullable(
                ioOrderDetailMapper.sumDeltaToDetailIdByProductId(productId, firstRow.getId()))
                .orElse(0L);
        long initialStockQty = product.getInitialStock() == null ? 0L : product.getInitialStock();
        long earliestStockQty = initialStockQty + detailDeltaSum;
        Map<Long, Long> stockQtyMap = new HashMap<>(detailList.size());
        for (int i = detailList.size() - 1; i >= 0; i--) {
            IoOrderDetail detail = detailList.get(i);
            IoOrder ioOrder = orderMap.get(detail.getOrderId());
            if (ioOrder == null) {
                continue;
            }
            stockQtyMap.put(detail.getId(), earliestStockQty);
            long delta = Objects.equals(ioOrder.getOrderType(), IoBizTypeConstant.INBOUND)
                    ? (detail.getQty() == null ? 0L : detail.getQty())
                    : -(detail.getQty() == null ? 0L : detail.getQty());
            earliestStockQty += delta;
        }
        return detailPage.convert(detail -> {
            IoOrder ioOrder = orderMap.get(detail.getOrderId());
            if (ioOrder == null) {
                return new InventoryIoDetailVO();
            }
            return buildInventoryIoDetailVO(ioOrder, detail, product,
                    deliverymanMap.get(ioOrder.getDeliverymanId()),
                    customerMap.get(ioOrder.getCustomerId()),
                    stockQtyMap.getOrDefault(detail.getId(), 0L));
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long generateOrderByApply(Long applyId, IoOrderGenerateDTO dto) {
        IoApply ioApply = ioApplyMapper.selectById(applyId);
        if (ioApply == null) {
            throw new BaseException("出入库申请不存在");
        }
        validateApplyCanGenerate(ioApply);
        validateHeaderRefs(ioApply.getOrderType(), ioApply.getDeliverymanId(), ioApply.getCustomerId(),
                ioApply.getIoTypeId());
        List<IoApplyDetail> applyDetailList = ioApplyDetailService.list(
                new LambdaQueryWrapper<IoApplyDetail>().eq(IoApplyDetail::getApplyId, applyId));
        if (applyDetailList.isEmpty()) {
            throw new BaseException(buildBizLabel(ioApply.getOrderType()) + "申请明细不能为空");
        }

        List<IoOrderDetailDTO> detailDTOList = dto.getDetailList();
        validateDetailRefs(detailDTOList);
        validateApplyGenerateDetails(ioApply.getOrderType(), applyDetailList, detailDTOList);

        IoOrder ioOrder = createOrder(ioApply.getOrderType(), ioApply.getId(), dto.getBizDate(),
                ioApply.getDeliverymanId(), ioApply.getCustomerId(), ioApply.getIoTypeId(), dto.getRemark(),
                detailDTOList);

        ioApply.setIoStatus(1);
        ioApplyMapper.updateById(ioApply);
        return ioOrder.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveOrder(IoOrderCreateDTO dto) {
        validateHeaderRefs(dto.getOrderType(), dto.getDeliverymanId(), dto.getCustomerId(), dto.getIoTypeId());
        List<IoOrderDetailDTO> detailDTOList = dto.getDetailList();
        validateDetailRefs(detailDTOList);
        return createOrder(dto.getOrderType(), null, dto.getBizDate(), dto.getDeliverymanId(),
                Objects.equals(dto.getOrderType(), IoBizTypeConstant.OUTBOUND) ? dto.getCustomerId() : null,
                dto.getIoTypeId(), dto.getRemark(), detailDTOList).getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateOrder(IoOrderUpdateDTO dto) {
        IoOrder ioOrder = getById(dto.getId());
        if (ioOrder == null) {
            throw new BaseException("出入库单不存在");
        }

        validateHeaderRefs(ioOrder.getOrderType(), dto.getDeliverymanId(), dto.getCustomerId(), dto.getIoTypeId());
        List<IoOrderDetailDTO> newDetailDTOList = dto.getDetailList();
        validateDetailRefs(newDetailDTOList);

        List<IoOrderDetail> oldDetailList = ioOrderDetailService.list(
                new LambdaQueryWrapper<IoOrderDetail>().eq(IoOrderDetail::getOrderId, dto.getId()));
        rollbackOrderInventory(ioOrder, oldDetailList, "修改");

        ioOrder.setBizDate(dto.getBizDate());
        ioOrder.setDeliverymanId(dto.getDeliverymanId());
        ioOrder.setIoTypeId(dto.getIoTypeId());
        ioOrder.setRemark(dto.getRemark());
        ioOrder.setCustomerId(Objects.equals(ioOrder.getOrderType(), IoBizTypeConstant.OUTBOUND) ? dto.getCustomerId() : null);
        if (Objects.equals(ioOrder.getOrderType(), IoBizTypeConstant.INBOUND)) {
            ioOrder.setPickingStatus(0);
        }
        if (!updateById(ioOrder)) {
            throw new BaseException("出入库单不存在");
        }

        ioOrderDetailService.remove(new LambdaQueryWrapper<IoOrderDetail>().eq(IoOrderDetail::getOrderId, dto.getId()));
        List<IoOrderDetail> newDetailList = newDetailDTOList.stream()
                .map(detailDTO -> buildOrderDetail(ioOrder.getId(), ioOrder.getOrderType(), detailDTO))
                .toList();
        if (!ioOrderDetailService.saveBatch(newDetailList)) {
            throw new BaseException(buildBizLabel(ioOrder.getOrderType()) + "单明细修改失败");
        }
        applyInventoryChange(ioOrder.getOrderType(), newDetailDTOList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeByIdChecked(Long id) {
        IoOrder ioOrder = getById(id);
        if (ioOrder == null) {
            throw new BaseException("出入库单不存在");
        }
        List<IoOrderDetail> detailList = ioOrderDetailService.list(
                new LambdaQueryWrapper<IoOrderDetail>().eq(IoOrderDetail::getOrderId, id));
        rollbackOrderInventory(ioOrder, detailList, "删除");
        ioOrderDetailService.remove(new LambdaQueryWrapper<IoOrderDetail>().eq(IoOrderDetail::getOrderId, id));
        if (!removeById(id)) {
            throw new BaseException("出入库单不存在");
        }
        restoreApplyStatus(ioOrder.getApplyId());
    }

    private IoOrder createOrder(Integer orderType, Long applyId, java.time.LocalDate bizDate, Long deliverymanId,
                                Long customerId, Long ioTypeId, String remark, List<IoOrderDetailDTO> detailDTOList) {
        IoOrder ioOrder = new IoOrder();
        ioOrder.setOrderNo(generateOrderNo(orderType));
        ioOrder.setOrderType(orderType);
        ioOrder.setApplyId(applyId);
        ioOrder.setBizDate(bizDate);
        ioOrder.setDeliverymanId(deliverymanId);
        ioOrder.setCustomerId(customerId);
        ioOrder.setIoTypeId(ioTypeId);
        ioOrder.setRemark(remark);
        ioOrder.setPickingStatus(0);
        saveOrderAndAdjustInventory(ioOrder, detailDTOList);
        return ioOrder;
    }

    private void saveOrderAndAdjustInventory(IoOrder ioOrder, List<IoOrderDetailDTO> detailDTOList) {
        if (!save(ioOrder)) {
            throw new BaseException(buildBizLabel(ioOrder.getOrderType()) + "单新增失败");
        }

        List<IoOrderDetail> detailList = detailDTOList.stream()
                .map(detailDTO -> buildOrderDetail(ioOrder.getId(), ioOrder.getOrderType(), detailDTO))
                .toList();
        if (!ioOrderDetailService.saveBatch(detailList)) {
            throw new BaseException(buildBizLabel(ioOrder.getOrderType()) + "单明细新增失败");
        }
        applyInventoryChange(ioOrder.getOrderType(), detailDTOList);
    }

    private void validateApplyCanGenerate(IoApply ioApply) {
        if (!Objects.equals(ioApply.getApproveStatus(), 1)) {
            throw new BaseException(buildBizLabel(ioApply.getOrderType()) + "申请未审批，无法生成"
                    + buildBizLabel(ioApply.getOrderType()) + "单");
        }
        if (Objects.equals(ioApply.getIoStatus(), 1)) {
            throw new BaseException(buildBizLabel(ioApply.getOrderType()) + "申请已生成"
                    + buildBizLabel(ioApply.getOrderType()) + "单");
        }
        long ioOrderCount = count(new LambdaQueryWrapper<IoOrder>().eq(IoOrder::getApplyId, ioApply.getId()));
        if (ioOrderCount > 0) {
            throw new BaseException(buildBizLabel(ioApply.getOrderType()) + "申请已生成"
                    + buildBizLabel(ioApply.getOrderType()) + "单");
        }
    }

    private void validateHeaderRefs(Integer orderType, Long deliverymanId, Long customerId, Long ioTypeId) {
        validateDeliveryman(orderType, deliverymanId);
        validateIoType(orderType, ioTypeId);
        if (Objects.equals(orderType, IoBizTypeConstant.OUTBOUND)) {
            if (customerId == null) {
                throw new BaseException("出库单客户不能为空");
            }
            validateCustomer(customerId);
        }
    }

    private void validateDeliveryman(Integer orderType, Long deliverymanId) {
        BaseDictMapDTO dictMap = baseDictMapService.getBaseDictMap();
        Deliveryman deliveryman = dictMap.getDeliverymanMap() == null
                ? null
                : dictMap.getDeliverymanMap().get(deliverymanId);
        if (deliveryman == null) {
            throw new BaseException("送货员不存在");
        }
        Integer expectedScope = Objects.equals(orderType, IoBizTypeConstant.INBOUND)
                ? DELIVERYMAN_SCOPE_INBOUND
                : DELIVERYMAN_SCOPE_OUTBOUND;
        if (!Objects.equals(deliveryman.getScope(), COMMON_SCOPE)
                && !Objects.equals(deliveryman.getScope(), expectedScope)) {
            throw new BaseException("送货员不适用于当前单据类型");
        }
    }

    private void validateCustomer(Long customerId) {
        BaseDictMapDTO dictMap = baseDictMapService.getBaseDictMap();
        Customer customer = dictMap.getCustomerMap() == null
                ? null
                : dictMap.getCustomerMap().get(customerId);
        if (customer == null) {
            throw new BaseException("客户不存在");
        }
        if (!Objects.equals(customer.getScope(), COMMON_SCOPE)
                && !Objects.equals(customer.getScope(), CUSTOMER_SCOPE_OUTBOUND)) {
            throw new BaseException("客户不适用于出库单");
        }
    }

    private void validateIoType(Integer orderType, Long ioTypeId) {
        BaseDictMapDTO dictMap = baseDictMapService.getBaseDictMap();
        IoType ioType = dictMap.getIoTypeMap() == null
                ? null
                : dictMap.getIoTypeMap().get(ioTypeId);
        if (ioType == null) {
            throw new BaseException("出入库类型不存在");
        }
        if (!Objects.equals(ioType.getBizType(), orderType)) {
            throw new BaseException("出入库类型与单据类型不匹配");
        }
    }

    private void validateDetailRefs(List<IoOrderDetailDTO> detailDTOList) {
        BaseDictMapDTO dictMap = baseDictMapService.getBaseDictMap();
        Map<Long, ProductLocation> productLocationMap = dictMap.getProductLocationMap() == null
                ? Map.of()
                : dictMap.getProductLocationMap();
        Set<Long> productIds = new LinkedHashSet<>();
        for (IoOrderDetailDTO detail : detailDTOList) {
            productIds.add(detail.getProductId());
            if (!productLocationMap.containsKey(detail.getLocationId())) {
                throw new BaseException("商品货位不存在");
            }
        }
        List<Product> productList = productMapper.selectBatchIds(productIds);
        if (productList.size() != productIds.size()) {
            throw new BaseException("出入库明细中存在不存在的商品");
        }
    }

    private void validateApplyGenerateDetails(Integer orderType, List<IoApplyDetail> applyDetailList,
                                              List<IoOrderDetailDTO> detailDTOList) {
        Map<Long, Long> applyQtyMap = applyDetailList.stream()
                .collect(Collectors.groupingBy(IoApplyDetail::getProductId, LinkedHashMap::new,
                        Collectors.summingLong(detail -> detail.getQty() == null ? 0L : detail.getQty())));
        Map<Long, Long> orderQtyMap = detailDTOList.stream()
                .collect(Collectors.groupingBy(IoOrderDetailDTO::getProductId, LinkedHashMap::new,
                        Collectors.summingLong(detail -> detail.getQty() == null ? 0L : detail.getQty())));
        if (!applyQtyMap.equals(orderQtyMap)) {
            throw new BaseException(buildBizLabel(orderType) + "单明细数量与"
                    + buildBizLabel(orderType) + "申请不一致");
        }
    }

    private IoOrderDetail buildOrderDetail(Long orderId, Integer orderType, IoOrderDetailDTO detailDTO) {
        IoOrderDetail detail = new IoOrderDetail();
        detail.setOrderId(orderId);
        detail.setOrderType(orderType);
        detail.setProductId(detailDTO.getProductId());
        detail.setQty(detailDTO.getQty());
        detail.setLocationId(detailDTO.getLocationId());
        detail.setPickedQty(0L);
        return detail;
    }

    private void applyInventoryChange(Integer orderType, List<IoOrderDetailDTO> detailDTOList) {
        Map<Long, Map<Long, InventoryDetail>> detailGroupMap = loadInventoryDetailGroup(detailDTOList);
        for (IoOrderDetailDTO detailDTO : detailDTOList) {
            Map<Long, InventoryDetail> productDetailMap = detailGroupMap.computeIfAbsent(
                    detailDTO.getProductId(), key -> new LinkedHashMap<>());
            long delta = Objects.equals(orderType, IoBizTypeConstant.INBOUND) ? detailDTO.getQty() : -detailDTO.getQty();
            changeDetailQty(productDetailMap, detailDTO.getProductId(), detailDTO.getLocationId(), delta, orderType);
        }
        for (Map.Entry<Long, Map<Long, InventoryDetail>> entry : detailGroupMap.entrySet()) {
            syncInventoryMain(entry.getKey(), entry.getValue());
        }
    }

    private void rollbackOrderInventory(IoOrder ioOrder, List<IoOrderDetail> oldDetailList, String actionName) {
        List<IoOrderDetailDTO> rollbackDetailList = oldDetailList.stream().map(this::toDetailDTO).toList();
        Map<Long, Map<Long, InventoryDetail>> detailGroupMap = loadInventoryDetailGroup(rollbackDetailList);
        try {
            for (IoOrderDetailDTO detailDTO : rollbackDetailList) {
                Map<Long, InventoryDetail> productDetailMap = detailGroupMap.computeIfAbsent(
                        detailDTO.getProductId(), key -> new LinkedHashMap<>());
                long rollbackDelta = Objects.equals(ioOrder.getOrderType(), IoBizTypeConstant.INBOUND)
                        ? -detailDTO.getQty()
                        : detailDTO.getQty();
                changeDetailQty(productDetailMap, detailDTO.getProductId(), detailDTO.getLocationId(),
                        rollbackDelta, ioOrder.getOrderType());
            }
            for (Map.Entry<Long, Map<Long, InventoryDetail>> entry : detailGroupMap.entrySet()) {
                syncInventoryMain(entry.getKey(), entry.getValue());
            }
        } catch (BaseException ex) {
            if (Objects.equals(ioOrder.getOrderType(), IoBizTypeConstant.INBOUND)) {
                throw new BaseException(actionName + "入库单失败，该入库库存已被后续业务占用");
            }
            throw ex;
        }
    }

    private IoOrderDetailDTO toDetailDTO(IoOrderDetail detail) {
        IoOrderDetailDTO dto = new IoOrderDetailDTO();
        dto.setProductId(detail.getProductId());
        dto.setQty(detail.getQty());
        dto.setLocationId(detail.getLocationId() == null ? NO_LOCATION_ID : detail.getLocationId());
        return dto;
    }

    private Map<Long, Map<Long, InventoryDetail>> loadInventoryDetailGroup(List<IoOrderDetailDTO> detailDTOList) {
        Set<Long> productIds = detailDTOList.stream().map(IoOrderDetailDTO::getProductId).collect(Collectors.toSet());
        if (productIds.isEmpty()) {
            return new LinkedHashMap<>();
        }
        return inventoryDetailService.list(new LambdaQueryWrapper<InventoryDetail>().in(InventoryDetail::getProductId, productIds))
                .stream()
                .collect(Collectors.groupingBy(InventoryDetail::getProductId, LinkedHashMap::new,
                        Collectors.toMap(InventoryDetail::getLocationId, detail -> detail,
                                (left, right) -> left, LinkedHashMap::new)));
    }

    private void syncInventoryMain(Long productId, Map<Long, InventoryDetail> detailMap) {
        Inventory inventory = inventoryService.getOne(
                new LambdaQueryWrapper<Inventory>().eq(Inventory::getProductId, productId), false);
        if (detailMap == null || detailMap.isEmpty()) {
            if (inventory != null) {
                inventoryService.removeById(inventory.getId());
            }
            return;
        }

        Long totalQty = detailMap.values().stream().map(InventoryDetail::getQty).reduce(0L, Long::sum);
        String locationIdsStr = detailMap.keySet().stream()
                .filter(locationId -> !Objects.equals(locationId, NO_LOCATION_ID))
                .sorted(Comparator.naturalOrder())
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        if (locationIdsStr.isBlank()) {
            locationIdsStr = null;
        }
        if (inventory == null) {
            Inventory newInventory = new Inventory();
            newInventory.setProductId(productId);
            newInventory.setTotalQty(totalQty);
            newInventory.setLocationIdsStr(locationIdsStr);
            inventoryService.save(newInventory);
            return;
        }
        inventory.setTotalQty(totalQty);
        inventory.setLocationIdsStr(locationIdsStr);
        inventoryService.updateById(inventory);
    }

    private void changeDetailQty(Map<Long, InventoryDetail> detailMap, Long productId, Long locationId, Long delta,
                                 Integer orderType) {
        if (delta == 0) {
            return;
        }
        InventoryDetail detail = detailMap.get(locationId);
        long currentQty = detail == null || detail.getQty() == null ? 0L : detail.getQty();
        long targetQty = currentQty + delta;
        if (targetQty < 0) {
            throw new BaseException(buildBizLabel(orderType) + "失败，商品库存不足");
        }
        if (targetQty == 0) {
            inventoryDetailService.removeById(detail.getId());
            detailMap.remove(locationId);
            return;
        }
        if (detail == null) {
            InventoryDetail inventoryDetail = new InventoryDetail();
            inventoryDetail.setProductId(productId);
            inventoryDetail.setLocationId(locationId);
            inventoryDetail.setQty(targetQty);
            inventoryDetailService.save(inventoryDetail);
            detailMap.put(locationId, inventoryDetail);
            return;
        }
        detail.setQty(targetQty);
        inventoryDetailService.updateById(detail);
    }

    private String generateOrderNo(Integer orderType) {
        String prefix = Objects.equals(orderType, IoBizTypeConstant.INBOUND)
                ? INBOUND_ORDER_PREFIX
                : OUTBOUND_ORDER_PREFIX;
        IoOrder lastOrder = getOne(new LambdaQueryWrapper<IoOrder>()
                .eq(IoOrder::getOrderType, orderType)
                .likeRight(IoOrder::getOrderNo, prefix)
                .orderByDesc(IoOrder::getId)
                .last("limit 1"), false);
        long nextNumber = 1L;
        if (lastOrder != null) {
            nextNumber = parseOrderNoNumber(lastOrder.getOrderNo(), prefix) + 1;
        }
        return prefix + String.format("%0" + ORDER_NO_DIGIT_LENGTH + "d", nextNumber);
    }

    private long parseOrderNoNumber(String orderNo, String prefix) {
        if (orderNo == null || !orderNo.startsWith(prefix) || orderNo.length() <= prefix.length()) {
            throw new BaseException("历史出入库单号格式不正确");
        }
        String numberPart = orderNo.substring(prefix.length());
        try {
            return Long.parseLong(numberPart);
        } catch (NumberFormatException ex) {
            throw new BaseException("历史出入库单号格式不正确");
        }
    }

    private String buildBizLabel(Integer orderType) {
        return Objects.equals(orderType, IoBizTypeConstant.INBOUND) ? "入库" : "出库";
    }

    private InventoryIoDetailVO buildInventoryIoDetailVO(IoOrder ioOrder, IoOrderDetail detail, Product product,
                                                         Deliveryman deliveryman,
                                                         Customer customer, Long currentStockQty) {
        InventoryIoDetailVO vo = new InventoryIoDetailVO();
        vo.setOrderNo(ioOrder.getOrderNo());
        vo.setOrderId(ioOrder.getId());
        vo.setOrderType(ioOrder.getOrderType());
        vo.setOrderTypeName(buildBizLabel(ioOrder.getOrderType()));
        vo.setBizDate(ioOrder.getBizDate());
        vo.setQty(detail.getQty());
        vo.setDeliveryman(deliveryman);
        vo.setCustomer(customer);
        vo.setProduct(product);
        vo.setCurrentStockQty(currentStockQty == null ? 0L : currentStockQty);
        return vo;
    }

    private LambdaQueryWrapper<IoOrder> buildWrapper(IoOrderQuery query) {
        return new LambdaQueryWrapper<IoOrder>()
                .like(StrUtil.isNotBlank(query.getOrderNo()), IoOrder::getOrderNo, query.getOrderNo())
                .eq(query.getOrderType() != null, IoOrder::getOrderType, query.getOrderType())
                .ge(query.getBizDateStart() != null, IoOrder::getBizDate, query.getBizDateStart())
                .le(query.getBizDateEnd() != null, IoOrder::getBizDate, query.getBizDateEnd())
                .eq(query.getDeliverymanId() != null, IoOrder::getDeliverymanId, query.getDeliverymanId())
                .eq(query.getCustomerId() != null, IoOrder::getCustomerId, query.getCustomerId())
                .eq(query.getIoTypeId() != null, IoOrder::getIoTypeId, query.getIoTypeId())
                .eq(query.getApplyId() != null, IoOrder::getApplyId, query.getApplyId())
                .orderByDesc(IoOrder::getId);
    }

    private IoOrderPageVO buildPageVO(IoOrder ioOrder, Map<Long, Deliveryman> deliverymanMap,
                                      Map<Long, Customer> customerMap, Map<Long, IoType> ioTypeMap,
                                      Map<Long, String> applyNoMap) {
        IoOrderPageVO vo = new IoOrderPageVO();
        vo.setId(ioOrder.getId());
        vo.setOrderNo(ioOrder.getOrderNo());
        vo.setOrderType(ioOrder.getOrderType());
        vo.setApplyId(ioOrder.getApplyId());
        vo.setBizDate(ioOrder.getBizDate());
        vo.setDeliverymanId(ioOrder.getDeliverymanId());
        vo.setCustomerId(ioOrder.getCustomerId());
        vo.setIoTypeId(ioOrder.getIoTypeId());
        vo.setRemark(ioOrder.getRemark());
        vo.setPickingStatus(ioOrder.getPickingStatus());
        vo.setCreateTime(ioOrder.getCreateTime());
        vo.setUpdateTime(ioOrder.getUpdateTime());
        vo.setCreateBy(ioOrder.getCreateBy());
        vo.setUpdateBy(ioOrder.getUpdateBy());
        vo.setOrderTypeName(buildBizLabel(ioOrder.getOrderType()));
        vo.setApplyNo(ioOrder.getApplyId() == null ? null : applyNoMap.get(ioOrder.getApplyId()));
        Deliveryman deliveryman = deliverymanMap.get(ioOrder.getDeliverymanId());
        vo.setDeliveryman(deliveryman);
        Customer customer = customerMap.get(ioOrder.getCustomerId());
        vo.setCustomer(customer);
        IoType ioType = ioTypeMap.get(ioOrder.getIoTypeId());
        vo.setIoTypeName(ioType == null ? null : ioType.getName());
        vo.setPickingStatusName(buildPickingStatusName(ioOrder));
        return vo;
    }

    private String buildPickingStatusName(IoOrder ioOrder) {
        if (Objects.equals(ioOrder.getOrderType(), IoBizTypeConstant.INBOUND)) {
            return null;
        }
        return Objects.equals(ioOrder.getPickingStatus(), 1) ? "已拣货" : "未拣货";
    }

    private void restoreApplyStatus(Long applyId) {
        if (applyId == null) {
            return;
        }
        IoApply ioApply = ioApplyMapper.selectById(applyId);
        if (ioApply == null) {
            return;
        }
        ioApply.setIoStatus(0);
        ioApplyMapper.updateById(ioApply);
    }
}
