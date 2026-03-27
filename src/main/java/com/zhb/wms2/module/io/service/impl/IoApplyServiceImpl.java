package com.zhb.wms2.module.io.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhb.wms2.common.enums.ApproveStatusEnum;
import com.zhb.wms2.common.enums.IoBizTypeEnum;
import com.zhb.wms2.common.enums.IoStatusEnum;
import com.zhb.wms2.common.enums.ScopeEnum;
import com.zhb.wms2.common.exception.BaseException;
import com.zhb.wms2.module.base.model.dto.BaseDictMapDTO;
import com.zhb.wms2.module.base.model.entity.Customer;
import com.zhb.wms2.module.base.model.entity.Deliveryman;
import com.zhb.wms2.module.base.model.entity.IoType;
import com.zhb.wms2.module.base.model.entity.Salesman;
import com.zhb.wms2.module.base.model.entity.Warehouse;
import com.zhb.wms2.module.base.service.BaseDictMapService;
import com.zhb.wms2.module.io.mapper.IoApplyMapper;
import com.zhb.wms2.module.io.mapper.IoOrderMapper;
import com.zhb.wms2.module.io.model.dto.IoApplyCreateDTO;
import com.zhb.wms2.module.io.model.dto.IoApplyCreateDetailDTO;
import com.zhb.wms2.module.io.model.dto.IoApplyUpdateDTO;
import com.zhb.wms2.module.io.model.entity.IoApply;
import com.zhb.wms2.module.io.model.entity.IoApplyDetail;
import com.zhb.wms2.module.io.model.entity.IoOrder;
import com.zhb.wms2.module.io.model.vo.IoApplyDetailVO;
import com.zhb.wms2.module.io.model.query.IoApplyQuery;
import com.zhb.wms2.module.io.model.vo.IoApplyPageVO;
import com.zhb.wms2.module.io.service.IoApplyDetailService;
import com.zhb.wms2.module.io.service.IoApplyService;
import com.zhb.wms2.module.product.mapper.ProductMapper;
import com.zhb.wms2.module.product.model.entity.Product;
import com.zhb.wms2.module.product.model.vo.ProductPageVO;
import com.zhb.wms2.module.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * IoApplyServiceImpl 服务实现
 *
 * @author zhb
 * @since 2026/3/26
 */
@Service
@RequiredArgsConstructor
public class IoApplyServiceImpl extends ServiceImpl<IoApplyMapper, IoApply> implements IoApplyService {

    /**
     * 入库申请单号前缀。
     */
    private static final String INBOUND_APPLY_PREFIX = "RS";

    /**
     * 出库申请单号前缀。
     */
    private static final String OUTBOUND_APPLY_PREFIX = "CS";

    /**
     * 申请单号流水位数。
     */
    private static final int APPLY_NO_DIGIT_LENGTH = 6;

    private final IoOrderMapper ioOrderMapper;
    private final ProductMapper productMapper;
    private final ProductService productService;
    private final BaseDictMapService baseDictMapService;
    private final IoApplyDetailService ioApplyDetailService;

    /**
     * 分页查询申请单，并补充关联基础资料与明细信息。
     */
    @Override
    public IPage<IoApplyPageVO> pageQuery(IoApplyQuery query) {
        IPage<IoApply> page = page(new Page<>(query.getCurrent(), query.getSize()), buildWrapper(query));
        List<IoApply> recordList = page.getRecords();
        if (recordList.isEmpty()) {
            return new Page<>(query.getCurrent(), query.getSize());
        }

        // 分页结果统一批量补基础资料和明细，避免列表页出现 N+1 查询。
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
        Map<Long, List<IoApplyDetailVO>> detailMap = buildDetailMap(recordList.stream().map(IoApply::getId).toList());
        return page.convert(ioApply -> buildPageVO(ioApply, deliverymanMap, customerMap, warehouseMap,
                salesmanMap, ioTypeMap, detailMap.get(ioApply.getId())));
    }

    /**
     * 查询申请单详情，并组装完整展示对象。
     */
    @Override
    public IoApplyPageVO getDetailById(Long id) {
        IoApply ioApply = getById(id);
        if (ioApply == null) {
            throw new BaseException("出入库申请不存在");
        }

        // 详情页复用列表组装逻辑，保证展示字段口径一致。
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
        Map<Long, List<IoApplyDetailVO>> detailMap = buildDetailMap(List.of(id));
        return buildPageVO(ioApply, deliverymanMap, customerMap, warehouseMap, salesmanMap, ioTypeMap,
                detailMap.get(id));
    }

    /**
     * 新增申请单及其明细。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveApply(IoApplyCreateDTO dto) {
        validateBizData(dto);

        // 出库场景绑定客户，入库场景绑定仓库，业务员按现有业务规则保留可选。
        IoApply ioApply = new IoApply()
                .setApplyNo(generateApplyNo(dto.getOrderType()))
                .setOrderType(dto.getOrderType())
                .setApplyDate(dto.getApplyDate())
                .setDeliverymanId(dto.getDeliverymanId())
                .setCustomerId(IoBizTypeEnum.OUTBOUND.matches(dto.getOrderType()) ? dto.getCustomerId() : null)
                .setWarehouseId(IoBizTypeEnum.INBOUND.matches(dto.getOrderType()) ? dto.getWarehouseId() : null)
                .setSalesmanId(dto.getSalesmanId())
                .setIoTypeId(dto.getIoTypeId())
                .setRemark(dto.getRemark())
                .setApproveStatus(ApproveStatusEnum.UNAPPROVED.getCode())
                .setIoStatus(IoStatusEnum.PENDING.getCode())
                .setApprovedTime(null);
        if (!save(ioApply)) {
            throw new BaseException("出入库申请新增失败");
        }
        // 主单保存成功后再批量落明细，保证明细拿到真实申请 ID。
        saveApplyDetails(ioApply.getId(), dto.getDetailList());
        return ioApply.getId();
    }

    /**
     * 修改未审批的申请单，并重建其明细。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateApply(IoApplyUpdateDTO dto) {
        IoApply ioApply = getById(dto.getId());
        if (ioApply == null) {
            throw new BaseException("出入库申请不存在");
        }
        validateApplyCanUpdate(ioApply);
        validateBizData(dto);

        boolean orderTypeChanged = !Objects.equals(ioApply.getOrderType(), dto.getOrderType());
        if (orderTypeChanged) {
            // 单据方向变化时重生申请单号，保持前缀与业务类型一致。
            ioApply.setApplyNo(generateApplyNo(dto.getOrderType()));
        }
        ioApply.setOrderType(dto.getOrderType())
                .setApplyDate(dto.getApplyDate())
                .setDeliverymanId(dto.getDeliverymanId())
                .setCustomerId(IoBizTypeEnum.OUTBOUND.matches(dto.getOrderType()) ? dto.getCustomerId() : null)
                .setWarehouseId(IoBizTypeEnum.INBOUND.matches(dto.getOrderType()) ? dto.getWarehouseId() : null)
                .setSalesmanId(dto.getSalesmanId())
                .setIoTypeId(dto.getIoTypeId())
                .setRemark(dto.getRemark());
        if (!updateById(ioApply)) {
            throw new BaseException("出入库申请不存在");
        }

        // 修改申请时直接重建明细，避免逐条 diff 带来额外复杂度。
        ioApplyDetailService.removeByApplyIdChecked(dto.getId());
        saveApplyDetails(dto.getId(), dto.getDetailList());
    }

    /**
     * 审批申请单。
     */
    @Override
    public void approveById(Long id) {
        IoApply ioApply = getById(id);
        if (ioApply == null) {
            throw new BaseException("出入库申请不存在");
        }
        if (isApproved(ioApply)) {
            throw new BaseException(buildBizLabel(ioApply.getOrderType()) + "申请已审批，请勿重复审批");
        }
        // 审批只更新审批状态和时间，不在此处直接生成出入库单。
        ioApply.setApproveStatus(ApproveStatusEnum.APPROVED.getCode())
                .setApprovedTime(LocalDateTime.now());
        if (!updateById(ioApply)) {
            throw new BaseException("出入库申请不存在");
        }
    }

    /**
     * 取消申请单审批，前提是未生成出入库单。
     */
    @Override
    public void cancelApproveById(Long id) {
        IoApply ioApply = getById(id);
        if (ioApply == null) {
            throw new BaseException("出入库申请不存在");
        }
        if (!isApproved(ioApply)) {
            throw new BaseException(buildBizLabel(ioApply.getOrderType()) + "申请未审批，无需取消审批");
        }
        // 只有还未生成单据的申请才允许撤销审批。
        validateApplyCanCancelApprove(ioApply);
        ioApply.setApproveStatus(ApproveStatusEnum.UNAPPROVED.getCode())
                .setApprovedTime(null);
        if (!updateById(ioApply)) {
            throw new BaseException("出入库申请不存在");
        }
    }

    /**
     * 删除未审批的申请单及其明细。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeByIdChecked(Long id) {
        IoApply ioApply = getById(id);
        if (ioApply == null) {
            throw new BaseException("出入库申请不存在");
        }
        // 删除申请前先做状态校验，再级联清理明细。
        validateApplyCanDelete(ioApply);
        ioApplyDetailService.removeByApplyIdChecked(id);
        if (!removeById(id)) {
            throw new BaseException("出入库申请不存在");
        }
    }

    /**
     * 校验申请头与明细中的业务引用是否合法。
     */
    private void validateBizData(IoApplyCreateDTO dto) {
        validateDeliveryman(dto.getDeliverymanId(), dto.getOrderType());
        validateIoType(dto.getIoTypeId(), dto.getOrderType());
        if (IoBizTypeEnum.OUTBOUND.matches(dto.getOrderType())) {
            // 出库申请必须绑定客户和业务员。
            if (dto.getCustomerId() == null) {
                throw new BaseException("出库申请客户不能为空");
            }
            validateCustomer(dto.getCustomerId());
            if (dto.getSalesmanId() == null) {
                throw new BaseException("出库申请业务员不能为空");
            }
            validateSalesman(dto.getSalesmanId());
        } else {
            if (dto.getWarehouseId() == null) {
                throw new BaseException("入库申请仓库不能为空");
            }
            validateWarehouse(dto.getWarehouseId());
            if (dto.getSalesmanId() != null) {
                validateSalesman(dto.getSalesmanId());
            }
        }
        validateProducts(dto.getDetailList());
    }

    /**
     * 校验送货员存在且适用于当前单据类型。
     */
    private void validateDeliveryman(Long deliverymanId, Integer orderType) {
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
    private void validateIoType(Long ioTypeId, Integer orderType) {
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
     * 校验申请明细中的商品都存在。
     */
    private void validateProducts(List<IoApplyCreateDetailDTO> detailList) {
        // 先对商品 ID 去重，再批量校验是否全部存在。
        Set<Long> productIds = new LinkedHashSet<>();
        for (IoApplyCreateDetailDTO detail : detailList) {
            productIds.add(detail.getProductId());
        }
        List<Product> productList = productMapper.selectBatchIds(productIds);
        if (productList.size() != productIds.size()) {
            throw new BaseException("申请明细中存在不存在的商品");
        }
    }

    /**
     * 将申请明细 DTO 转为实体。
     */
    private IoApplyDetail buildDetail(Long applyId, IoApplyCreateDetailDTO detailDTO) {
        return new IoApplyDetail()
                .setApplyId(applyId)
                .setProductId(detailDTO.getProductId())
                .setQty(detailDTO.getQty())
                .setRemark(detailDTO.getRemark());
    }

    /**
     * 批量保存申请明细。
     */
    private void saveApplyDetails(Long applyId, List<IoApplyCreateDetailDTO> detailDTOList) {
        // 明细按前端提交顺序转实体后批量落库。
        List<IoApplyDetail> detailList = detailDTOList.stream()
                .map(detailDTO -> buildDetail(applyId, detailDTO))
                .toList();
        ioApplyDetailService.saveBatchChecked(detailList);
    }

    /**
     * 生成同类型申请单号。
     */
    private String generateApplyNo(Integer orderType) {
        String prefix = IoBizTypeEnum.INBOUND.matches(orderType)
                ? INBOUND_APPLY_PREFIX
                : OUTBOUND_APPLY_PREFIX;
        // 单号按同类型历史最后一条记录递增生成。
        IoApply lastApply = getOne(new LambdaQueryWrapper<IoApply>()
                .eq(IoApply::getOrderType, orderType)
                .likeRight(IoApply::getApplyNo, prefix)
                .orderByDesc(IoApply::getId)
                .last("limit 1"), false);
        long nextNumber = 1L;
        if (lastApply != null) {
            nextNumber = parseApplyNoNumber(lastApply.getApplyNo(), prefix) + 1;
        }
        return prefix + String.format("%0" + APPLY_NO_DIGIT_LENGTH + "d", nextNumber);
    }

    /**
     * 从历史申请单号中解析流水号部分。
     */
    private long parseApplyNoNumber(String applyNo, String prefix) {
        if (applyNo == null || !applyNo.startsWith(prefix) || applyNo.length() <= prefix.length()) {
            throw new BaseException("历史申请单号格式不正确");
        }
        String numberPart = applyNo.substring(prefix.length());
        try {
            return Long.parseLong(numberPart);
        } catch (NumberFormatException ex) {
            throw new BaseException("历史申请单号格式不正确");
        }
    }

    /**
     * 校验申请单是否允许修改。
     */
    private void validateApplyCanUpdate(IoApply ioApply) {
        if (isApproved(ioApply)) {
            throw new BaseException(buildBizLabel(ioApply.getOrderType()) + "申请已审批，无法修改");
        }
    }

    /**
     * 校验申请单是否允许删除。
     */
    private void validateApplyCanDelete(IoApply ioApply) {
        if (isApproved(ioApply)) {
            throw new BaseException(buildBizLabel(ioApply.getOrderType()) + "申请已审批，无法删除");
        }
    }

    /**
     * 校验申请单是否允许取消审批。
     */
    private void validateApplyCanCancelApprove(IoApply ioApply) {
        // 已生成出入库单的申请必须先删除单据，再允许撤销审批。
        long ioOrderCount = ioOrderMapper.selectCount(
                new LambdaQueryWrapper<IoOrder>().eq(IoOrder::getApplyId, ioApply.getId()));
        if (ioOrderCount > 0) {
            String bizLabel = buildBizLabel(ioApply.getOrderType());
            throw new BaseException(bizLabel + "申请已生成" + bizLabel + "单，请先删除" + bizLabel + "单后再取消审批");
        }
    }

    /**
     * 判断申请单是否已审批。
     */
    private boolean isApproved(IoApply ioApply) {
        return ApproveStatusEnum.APPROVED.matches(ioApply.getApproveStatus());
    }

    /**
     * 构建申请单分页查询条件。
     */
    private LambdaQueryWrapper<IoApply> buildWrapper(IoApplyQuery query) {
        return new LambdaQueryWrapper<IoApply>()
                .like(StrUtil.isNotBlank(query.getApplyNo()), IoApply::getApplyNo, query.getApplyNo())
                .eq(query.getOrderType() != null, IoApply::getOrderType, query.getOrderType())
                .ge(query.getApplyDateStart() != null, IoApply::getApplyDate, query.getApplyDateStart())
                .le(query.getApplyDateEnd() != null, IoApply::getApplyDate, query.getApplyDateEnd())
                .eq(query.getDeliverymanId() != null, IoApply::getDeliverymanId, query.getDeliverymanId())
                .eq(query.getCustomerId() != null, IoApply::getCustomerId, query.getCustomerId())
                .eq(query.getWarehouseId() != null, IoApply::getWarehouseId, query.getWarehouseId())
                .eq(query.getSalesmanId() != null, IoApply::getSalesmanId, query.getSalesmanId())
                .eq(query.getIoTypeId() != null, IoApply::getIoTypeId, query.getIoTypeId())
                .eq(query.getApproveStatus() != null, IoApply::getApproveStatus, query.getApproveStatus())
                .eq(query.getIoStatus() != null, IoApply::getIoStatus, query.getIoStatus())
                .orderByDesc(IoApply::getId);
    }

    /**
     * 批量查询并组装申请明细。
     */
    private Map<Long, List<IoApplyDetailVO>> buildDetailMap(List<Long> applyIds) {
        if (applyIds == null || applyIds.isEmpty()) {
            return Collections.emptyMap();
        }
        List<IoApplyDetail> detailList = ioApplyDetailService.list(new LambdaQueryWrapper<IoApplyDetail>()
                .in(IoApplyDetail::getApplyId, applyIds)
                .orderByAsc(IoApplyDetail::getId));
        if (detailList.isEmpty()) {
            return Collections.emptyMap();
        }

        // 批量拉商品详情映射，避免逐条明细回填商品展示信息。
        Set<Long> productIds = detailList.stream()
                .map(IoApplyDetail::getProductId)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        Map<Long, ProductPageVO> productMap = productIds.isEmpty()
                ? Collections.emptyMap()
                : productService.getDetailMapByIds(productIds);

        return detailList.stream()
                .map(detail -> buildDetailVO(detail, productMap.get(detail.getProductId())))
                .collect(Collectors.groupingBy(IoApplyDetailVO::getApplyId, LinkedHashMap::new, Collectors.toList()));
    }

    /**
     * 组装单条申请明细展示对象。
     */
    private IoApplyDetailVO buildDetailVO(IoApplyDetail detail, ProductPageVO product) {
        IoApplyDetailVO vo = new IoApplyDetailVO();
        vo.setId(detail.getId())
                .setApplyId(detail.getApplyId())
                .setProductId(detail.getProductId())
                .setQty(detail.getQty())
                .setRemark(detail.getRemark());
        vo.setCreateTime(detail.getCreateTime())
                .setUpdateTime(detail.getUpdateTime())
                .setCreateBy(detail.getCreateBy())
                .setUpdateBy(detail.getUpdateBy());
        vo.setProduct(product);
        return vo;
    }

    /**
     * 组装申请单分页展示对象。
     */
    private IoApplyPageVO buildPageVO(IoApply ioApply, Map<Long, Deliveryman> deliverymanMap,
                                      Map<Long, Customer> customerMap, Map<Long, Warehouse> warehouseMap,
                                      Map<Long, Salesman> salesmanMap,
                                      Map<Long, IoType> ioTypeMap,
                                      List<IoApplyDetailVO> detailList) {
        IoApplyPageVO vo = new IoApplyPageVO();
        vo.setId(ioApply.getId())
                .setApplyNo(ioApply.getApplyNo())
                .setOrderType(ioApply.getOrderType())
                .setApplyDate(ioApply.getApplyDate())
                .setDeliverymanId(ioApply.getDeliverymanId())
                .setCustomerId(ioApply.getCustomerId())
                .setWarehouseId(ioApply.getWarehouseId())
                .setSalesmanId(ioApply.getSalesmanId())
                .setIoTypeId(ioApply.getIoTypeId())
                .setRemark(ioApply.getRemark())
                .setApproveStatus(ioApply.getApproveStatus())
                .setIoStatus(ioApply.getIoStatus())
                .setApprovedTime(ioApply.getApprovedTime());
        vo.setCreateTime(ioApply.getCreateTime())
                .setUpdateTime(ioApply.getUpdateTime())
                .setCreateBy(ioApply.getCreateBy())
                .setUpdateBy(ioApply.getUpdateBy());
        Deliveryman deliveryman = deliverymanMap.get(ioApply.getDeliverymanId());
        Customer customer = customerMap.get(ioApply.getCustomerId());
        Warehouse warehouse = warehouseMap.get(ioApply.getWarehouseId());
        Salesman salesman = salesmanMap.get(ioApply.getSalesmanId());
        IoType ioType = ioTypeMap.get(ioApply.getIoTypeId());
        // 展示层字段统一在这里派生，避免控制层重复判断枚举和字典名称。
        vo.setOrderTypeName(buildBizLabel(ioApply.getOrderType()))
                .setDeliveryman(deliveryman)
                .setCustomer(customer)
                .setWarehouse(warehouse)
                .setSalesman(salesman)
                .setIoTypeName(ioType == null ? null : ioType.getName())
                .setApproveStatusName(ApproveStatusEnum.getDesc(ioApply.getApproveStatus()))
                .setIoStatusName(buildIoStatusName(ioApply.getOrderType(), ioApply.getIoStatus()))
                .setDetailList(detailList == null ? List.of() : detailList);
        return vo;
    }

    /**
     * 返回单据类型对应的业务名称。
     */
    private String buildBizLabel(Integer orderType) {
        return IoBizTypeEnum.getDesc(orderType);
    }

    /**
     * 返回申请单维度的出入库状态名称。
     */
    private String buildIoStatusName(Integer orderType, Integer ioStatus) {
        return IoStatusEnum.getApplyDesc(ioStatus, orderType);
    }

}
