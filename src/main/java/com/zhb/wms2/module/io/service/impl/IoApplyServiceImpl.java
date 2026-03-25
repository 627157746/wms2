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

@Service
@RequiredArgsConstructor
public class IoApplyServiceImpl extends ServiceImpl<IoApplyMapper, IoApply> implements IoApplyService {

    private static final String INBOUND_APPLY_PREFIX = "RS";
    private static final String OUTBOUND_APPLY_PREFIX = "CS";
    private static final int APPLY_NO_DIGIT_LENGTH = 6;

    private final IoOrderMapper ioOrderMapper;
    private final ProductMapper productMapper;
    private final ProductService productService;
    private final BaseDictMapService baseDictMapService;
    private final IoApplyDetailService ioApplyDetailService;

    @Override
    public IPage<IoApplyPageVO> pageQuery(IoApplyQuery query) {
        IPage<IoApply> page = page(new Page<>(query.getCurrent(), query.getSize()), buildWrapper(query));
        List<IoApply> recordList = page.getRecords();
        if (recordList.isEmpty()) {
            return new Page<>(query.getCurrent(), query.getSize());
        }

        BaseDictMapDTO dictMap = baseDictMapService.getBaseDictMap();
        Map<Long, Deliveryman> deliverymanMap = dictMap.getDeliverymanMap() == null
                ? Map.of() : dictMap.getDeliverymanMap();
        Map<Long, Customer> customerMap = dictMap.getCustomerMap() == null
                ? Map.of() : dictMap.getCustomerMap();
        Map<Long, Salesman> salesmanMap = dictMap.getSalesmanMap() == null
                ? Map.of() : dictMap.getSalesmanMap();
        Map<Long, IoType> ioTypeMap = dictMap.getIoTypeMap() == null
                ? Map.of() : dictMap.getIoTypeMap();
        Map<Long, List<IoApplyDetailVO>> detailMap = buildDetailMap(recordList.stream().map(IoApply::getId).toList());
        return page.convert(ioApply -> buildPageVO(ioApply, deliverymanMap, customerMap, salesmanMap, ioTypeMap,
                detailMap.get(ioApply.getId())));
    }

    @Override
    public IoApplyPageVO getDetailById(Long id) {
        IoApply ioApply = getById(id);
        if (ioApply == null) {
            throw new BaseException("出入库申请不存在");
        }

        BaseDictMapDTO dictMap = baseDictMapService.getBaseDictMap();
        Map<Long, Deliveryman> deliverymanMap = dictMap.getDeliverymanMap() == null
                ? Map.of() : dictMap.getDeliverymanMap();
        Map<Long, Customer> customerMap = dictMap.getCustomerMap() == null
                ? Map.of() : dictMap.getCustomerMap();
        Map<Long, Salesman> salesmanMap = dictMap.getSalesmanMap() == null
                ? Map.of() : dictMap.getSalesmanMap();
        Map<Long, IoType> ioTypeMap = dictMap.getIoTypeMap() == null
                ? Map.of() : dictMap.getIoTypeMap();
        Map<Long, List<IoApplyDetailVO>> detailMap = buildDetailMap(List.of(id));
        return buildPageVO(ioApply, deliverymanMap, customerMap, salesmanMap, ioTypeMap, detailMap.get(id));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveApply(IoApplyCreateDTO dto) {
        validateBizData(dto);

        IoApply ioApply = new IoApply();
        ioApply.setApplyNo(generateApplyNo(dto.getOrderType()));
        ioApply.setOrderType(dto.getOrderType());
        ioApply.setApplyDate(dto.getApplyDate());
        ioApply.setDeliverymanId(dto.getDeliverymanId());
        ioApply.setCustomerId(
                IoBizTypeEnum.OUTBOUND.matches(dto.getOrderType()) ? dto.getCustomerId() : null);
        ioApply.setSalesmanId(
                IoBizTypeEnum.OUTBOUND.matches(dto.getOrderType()) ? dto.getSalesmanId() : null);
        ioApply.setIoTypeId(dto.getIoTypeId());
        ioApply.setRemark(dto.getRemark());
        ioApply.setApproveStatus(ApproveStatusEnum.UNAPPROVED.getCode());
        ioApply.setIoStatus(IoStatusEnum.PENDING.getCode());
        ioApply.setApprovedTime(null);
        if (!save(ioApply)) {
            throw new BaseException("出入库申请新增失败");
        }
        saveApplyDetails(ioApply.getId(), dto.getDetailList());
        return ioApply.getId();
    }

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
            ioApply.setApplyNo(generateApplyNo(dto.getOrderType()));
        }
        ioApply.setOrderType(dto.getOrderType());
        ioApply.setApplyDate(dto.getApplyDate());
        ioApply.setDeliverymanId(dto.getDeliverymanId());
        ioApply.setCustomerId(
                IoBizTypeEnum.OUTBOUND.matches(dto.getOrderType()) ? dto.getCustomerId() : null);
        ioApply.setSalesmanId(
                IoBizTypeEnum.OUTBOUND.matches(dto.getOrderType()) ? dto.getSalesmanId() : null);
        ioApply.setIoTypeId(dto.getIoTypeId());
        ioApply.setRemark(dto.getRemark());
        if (!updateById(ioApply)) {
            throw new BaseException("出入库申请不存在");
        }

        ioApplyDetailService.remove(new LambdaQueryWrapper<IoApplyDetail>().eq(IoApplyDetail::getApplyId, dto.getId()));
        saveApplyDetails(dto.getId(), dto.getDetailList());
    }

    @Override
    public void approveById(Long id) {
        IoApply ioApply = getById(id);
        if (ioApply == null) {
            throw new BaseException("出入库申请不存在");
        }
        if (isApproved(ioApply)) {
            throw new BaseException(buildBizLabel(ioApply.getOrderType()) + "申请已审批，请勿重复审批");
        }
        ioApply.setApproveStatus(ApproveStatusEnum.APPROVED.getCode());
        ioApply.setApprovedTime(LocalDateTime.now());
        if (!updateById(ioApply)) {
            throw new BaseException("出入库申请不存在");
        }
    }

    @Override
    public void cancelApproveById(Long id) {
        IoApply ioApply = getById(id);
        if (ioApply == null) {
            throw new BaseException("出入库申请不存在");
        }
        if (!isApproved(ioApply)) {
            throw new BaseException(buildBizLabel(ioApply.getOrderType()) + "申请未审批，无需取消审批");
        }
        validateApplyCanCancelApprove(ioApply);
        ioApply.setApproveStatus(ApproveStatusEnum.UNAPPROVED.getCode());
        ioApply.setApprovedTime(null);
        if (!updateById(ioApply)) {
            throw new BaseException("出入库申请不存在");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeByIdChecked(Long id) {
        IoApply ioApply = getById(id);
        if (ioApply == null) {
            throw new BaseException("出入库申请不存在");
        }
        validateApplyCanDelete(ioApply);
        ioApplyDetailService.remove(new LambdaQueryWrapper<IoApplyDetail>().eq(IoApplyDetail::getApplyId, id));
        if (!removeById(id)) {
            throw new BaseException("出入库申请不存在");
        }
    }

    private void validateBizData(IoApplyCreateDTO dto) {
        validateDeliveryman(dto.getDeliverymanId(), dto.getOrderType());
        validateIoType(dto.getIoTypeId(), dto.getOrderType());
        if (IoBizTypeEnum.OUTBOUND.matches(dto.getOrderType())) {
            if (dto.getCustomerId() == null) {
                throw new BaseException("出库申请客户不能为空");
            }
            validateCustomer(dto.getCustomerId());
            if (dto.getSalesmanId() == null) {
                throw new BaseException("出库申请业务员不能为空");
            }
            validateSalesman(dto.getSalesmanId());
        }
        validateProducts(dto.getDetailList());
    }

    private void validateDeliveryman(Long deliverymanId, Integer orderType) {
        BaseDictMapDTO dictMap = baseDictMapService.getBaseDictMap();
        Deliveryman deliveryman = dictMap.getDeliverymanMap() == null
                ? null
                : dictMap.getDeliverymanMap().get(deliverymanId);
        if (deliveryman == null) {
            throw new BaseException("送货员不存在");
        }
        Integer expectedScope = IoBizTypeEnum.INBOUND.matches(orderType)
                ? ScopeEnum.INBOUND.getCode()
                : ScopeEnum.OUTBOUND.getCode();
        if (!ScopeEnum.supportsBizType(deliveryman.getScope(), expectedScope)) {
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
    }

    private void validateSalesman(Long salesmanId) {
        BaseDictMapDTO dictMap = baseDictMapService.getBaseDictMap();
        Salesman salesman = dictMap.getSalesmanMap() == null
                ? null
                : dictMap.getSalesmanMap().get(salesmanId);
        if (salesman == null) {
            throw new BaseException("业务员不存在");
        }
    }

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

    private void validateProducts(List<IoApplyCreateDetailDTO> detailList) {
        Set<Long> productIds = new LinkedHashSet<>();
        for (IoApplyCreateDetailDTO detail : detailList) {
            productIds.add(detail.getProductId());
        }
        List<Product> productList = productMapper.selectBatchIds(productIds);
        if (productList.size() != productIds.size()) {
            throw new BaseException("申请明细中存在不存在的商品");
        }
    }

    private IoApplyDetail buildDetail(Long applyId, IoApplyCreateDetailDTO detailDTO) {
        IoApplyDetail detail = new IoApplyDetail();
        detail.setApplyId(applyId);
        detail.setProductId(detailDTO.getProductId());
        detail.setQty(detailDTO.getQty());
        return detail;
    }

    private void saveApplyDetails(Long applyId, List<IoApplyCreateDetailDTO> detailDTOList) {
        List<IoApplyDetail> detailList = detailDTOList.stream()
                .map(detailDTO -> buildDetail(applyId, detailDTO))
                .toList();
        if (!ioApplyDetailService.saveBatch(detailList)) {
            throw new BaseException("出入库申请明细新增失败");
        }
    }

    private String generateApplyNo(Integer orderType) {
        String prefix = IoBizTypeEnum.INBOUND.matches(orderType)
                ? INBOUND_APPLY_PREFIX
                : OUTBOUND_APPLY_PREFIX;
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

    private void validateApplyCanUpdate(IoApply ioApply) {
        if (isApproved(ioApply)) {
            throw new BaseException(buildBizLabel(ioApply.getOrderType()) + "申请已审批，无法修改");
        }
    }

    private void validateApplyCanDelete(IoApply ioApply) {
        if (isApproved(ioApply)) {
            throw new BaseException(buildBizLabel(ioApply.getOrderType()) + "申请已审批，无法删除");
        }
    }

    private void validateApplyCanCancelApprove(IoApply ioApply) {
        long ioOrderCount = ioOrderMapper.selectCount(
                new LambdaQueryWrapper<IoOrder>().eq(IoOrder::getApplyId, ioApply.getId()));
        if (ioOrderCount > 0) {
            String bizLabel = buildBizLabel(ioApply.getOrderType());
            throw new BaseException(bizLabel + "申请已生成" + bizLabel + "单，请先删除" + bizLabel + "单后再取消审批");
        }
    }

    private boolean isApproved(IoApply ioApply) {
        return ApproveStatusEnum.APPROVED.matches(ioApply.getApproveStatus());
    }

    private LambdaQueryWrapper<IoApply> buildWrapper(IoApplyQuery query) {
        return new LambdaQueryWrapper<IoApply>()
                .like(StrUtil.isNotBlank(query.getApplyNo()), IoApply::getApplyNo, query.getApplyNo())
                .eq(query.getOrderType() != null, IoApply::getOrderType, query.getOrderType())
                .ge(query.getApplyDateStart() != null, IoApply::getApplyDate, query.getApplyDateStart())
                .le(query.getApplyDateEnd() != null, IoApply::getApplyDate, query.getApplyDateEnd())
                .eq(query.getDeliverymanId() != null, IoApply::getDeliverymanId, query.getDeliverymanId())
                .eq(query.getCustomerId() != null, IoApply::getCustomerId, query.getCustomerId())
                .eq(query.getSalesmanId() != null, IoApply::getSalesmanId, query.getSalesmanId())
                .eq(query.getIoTypeId() != null, IoApply::getIoTypeId, query.getIoTypeId())
                .eq(query.getApproveStatus() != null, IoApply::getApproveStatus, query.getApproveStatus())
                .eq(query.getIoStatus() != null, IoApply::getIoStatus, query.getIoStatus())
                .orderByDesc(IoApply::getId);
    }

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

    private IoApplyDetailVO buildDetailVO(IoApplyDetail detail, ProductPageVO product) {
        IoApplyDetailVO vo = new IoApplyDetailVO();
        vo.setId(detail.getId());
        vo.setApplyId(detail.getApplyId());
        vo.setProductId(detail.getProductId());
        vo.setQty(detail.getQty());
        vo.setCreateTime(detail.getCreateTime());
        vo.setUpdateTime(detail.getUpdateTime());
        vo.setCreateBy(detail.getCreateBy());
        vo.setUpdateBy(detail.getUpdateBy());
        vo.setProduct(product);
        return vo;
    }

    private IoApplyPageVO buildPageVO(IoApply ioApply, Map<Long, Deliveryman> deliverymanMap,
                                      Map<Long, Customer> customerMap, Map<Long, Salesman> salesmanMap,
                                      Map<Long, IoType> ioTypeMap,
                                      List<IoApplyDetailVO> detailList) {
        IoApplyPageVO vo = new IoApplyPageVO();
        vo.setId(ioApply.getId());
        vo.setApplyNo(ioApply.getApplyNo());
        vo.setOrderType(ioApply.getOrderType());
        vo.setApplyDate(ioApply.getApplyDate());
        vo.setDeliverymanId(ioApply.getDeliverymanId());
        vo.setCustomerId(ioApply.getCustomerId());
        vo.setSalesmanId(ioApply.getSalesmanId());
        vo.setIoTypeId(ioApply.getIoTypeId());
        vo.setRemark(ioApply.getRemark());
        vo.setApproveStatus(ioApply.getApproveStatus());
        vo.setIoStatus(ioApply.getIoStatus());
        vo.setApprovedTime(ioApply.getApprovedTime());
        vo.setCreateTime(ioApply.getCreateTime());
        vo.setUpdateTime(ioApply.getUpdateTime());
        vo.setCreateBy(ioApply.getCreateBy());
        vo.setUpdateBy(ioApply.getUpdateBy());
        vo.setOrderTypeName(buildBizLabel(ioApply.getOrderType()));
        Deliveryman deliveryman = deliverymanMap.get(ioApply.getDeliverymanId());
        vo.setDeliveryman(deliveryman);
        Customer customer = customerMap.get(ioApply.getCustomerId());
        vo.setCustomer(customer);
        Salesman salesman = salesmanMap.get(ioApply.getSalesmanId());
        vo.setSalesman(salesman);
        IoType ioType = ioTypeMap.get(ioApply.getIoTypeId());
        vo.setIoTypeName(ioType == null ? null : ioType.getName());
        vo.setApproveStatusName(ApproveStatusEnum.getDesc(ioApply.getApproveStatus()));
        vo.setIoStatusName(buildIoStatusName(ioApply.getOrderType(), ioApply.getIoStatus()));
        vo.setDetailList(detailList == null ? List.of() : detailList);
        return vo;
    }

    private String buildBizLabel(Integer orderType) {
        return IoBizTypeEnum.getDesc(orderType);
    }

    private String buildIoStatusName(Integer orderType, Integer ioStatus) {
        return IoStatusEnum.getApplyDesc(ioStatus, orderType);
    }

}
