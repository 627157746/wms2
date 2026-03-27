package com.zhb.wms2.module.io.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhb.wms2.common.enums.*;
import com.zhb.wms2.common.exception.BaseException;
import com.zhb.wms2.module.base.model.dto.BaseDictMapDTO;
import com.zhb.wms2.module.base.model.entity.*;
import com.zhb.wms2.module.base.service.BaseDictMapService;
import com.zhb.wms2.module.io.mapper.IoApplyMapper;
import com.zhb.wms2.module.io.mapper.IoOrderDetailMapper;
import com.zhb.wms2.module.io.mapper.IoOrderMapper;
import com.zhb.wms2.module.io.model.dto.*;
import com.zhb.wms2.module.io.model.entity.IoApply;
import com.zhb.wms2.module.io.model.entity.IoApplyDetail;
import com.zhb.wms2.module.io.model.entity.IoOrder;
import com.zhb.wms2.module.io.model.entity.IoOrderDetail;
import com.zhb.wms2.module.io.model.query.IoOrderQuery;
import com.zhb.wms2.module.io.model.vo.IoOrderDetailVO;
import com.zhb.wms2.module.io.model.vo.IoOrderPageVO;
import com.zhb.wms2.module.io.service.IoApplyDetailService;
import com.zhb.wms2.module.io.service.IoOrderDetailService;
import com.zhb.wms2.module.io.service.IoOrderService;
import com.zhb.wms2.module.product.mapper.ProductMapper;
import com.zhb.wms2.module.product.model.entity.Product;
import com.zhb.wms2.module.product.model.entity.ProductStockDetail;
import com.zhb.wms2.module.product.model.query.StockIoDetailQuery;
import com.zhb.wms2.module.product.model.vo.ProductPageVO;
import com.zhb.wms2.module.product.model.vo.StockIoDetailVO;
import com.zhb.wms2.module.product.service.ProductService;
import com.zhb.wms2.module.product.service.ProductStockDetailService;
import com.zhb.wms2.module.product.service.support.ProductStockSummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * IoOrderServiceImpl 服务实现
 *
 * @author zhb
 * @since 2026/3/26
 */
@Service
@RequiredArgsConstructor
public class IoOrderServiceImpl extends ServiceImpl<IoOrderMapper, IoOrder> implements IoOrderService {

    /**
     * 虚拟“无货位”记录使用的货位 ID。
     */
    private static final Long NO_LOCATION_ID = 0L;

    /**
     * 虚拟“无货位”记录使用的货位编码。
     */
    private static final String NO_LOCATION_CODE = "无货位";

    /**
     * 入库单号前缀。
     */
    private static final String INBOUND_ORDER_PREFIX = "RK";

    /**
     * 出库单号前缀。
     */
    private static final String OUTBOUND_ORDER_PREFIX = "CK";

    /**
     * 单号流水位数。
     */
    private static final int ORDER_NO_DIGIT_LENGTH = 6;

    private final IoApplyMapper ioApplyMapper;
    private final ProductMapper productMapper;
    private final ProductService productService;
    private final ProductStockDetailService productStockDetailService;
    private final IoApplyDetailService ioApplyDetailService;
    private final IoOrderDetailMapper ioOrderDetailMapper;
    private final IoOrderDetailService ioOrderDetailService;
    private final BaseDictMapService baseDictMapService;
    private final ProductStockSummaryService productStockSummaryService;

    /**
     * 分页查询出入库单，并补充申请号、基础资料和明细信息。
     */
    @Override
    public IPage<IoOrderPageVO> pageQuery(IoOrderQuery query) {
        IPage<IoOrder> page = page(new Page<>(query.getCurrent(), query.getSize()), buildWrapper(query));
        List<IoOrder> recordList = page.getRecords();
        if (recordList.isEmpty()) {
            return new Page<>(query.getCurrent(), query.getSize());
        }

        // 列表页统一批量回填申请号、字典信息和明细，避免逐条补数据。
        BaseDictMapDTO dictMap = baseDictMapService.getBaseDictMap();
        Map<Long, Deliveryman> deliverymanMap = dictMap.getDeliverymanMap() == null
                ? Map.of() : dictMap.getDeliverymanMap();
        Map<Long, Customer> customerMap = dictMap.getCustomerMap() == null
                ? Map.of() : dictMap.getCustomerMap();
        Map<Long, Warehouse> warehouseMap = dictMap.getWarehouseMap() == null
                ? Map.of() : dictMap.getWarehouseMap();
        Map<Long, Salesman> salesmanMap = dictMap.getSalesmanMap() == null
                ? Map.of() : dictMap.getSalesmanMap();
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
        Map<Long, List<IoOrderDetailVO>> detailMap = buildDetailMap(recordList.stream().map(IoOrder::getId).toList());
        return page.convert(ioOrder -> buildPageVO(ioOrder, deliverymanMap, customerMap, warehouseMap,
                salesmanMap, ioTypeMap, applyNoMap, detailMap.get(ioOrder.getId())));
    }

    /**
     * 查询出入库单详情，并组装完整展示对象。
     */
    @Override
    public IoOrderPageVO getDetailById(Long id) {
        IoOrder ioOrder = getById(id);
        if (ioOrder == null) {
            throw new BaseException("出入库单不存在");
        }

        // 详情页复用同一套 VO 组装逻辑，保证和分页列表字段一致。
        BaseDictMapDTO dictMap = baseDictMapService.getBaseDictMap();
        Map<Long, Deliveryman> deliverymanMap = dictMap.getDeliverymanMap() == null
                ? Map.of() : dictMap.getDeliverymanMap();
        Map<Long, Customer> customerMap = dictMap.getCustomerMap() == null
                ? Map.of() : dictMap.getCustomerMap();
        Map<Long, Warehouse> warehouseMap = dictMap.getWarehouseMap() == null
                ? Map.of() : dictMap.getWarehouseMap();
        Map<Long, Salesman> salesmanMap = dictMap.getSalesmanMap() == null
                ? Map.of() : dictMap.getSalesmanMap();
        Map<Long, IoType> ioTypeMap = dictMap.getIoTypeMap() == null
                ? Map.of() : dictMap.getIoTypeMap();
        Map<Long, String> applyNoMap = ioOrder.getApplyId() == null
                ? Collections.emptyMap()
                : ioApplyMapper.selectByIds(List.of(ioOrder.getApplyId())).stream()
                .collect(Collectors.toMap(IoApply::getId, IoApply::getApplyNo,
                        (left, right) -> left, LinkedHashMap::new));
        Map<Long, List<IoOrderDetailVO>> detailMap = buildDetailMap(List.of(id));
        return buildPageVO(ioOrder, deliverymanMap, customerMap, warehouseMap, salesmanMap, ioTypeMap,
                applyNoMap, detailMap.get(id));
    }

    /**
     * 按商品分页查询出入库明细流水。
     */
    @Override
    public IPage<StockIoDetailVO> pageDetailByProductId(StockIoDetailQuery query) {
        Long productId = query.getProductId();
        if (productId != null && productMapper.selectById(productId) == null) {
            throw new BaseException("商品不存在");
        }

        // 明细流水分页先查明细，再批量补单头和商品信息。
        IPage<IoOrderDetail> detailPage = ioOrderDetailMapper.selectPageByProductId(
                new Page<>(query.getCurrent(), query.getSize()), query);
        List<IoOrderDetail> detailList = detailPage.getRecords();
        if (detailList == null || detailList.isEmpty()) {
            return detailPage.convert(detail -> new StockIoDetailVO());
        }

        Set<Long> orderIds = detailList.stream()
                .map(IoOrderDetail::getOrderId)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        Map<Long, IoOrder> orderMap = orderIds.isEmpty() ? Map.of()
                : listByIds(orderIds).stream()
                .collect(Collectors.toMap(IoOrder::getId, Function.identity(),
                        (left, right) -> left, LinkedHashMap::new));
        Set<Long> productIds = detailList.stream()
                .map(IoOrderDetail::getProductId)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        Map<Long, Product> productMap = productIds.isEmpty() ? Map.of()
                : productMapper.selectByIds(productIds).stream()
                .collect(Collectors.toMap(Product::getId, Function.identity(),
                        (left, right) -> left, LinkedHashMap::new));
        BaseDictMapDTO dictMap = baseDictMapService.getBaseDictMap();
        Map<Long, Deliveryman> deliverymanMap = dictMap.getDeliverymanMap() == null
                ? Map.of() : dictMap.getDeliverymanMap();
        Map<Long, Customer> customerMap = dictMap.getCustomerMap() == null
                ? Map.of() : dictMap.getCustomerMap();
        Map<Long, Salesman> salesmanMap = dictMap.getSalesmanMap() == null
                ? Map.of() : dictMap.getSalesmanMap();
        Map<Long, Long> stockQtyMap = buildCurrentStockQtyMap(detailList);
        return detailPage.convert(detail -> {
            IoOrder ioOrder = orderMap.get(detail.getOrderId());
            if (ioOrder == null) {
                return new StockIoDetailVO();
            }
            // 当前库存按明细 ID 反查，便于页面展示该笔单据执行后的库存结果。
            Product product = productMap.get(detail.getProductId());
            return buildStockIoDetailVO(ioOrder, detail, product,
                    deliverymanMap.get(ioOrder.getDeliverymanId()),
                    customerMap.get(ioOrder.getCustomerId()),
                    salesmanMap.get(ioOrder.getSalesmanId()),
                    stockQtyMap.getOrDefault(detail.getId(), 0L));
        });
    }

    /**
     * 查询每条出入库明细当前对应的库存数量。
     */
    private Map<Long, Long> buildCurrentStockQtyMap(List<IoOrderDetail> detailList) {
        if (detailList == null || detailList.isEmpty()) {
            return Collections.emptyMap();
        }
        List<Long> detailIds = detailList.stream()
                .map(IoOrderDetail::getId)
                .filter(Objects::nonNull)
                .toList();
        if (detailIds.isEmpty()) {
            return Collections.emptyMap();
        }
        // 由 mapper 一次性计算每条明细对应的当前库存，避免在 Java 侧拼库存流水。
        return ioOrderDetailMapper.selectCurrentStockQtyByDetailIds(detailIds).stream()
                .collect(Collectors.toMap(IoOrderDetailStockQtyDTO::getDetailId,
                        item -> item.getCurrentStockQty() == null ? 0L : item.getCurrentStockQty(),
                        (left, right) -> left, LinkedHashMap::new));
    }

    /**
     * 根据申请单生成出入库单，并同步申请执行状态。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long generateOrderByApply(Long applyId, IoOrderGenerateDTO dto) {
        IoApply ioApply = ioApplyMapper.selectById(applyId);
        if (ioApply == null) {
            throw new BaseException("出入库申请不存在");
        }
        validateApplyCanGenerate(ioApply);
        validateHeaderRefs(ioApply.getOrderType(), ioApply.getDeliverymanId(), ioApply.getCustomerId(),
                ioApply.getWarehouseId(), ioApply.getSalesmanId(), ioApply.getIoTypeId());
        List<IoApplyDetail> applyDetailList = ioApplyDetailService.list(
                new LambdaQueryWrapper<IoApplyDetail>().eq(IoApplyDetail::getApplyId, applyId));
        if (applyDetailList.isEmpty()) {
            throw new BaseException(buildBizLabel(ioApply.getOrderType()) + "申请明细不能为空");
        }

        List<IoOrderDetailDTO> detailDTOList = dto.getDetailList();
        validateDetailRefs(detailDTOList);
        // 由申请生成的单据不允许随意改数量，必须与申请汇总数量一致。
        validateApplyGenerateDetails(ioApply.getOrderType(), applyDetailList, detailDTOList);

        IoOrder ioOrder = createOrder(ioApply.getOrderType(), ioApply.getId(), dto.getBizDate(),
                ioApply.getDeliverymanId(), ioApply.getCustomerId(), ioApply.getWarehouseId(),
                ioApply.getSalesmanId(), ioApply.getIoTypeId(), dto.getRemark(), detailDTOList);
        // 生成单据成功后，把申请状态推进为已执行完成。
        ioApply.setIoStatus(IoStatusEnum.DONE.getCode());
        ioApplyMapper.updateById(ioApply);
        return ioOrder.getId();
    }

    /**
     * 手工新增出入库单。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveOrder(IoOrderCreateDTO dto) {
        // 手工单据直接校验单头和明细引用，不依赖申请单。
        validateHeaderRefs(dto.getOrderType(), dto.getDeliverymanId(), dto.getCustomerId(), dto.getWarehouseId(),
                dto.getSalesmanId(), dto.getIoTypeId());
        List<IoOrderDetailDTO> detailDTOList = dto.getDetailList();
        validateDetailRefs(detailDTOList);
        return createOrder(dto.getOrderType(), null, dto.getBizDate(), dto.getDeliverymanId(),
                IoBizTypeEnum.OUTBOUND.matches(dto.getOrderType()) ? dto.getCustomerId() : null,
                IoBizTypeEnum.INBOUND.matches(dto.getOrderType()) ? dto.getWarehouseId() : null,
                dto.getSalesmanId(), dto.getIoTypeId(), dto.getRemark(), detailDTOList).getId();
    }

    /**
     * 修改出入库单，并在库存受影响时先回滚再重放。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateOrder(IoOrderUpdateDTO dto) {
        IoOrder ioOrder = getById(dto.getId());
        if (ioOrder == null) {
            throw new BaseException("出入库单不存在");
        }

        validateHeaderRefs(ioOrder.getOrderType(), dto.getDeliverymanId(), dto.getCustomerId(), dto.getWarehouseId(),
                dto.getSalesmanId(), dto.getIoTypeId());
        List<IoOrderDetailDTO> newDetailDTOList = dto.getDetailList();
        validateDetailRefs(newDetailDTOList);

        List<IoOrderDetail> oldDetailList = ioOrderDetailService.list(
                new LambdaQueryWrapper<IoOrderDetail>().eq(IoOrderDetail::getOrderId, dto.getId()));
        boolean stockChanged = hasStockChange(oldDetailList, newDetailDTOList);
        if (stockChanged) {
            // 库存受影响时先回滚旧库存，再重放新明细，避免直接 diff 出错。
            rollbackOrderStock(ioOrder, oldDetailList, "修改");
        }

        ioOrder.setBizDate(dto.getBizDate())
                .setDeliverymanId(dto.getDeliverymanId())
                .setIoTypeId(dto.getIoTypeId())
                .setRemark(dto.getRemark())
                .setCustomerId(IoBizTypeEnum.OUTBOUND.matches(ioOrder.getOrderType()) ? dto.getCustomerId() : null)
                .setWarehouseId(IoBizTypeEnum.INBOUND.matches(ioOrder.getOrderType()) ? dto.getWarehouseId() : null)
                .setSalesmanId(dto.getSalesmanId());
        if (IoBizTypeEnum.INBOUND.matches(ioOrder.getOrderType())) {
            ioOrder.setPickingStatus(PickingStatusEnum.UNPICKED.getCode());
        }
        if (!updateById(ioOrder)) {
            throw new BaseException("出入库单不存在");
        }

        // 明细修改统一采用全删全建，保持逻辑简单且与库存回放一致。
        ioOrderDetailService.removeByOrderIdChecked(dto.getId());
        List<IoOrderDetail> newDetailList = newDetailDTOList.stream()
                .map(detailDTO -> buildOrderDetail(ioOrder.getId(), ioOrder.getOrderType(), detailDTO))
                .toList();
        ioOrderDetailService.saveBatchChecked(newDetailList);
        if (stockChanged) {
            applyStockChange(ioOrder.getOrderType(), newDetailDTOList);
        }
    }

    /**
     * 调整单条明细的货位，并同步对应库存明细。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDetailLocation(IoOrderDetailLocationUpdateDTO dto) {
        IoOrderDetail detail = ioOrderDetailService.getById(dto.getDetailId());
        if (detail == null) {
            throw new BaseException("出入库单明细不存在");
        }
        IoOrder ioOrder = getById(detail.getOrderId());
        if (ioOrder == null) {
            throw new BaseException("出入库单不存在");
        }

        validateLocationExists(dto.getLocationId());
        if (Objects.equals(detail.getLocationId(), dto.getLocationId())) {
            return;
        }

        // 货位调整本质上是对旧货位回滚、对新货位重放一次库存变更。
        Map<Long, ProductStockDetail> detailMap = loadProductStockDetailMap(detail.getProductId());
        long rollbackDelta = IoBizTypeEnum.INBOUND.matches(ioOrder.getOrderType()) ? -detail.getQty() : detail.getQty();
        changeDetailQty(detailMap, detail.getProductId(), detail.getLocationId(), rollbackDelta, ioOrder.getOrderType());

        long applyDelta = IoBizTypeEnum.INBOUND.matches(ioOrder.getOrderType()) ? detail.getQty() : -detail.getQty();
        changeDetailQty(detailMap, detail.getProductId(), dto.getLocationId(), applyDelta, ioOrder.getOrderType());
        productStockSummaryService.syncByDetailMap(detail.getProductId(), detailMap);

        detail.setLocationId(dto.getLocationId());
        ioOrderDetailService.updateByIdChecked(detail);
    }

    /**
     * 对出库单执行拣货完成标记。
     */
    @Override
    public void pickById(Long id) {
        IoOrder ioOrder = getById(id);
        if (ioOrder == null) {
            throw new BaseException("出入库单不存在");
        }
        if (IoBizTypeEnum.INBOUND.matches(ioOrder.getOrderType())) {
            throw new BaseException("入库单无需拣货");
        }
        if (PickingStatusEnum.PICKED.matches(ioOrder.getPickingStatus())) {
            throw new BaseException("出库单已拣货，无需重复操作");
        }
        // 拣货状态只对出库单生效，入库单不进入该流程。
        ioOrder.setPickingStatus(PickingStatusEnum.PICKED.getCode());
        if (!updateById(ioOrder)) {
            throw new BaseException("出入库单不存在");
        }
    }

    /**
     * 删除出入库单前回滚库存，并删除对应明细。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeByIdChecked(Long id) {
        IoOrder ioOrder = getById(id);
        if (ioOrder == null) {
            throw new BaseException("出入库单不存在");
        }
        List<IoOrderDetail> detailList = ioOrderDetailService.list(
                new LambdaQueryWrapper<IoOrderDetail>().eq(IoOrderDetail::getOrderId, id));
        // 删除单据前必须先撤销库存影响，保证库存汇总和明细都回到删除前状态。
        rollbackOrderStock(ioOrder, detailList, "删除");
        ioOrderDetailService.removeByOrderIdChecked(id);
        if (!removeById(id)) {
            throw new BaseException("出入库单不存在");
        }
        rollbackApplyIoStatus(ioOrder.getApplyId());
    }

    /**
     * 创建出入库单实体并落库，同时同步库存变化。
     */
    private IoOrder createOrder(Integer orderType, Long applyId, java.time.LocalDate bizDate, Long deliverymanId,
                                Long customerId, Long warehouseId, Long salesmanId, Long ioTypeId, String remark,
                                List<IoOrderDetailDTO> detailDTOList) {
        // 单据实体先组装完整，再统一交给保存和库存调整逻辑处理。
        IoOrder ioOrder = new IoOrder()
                .setOrderNo(generateOrderNo(orderType))
                .setOrderType(orderType)
                .setApplyId(applyId)
                .setBizDate(bizDate)
                .setDeliverymanId(deliverymanId)
                .setCustomerId(customerId)
                .setWarehouseId(warehouseId)
                .setSalesmanId(salesmanId)
                .setIoTypeId(ioTypeId)
                .setRemark(remark)
                .setPickingStatus(PickingStatusEnum.UNPICKED.getCode());
        saveOrderAndAdjustStock(ioOrder, detailDTOList);
        return ioOrder;
    }

    /**
     * 保存出入库单及明细，并应用库存变化。
     */
    private void saveOrderAndAdjustStock(IoOrder ioOrder, List<IoOrderDetailDTO> detailDTOList) {
        if (!save(ioOrder)) {
            throw new BaseException(buildBizLabel(ioOrder.getOrderType()) + "单新增失败");
        }

        // 单头落库后再生成明细并同步库存，避免明细缺少主单 ID。
        List<IoOrderDetail> detailList = detailDTOList.stream()
                .map(detailDTO -> buildOrderDetail(ioOrder.getId(), ioOrder.getOrderType(), detailDTO))
                .toList();
        ioOrderDetailService.saveBatchChecked(detailList);
        applyStockChange(ioOrder.getOrderType(), detailDTOList);
    }

    /**
     * 校验申请单是否满足生成出入库单的前置条件。
     */
    private void validateApplyCanGenerate(IoApply ioApply) {
        if (!ApproveStatusEnum.APPROVED.matches(ioApply.getApproveStatus())) {
            throw new BaseException(buildBizLabel(ioApply.getOrderType()) + "申请未审批，无法生成"
                    + buildBizLabel(ioApply.getOrderType()) + "单");
        }
        if (IoStatusEnum.DONE.matches(ioApply.getIoStatus())) {
            throw new BaseException(buildBizLabel(ioApply.getOrderType()) + "申请已生成"
                    + buildBizLabel(ioApply.getOrderType()) + "单");
        }
        long ioOrderCount = count(new LambdaQueryWrapper<IoOrder>().eq(IoOrder::getApplyId, ioApply.getId()));
        if (ioOrderCount > 0) {
            throw new BaseException(buildBizLabel(ioApply.getOrderType()) + "申请已生成"
                    + buildBizLabel(ioApply.getOrderType()) + "单");
        }
    }

    /**
     * 删除来源单据后，将申请执行状态回退为未执行。
     */
    private void rollbackApplyIoStatus(Long applyId) {
        if (applyId == null) {
            return;
        }
        IoApply ioApply = ioApplyMapper.selectById(applyId);
        if (ioApply == null) {
            throw new BaseException("来源出入库申请不存在");
        }
        ioApply.setIoStatus(IoStatusEnum.PENDING.getCode());
        if (ioApplyMapper.updateById(ioApply) <= 0) {
            throw new BaseException("来源出入库申请不存在");
        }
    }

    /**
     * 校验单头关联的送货员、客户、业务员和出入库类型。
     */
    private void validateHeaderRefs(Integer orderType, Long deliverymanId, Long customerId, Long warehouseId,
                                    Long salesmanId, Long ioTypeId) {
        validateDeliveryman(orderType, deliverymanId);
        validateIoType(orderType, ioTypeId);
        if (IoBizTypeEnum.OUTBOUND.matches(orderType)) {
            // 出库单必须具备客户和业务员。
            if (customerId == null) {
                throw new BaseException("出库单客户不能为空");
            }
            validateCustomer(customerId);
            if (salesmanId == null) {
                throw new BaseException("出库单业务员不能为空");
            }
            validateSalesman(salesmanId);
        } else {
            // 入库单必须绑定仓库，业务员按现有业务规则保留可选。
            if (warehouseId == null) {
                throw new BaseException("入库单仓库不能为空");
            }
            validateWarehouse(warehouseId);
            if (salesmanId != null) {
                validateSalesman(salesmanId);
            }
        }
    }

    /**
     * 校验送货员存在且适用于当前单据类型。
     */
    private void validateDeliveryman(Integer orderType, Long deliverymanId) {
        BaseDictMapDTO dictMap = baseDictMapService.getBaseDictMap();
        Deliveryman deliveryman = dictMap.getDeliverymanMap() == null
                ? null
                : dictMap.getDeliverymanMap().get(deliverymanId);
        if (deliveryman == null) {
            throw new BaseException("送货员不存在");
        }
        if (!ScopeEnum.supportsBizType(deliveryman.getScope(), orderType)) {
            throw new BaseException("送货员不适用于当前单据类型");
        }
    }

    /**
     * 校验客户存在。
     */
    private void validateCustomer(Long customerId) {
        BaseDictMapDTO dictMap = baseDictMapService.getBaseDictMap();
        Customer customer = dictMap.getCustomerMap() == null
                ? null
                : dictMap.getCustomerMap().get(customerId);
        if (customer == null) {
            throw new BaseException("客户不存在");
        }
    }

    /**
     * 校验业务员存在。
     */
    private void validateSalesman(Long salesmanId) {
        BaseDictMapDTO dictMap = baseDictMapService.getBaseDictMap();
        Salesman salesman = dictMap.getSalesmanMap() == null
                ? null
                : dictMap.getSalesmanMap().get(salesmanId);
        if (salesman == null) {
            throw new BaseException("业务员不存在");
        }
    }

    /**
     * 校验仓库存在。
     */
    private void validateWarehouse(Long warehouseId) {
        BaseDictMapDTO dictMap = baseDictMapService.getBaseDictMap();
        Warehouse warehouse = dictMap.getWarehouseMap() == null
                ? null
                : dictMap.getWarehouseMap().get(warehouseId);
        if (warehouse == null) {
            throw new BaseException("仓库不存在");
        }
    }

    /**
     * 校验出入库类型存在且适用于当前单据类型。
     */
    private void validateIoType(Integer orderType, Long ioTypeId) {
        BaseDictMapDTO dictMap = baseDictMapService.getBaseDictMap();
        IoType ioType = dictMap.getIoTypeMap() == null
                ? null
                : dictMap.getIoTypeMap().get(ioTypeId);
        if (ioType == null) {
            throw new BaseException("出入库类型不存在");
        }
        if (!ScopeEnum.supportsBizType(ioType.getScope(), orderType)) {
            throw new BaseException("出入库类型与单据类型不匹配");
        }
    }

    /**
     * 校验明细中的商品和货位引用都存在。
     */
    private void validateDetailRefs(List<IoOrderDetailDTO> detailDTOList) {
        BaseDictMapDTO dictMap = baseDictMapService.getBaseDictMap();
        Map<Long, ProductLocation> productLocationMap = dictMap.getProductLocationMap() == null
                ? Map.of()
                : dictMap.getProductLocationMap();
        // 先批量收集商品，再顺手校验每条明细的货位引用。
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

    /**
     * 校验货位存在。
     */
    private void validateLocationExists(Long locationId) {
        BaseDictMapDTO dictMap = baseDictMapService.getBaseDictMap();
        Map<Long, ProductLocation> productLocationMap = dictMap.getProductLocationMap() == null
                ? Map.of()
                : dictMap.getProductLocationMap();
        if (!productLocationMap.containsKey(locationId)) {
            throw new BaseException("商品货位不存在");
        }
    }

    /**
     * 校验由申请生成的出入库单明细数量与申请一致。
     */
    private void validateApplyGenerateDetails(Integer orderType, List<IoApplyDetail> applyDetailList,
                                              List<IoOrderDetailDTO> detailDTOList) {
        boolean hasLegacyDetail = applyDetailList.stream().anyMatch(detail -> detail.getLocationId() == null);
        // 新申请明细已带货位时，生成单据必须按商品+货位一致；历史数据继续兼容旧口径。
        if (!hasLegacyDetail) {
            Map<Long, Map<Long, Long>> applyQtyMap = buildStockQtyMapFromApplyDetail(applyDetailList);
            Map<Long, Map<Long, Long>> orderQtyMap = buildStockQtyMapFromDetailDTO(detailDTOList);
            if (!applyQtyMap.equals(orderQtyMap)) {
                throw new BaseException(buildBizLabel(orderType) + "单明细数量与"
                        + buildBizLabel(orderType) + "申请不一致");
            }
            return;
        }

        // 生成单据时按商品汇总数量比对，允许历史申请继续拆到多个货位。
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

    /**
     * 将出入库明细 DTO 转为实体。
     */
    private IoOrderDetail buildOrderDetail(Long orderId, Integer orderType, IoOrderDetailDTO detailDTO) {
        return new IoOrderDetail()
                .setOrderId(orderId)
                .setOrderType(orderType)
                .setProductId(detailDTO.getProductId())
                .setQty(detailDTO.getQty())
                .setLocationId(detailDTO.getLocationId())
                .setRemark(detailDTO.getRemark())
                .setPickedQty(0L);
    }

    /**
     * 按单据方向增减库存明细，并同步商品库存汇总。
     */
    private void applyStockChange(Integer orderType, List<IoOrderDetailDTO> detailDTOList) {
        // 先按商品分组加载库存明细，再逐条应用库存增减，最后回写商品汇总。
        Map<Long, Map<Long, ProductStockDetail>> detailGroupMap = loadStockDetailGroup(detailDTOList);
        for (IoOrderDetailDTO detailDTO : detailDTOList) {
            Map<Long, ProductStockDetail> productDetailMap = detailGroupMap.computeIfAbsent(
                    detailDTO.getProductId(), key -> new LinkedHashMap<>());
            long delta = IoBizTypeEnum.INBOUND.matches(orderType) ? detailDTO.getQty() : -detailDTO.getQty();
            changeDetailQty(productDetailMap, detailDTO.getProductId(), detailDTO.getLocationId(), delta, orderType);
        }
        for (Map.Entry<Long, Map<Long, ProductStockDetail>> entry : detailGroupMap.entrySet()) {
            productStockSummaryService.syncByDetailMap(entry.getKey(), entry.getValue());
        }
    }

    /**
     * 判断修改前后是否发生了库存分布变化。
     */
    private boolean hasStockChange(List<IoOrderDetail> oldDetailList, List<IoOrderDetailDTO> newDetailDTOList) {
        // 只要商品、货位或数量任一维度变化，就认为库存分布已变化。
        return !buildStockQtyMapFromOrderDetail(oldDetailList).equals(buildStockQtyMapFromDetailDTO(newDetailDTOList));
    }

    /**
     * 将旧明细列表转换为商品-货位-数量的映射。
     */
    private Map<Long, Map<Long, Long>> buildStockQtyMapFromOrderDetail(List<IoOrderDetail> detailList) {
        if (detailList == null || detailList.isEmpty()) {
            return Collections.emptyMap();
        }
        return detailList.stream()
                .collect(Collectors.groupingBy(IoOrderDetail::getProductId, LinkedHashMap::new,
                        Collectors.groupingBy(IoOrderDetail::getLocationId, LinkedHashMap::new,
                                Collectors.summingLong(detail -> detail.getQty() == null ? 0L : detail.getQty()))));
    }

    /**
     * 将申请明细列表转换为商品-货位-数量的映射。
     */
    private Map<Long, Map<Long, Long>> buildStockQtyMapFromApplyDetail(List<IoApplyDetail> detailList) {
        if (detailList == null || detailList.isEmpty()) {
            return Collections.emptyMap();
        }
        return detailList.stream()
                .collect(Collectors.groupingBy(IoApplyDetail::getProductId, LinkedHashMap::new,
                        Collectors.groupingBy(IoApplyDetail::getLocationId, LinkedHashMap::new,
                                Collectors.summingLong(detail -> detail.getQty() == null ? 0L : detail.getQty()))));
    }

    /**
     * 将新明细 DTO 列表转换为商品-货位-数量的映射。
     */
    private Map<Long, Map<Long, Long>> buildStockQtyMapFromDetailDTO(List<IoOrderDetailDTO> detailDTOList) {
        if (detailDTOList == null || detailDTOList.isEmpty()) {
            return Collections.emptyMap();
        }
        return detailDTOList.stream()
                .collect(Collectors.groupingBy(IoOrderDetailDTO::getProductId, LinkedHashMap::new,
                        Collectors.groupingBy(IoOrderDetailDTO::getLocationId, LinkedHashMap::new,
                                Collectors.summingLong(detail -> detail.getQty() == null ? 0L : detail.getQty()))));
    }

    /**
     * 按原单据方向反向回滚库存变化。
     */
    private void rollbackOrderStock(IoOrder ioOrder, List<IoOrderDetail> oldDetailList, String actionName) {
        List<IoOrderDetailDTO> rollbackDetailList = oldDetailList.stream().map(this::toDetailDTO).toList();
        // 回滚库存时沿用当前库存明细分组，避免对同一商品重复查库。
        Map<Long, Map<Long, ProductStockDetail>> detailGroupMap = loadStockDetailGroup(rollbackDetailList);
        for (IoOrderDetailDTO detailDTO : rollbackDetailList) {
            Map<Long, ProductStockDetail> productDetailMap = detailGroupMap.computeIfAbsent(
                    detailDTO.getProductId(), key -> new LinkedHashMap<>());
            long rollbackDelta = IoBizTypeEnum.INBOUND.matches(ioOrder.getOrderType())
                    ? -detailDTO.getQty()
                    : detailDTO.getQty();
            changeDetailQty(productDetailMap, detailDTO.getProductId(), detailDTO.getLocationId(),
                    rollbackDelta, ioOrder.getOrderType());
        }
        for (Map.Entry<Long, Map<Long, ProductStockDetail>> entry : detailGroupMap.entrySet()) {
            // 每个商品单独重算库存汇总，保证商品表库存与明细一致。
            productStockSummaryService.syncByDetailMap(entry.getKey(), entry.getValue());
        }
    }

    /**
     * 将历史明细实体转换为库存回滚使用的 DTO。
     */
    private IoOrderDetailDTO toDetailDTO(IoOrderDetail detail) {
        return new IoOrderDetailDTO()
                .setProductId(detail.getProductId())
                .setQty(detail.getQty())
                .setLocationId(detail.getLocationId() == null ? NO_LOCATION_ID : detail.getLocationId())
                .setRemark(detail.getRemark());
    }

    /**
     * 批量加载涉及商品的库存明细分组。
     */
    private Map<Long, Map<Long, ProductStockDetail>> loadStockDetailGroup(List<IoOrderDetailDTO> detailDTOList) {
        Set<Long> productIds = detailDTOList.stream().map(IoOrderDetailDTO::getProductId).collect(Collectors.toSet());
        if (productIds.isEmpty()) {
            return new LinkedHashMap<>();
        }
        // 一次性按商品拉出库存明细，后续库存调整直接在内存映射上操作。
        return productStockDetailService.list(new LambdaQueryWrapper<ProductStockDetail>().in(ProductStockDetail::getProductId, productIds))
                .stream()
                .collect(Collectors.groupingBy(ProductStockDetail::getProductId, LinkedHashMap::new,
                        Collectors.toMap(ProductStockDetail::getLocationId, detail -> detail,
                                (left, right) -> left, LinkedHashMap::new)));
    }

    /**
     * 加载单个商品的库存明细映射。
     */
    private Map<Long, ProductStockDetail> loadProductStockDetailMap(Long productId) {
        return productStockDetailService.list(new LambdaQueryWrapper<ProductStockDetail>()
                        .eq(ProductStockDetail::getProductId, productId))
                .stream()
                .collect(Collectors.toMap(ProductStockDetail::getLocationId, Function.identity(),
                        (left, right) -> left, LinkedHashMap::new));
    }

    /**
     * 调整指定商品在指定货位上的库存数量。
     */
    private void changeDetailQty(Map<Long, ProductStockDetail> detailMap, Long productId, Long locationId, Long delta,
                                 Integer orderType) {
        if (delta == 0) {
            return;
        }
        ProductStockDetail detail = detailMap.get(locationId);
        long currentQty = detail == null || detail.getQty() == null ? 0L : detail.getQty();
        long targetQty = currentQty + delta;
        if (targetQty < 0) {
            throw new BaseException(buildBizLabel(orderType) + "失败，商品库存不足");
        }
        if (targetQty == 0) {
            // 出库扣到最后一条库存时保留 0 库存记录，便于后续继续沿用历史货位。
            if (shouldKeepZeroStockDetail(detailMap, detail, orderType)) {
                detail.setQty(0L);
                productStockDetailService.updateByIdChecked(detail);
                return;
            }
            // 非最后库存归零时继续删除明细，避免同商品残留多条 0 库存记录。
            productStockDetailService.removeByIdChecked(detail.getId());
            detailMap.remove(locationId);
            return;
        }
        if (detail == null) {
            // 当前货位尚无库存记录时，按新增明细处理。
            ProductStockDetail productStockDetail = new ProductStockDetail()
                    .setProductId(productId)
                    .setLocationId(locationId)
                    .setQty(targetQty);
            productStockDetailService.saveChecked(productStockDetail);
            detailMap.put(locationId, productStockDetail);
            return;
        }
        detail.setQty(targetQty);
        productStockDetailService.updateByIdChecked(detail);
    }

    /**
     * 判断是否需要保留最后一条 0 库存明细。
     */
    private boolean shouldKeepZeroStockDetail(Map<Long, ProductStockDetail> detailMap, ProductStockDetail currentDetail,
                                              Integer orderType) {
        if (currentDetail == null || !IoBizTypeEnum.OUTBOUND.matches(orderType)) {
            return false;
        }
        return detailMap.values().stream()
                .filter(Objects::nonNull)
                .filter(detail -> !Objects.equals(detail.getLocationId(), currentDetail.getLocationId()))
                .map(ProductStockDetail::getQty)
                .filter(Objects::nonNull)
                .noneMatch(qty -> qty > 0);
    }

    /**
     * 生成同类型出入库单号。
     */
    private String generateOrderNo(Integer orderType) {
        String prefix = IoBizTypeEnum.INBOUND.matches(orderType)
                ? INBOUND_ORDER_PREFIX
                : OUTBOUND_ORDER_PREFIX;
        // 单号按同类型历史最后一条记录递增生成。
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

    /**
     * 从历史出入库单号中解析流水号部分。
     */
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

    /**
     * 返回单据类型对应的业务名称。
     */
    private String buildBizLabel(Integer orderType) {
        return IoBizTypeEnum.getDesc(orderType);
    }

    /**
     * 组装商品出入库流水展示对象。
     */
    private StockIoDetailVO buildStockIoDetailVO(IoOrder ioOrder, IoOrderDetail detail, Product product,
                                                     Deliveryman deliveryman,
                                                     Customer customer, Salesman salesman,
                                                     Long currentStockQty) {
        return new StockIoDetailVO()
                .setOrderNo(ioOrder.getOrderNo())
                .setOrderId(ioOrder.getId())
                .setOrderType(ioOrder.getOrderType())
                .setOrderTypeName(buildBizLabel(ioOrder.getOrderType()))
                .setBizDate(ioOrder.getBizDate())
                .setQty(detail.getQty())
                .setDeliveryman(deliveryman)
                .setCustomer(customer)
                .setSalesman(salesman)
                .setProduct(product)
                .setCurrentStockQty(currentStockQty == null ? 0L : currentStockQty);
    }

    /**
     * 构建出入库单分页查询条件。
     */
    private LambdaQueryWrapper<IoOrder> buildWrapper(IoOrderQuery query) {
        return new LambdaQueryWrapper<IoOrder>()
                .like(StrUtil.isNotBlank(query.getOrderNo()), IoOrder::getOrderNo, query.getOrderNo())
                .eq(query.getOrderType() != null, IoOrder::getOrderType, query.getOrderType())
                .ge(query.getBizDateStart() != null, IoOrder::getBizDate, query.getBizDateStart())
                .le(query.getBizDateEnd() != null, IoOrder::getBizDate, query.getBizDateEnd())
                .eq(query.getDeliverymanId() != null, IoOrder::getDeliverymanId, query.getDeliverymanId())
                .eq(query.getCustomerId() != null, IoOrder::getCustomerId, query.getCustomerId())
                .eq(query.getWarehouseId() != null, IoOrder::getWarehouseId, query.getWarehouseId())
                .eq(query.getSalesmanId() != null, IoOrder::getSalesmanId, query.getSalesmanId())
                .eq(query.getIoTypeId() != null, IoOrder::getIoTypeId, query.getIoTypeId())
                .eq(query.getApplyId() != null, IoOrder::getApplyId, query.getApplyId())
                .eq(query.getPickingStatus() != null, IoOrder::getPickingStatus, query.getPickingStatus())
                .orderByDesc(IoOrder::getId);
    }

    /**
     * 批量查询并组装出入库单明细。
     */
    private Map<Long, List<IoOrderDetailVO>> buildDetailMap(List<Long> orderIds) {
        if (orderIds == null || orderIds.isEmpty()) {
            return Collections.emptyMap();
        }
        List<IoOrderDetail> detailList = ioOrderDetailService.list(new LambdaQueryWrapper<IoOrderDetail>()
                .in(IoOrderDetail::getOrderId, orderIds)
                .orderByAsc(IoOrderDetail::getId));
        if (detailList.isEmpty()) {
            return Collections.emptyMap();
        }

        // 商品详情和货位编码都按批量方式回填，避免明细列表逐条查字典。
        Set<Long> productIds = detailList.stream()
                .map(IoOrderDetail::getProductId)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        Map<Long, ProductPageVO> productMap = productIds.isEmpty()
                ? Collections.emptyMap()
                : productService.getDetailMapByIds(productIds);

        BaseDictMapDTO dictMap = baseDictMapService.getBaseDictMap();
        Map<Long, ProductLocation> locationMap = dictMap.getProductLocationMap() == null
                ? Map.of()
                : new LinkedHashMap<>(dictMap.getProductLocationMap());

        return detailList.stream()
                .map(detail -> buildDetailVO(detail, productMap.get(detail.getProductId()),
                        buildLocationCode(detail.getLocationId(), locationMap)))
                .collect(Collectors.groupingBy(IoOrderDetailVO::getOrderId, LinkedHashMap::new, Collectors.toList()));
    }

    /**
     * 组装单条出入库明细展示对象。
     */
    private IoOrderDetailVO buildDetailVO(IoOrderDetail detail, ProductPageVO product, String locationCode) {
        IoOrderDetailVO vo = new IoOrderDetailVO();
        vo.setId(detail.getId())
                .setOrderId(detail.getOrderId())
                .setOrderType(detail.getOrderType())
                .setProductId(detail.getProductId())
                .setQty(detail.getQty())
                .setLocationId(detail.getLocationId())
                .setRemark(detail.getRemark())
                .setPickedQty(detail.getPickedQty());
        vo.setCreateTime(detail.getCreateTime())
                .setUpdateTime(detail.getUpdateTime())
                .setCreateBy(detail.getCreateBy())
                .setUpdateBy(detail.getUpdateBy());
        vo.setProduct(product)
                .setLocationCode(locationCode);
        return vo;
    }

    /**
     * 将货位 ID 转成可展示的货位编码。
     */
    private String buildLocationCode(Long locationId, Map<Long, ProductLocation> locationMap) {
        if (Objects.equals(locationId, NO_LOCATION_ID)) {
            return NO_LOCATION_CODE;
        }
        ProductLocation location = locationMap.get(locationId);
        return location == null ? null : location.getCode();
    }

    /**
     * 组装出入库单分页展示对象。
     */
    private IoOrderPageVO buildPageVO(IoOrder ioOrder, Map<Long, Deliveryman> deliverymanMap,
                                      Map<Long, Customer> customerMap, Map<Long, Warehouse> warehouseMap,
                                      Map<Long, Salesman> salesmanMap,
                                      Map<Long, IoType> ioTypeMap,
                                      Map<Long, String> applyNoMap, List<IoOrderDetailVO> detailList) {
        IoOrderPageVO vo = new IoOrderPageVO();
        vo.setId(ioOrder.getId())
                .setOrderNo(ioOrder.getOrderNo())
                .setOrderType(ioOrder.getOrderType())
                .setApplyId(ioOrder.getApplyId())
                .setBizDate(ioOrder.getBizDate())
                .setDeliverymanId(ioOrder.getDeliverymanId())
                .setCustomerId(ioOrder.getCustomerId())
                .setWarehouseId(ioOrder.getWarehouseId())
                .setSalesmanId(ioOrder.getSalesmanId())
                .setIoTypeId(ioOrder.getIoTypeId())
                .setRemark(ioOrder.getRemark())
                .setPickingStatus(ioOrder.getPickingStatus());
        vo.setCreateTime(ioOrder.getCreateTime())
                .setUpdateTime(ioOrder.getUpdateTime())
                .setCreateBy(ioOrder.getCreateBy())
                .setUpdateBy(ioOrder.getUpdateBy());
        Deliveryman deliveryman = deliverymanMap.get(ioOrder.getDeliverymanId());
        Customer customer = customerMap.get(ioOrder.getCustomerId());
        Warehouse warehouse = warehouseMap.get(ioOrder.getWarehouseId());
        Salesman salesman = salesmanMap.get(ioOrder.getSalesmanId());
        IoType ioType = ioTypeMap.get(ioOrder.getIoTypeId());
        vo.setOrderTypeName(buildBizLabel(ioOrder.getOrderType()))
                .setApplyNo(ioOrder.getApplyId() == null ? null : applyNoMap.get(ioOrder.getApplyId()))
                .setDeliveryman(deliveryman)
                .setCustomer(customer)
                .setWarehouse(warehouse)
                .setSalesman(salesman)
                .setIoTypeName(ioType == null ? null : ioType.getName())
                .setPickingStatusName(buildPickingStatusName(ioOrder))
                .setDetailList(detailList == null ? List.of() : detailList);
        return vo;
    }

    /**
     * 返回出库单拣货状态名称，入库单不展示该字段。
     */
    private String buildPickingStatusName(IoOrder ioOrder) {
        if (IoBizTypeEnum.INBOUND.matches(ioOrder.getOrderType())) {
            return null;
        }
        return PickingStatusEnum.getDesc(ioOrder.getPickingStatus());
    }

}
