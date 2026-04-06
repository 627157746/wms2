package com.zhb.wms2.module.base.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhb.wms2.common.enums.IoBizTypeEnum;
import com.zhb.wms2.common.enums.ScopeEnum;
import com.zhb.wms2.common.exception.BaseException;
import com.zhb.wms2.module.base.mapper.DeliverymanMapper;
import com.zhb.wms2.module.base.model.dto.BaseSortUpdateDTO;
import com.zhb.wms2.module.base.model.entity.Deliveryman;
import com.zhb.wms2.module.base.model.query.DeliverymanQuery;
import com.zhb.wms2.module.base.service.DeliverymanService;
import com.zhb.wms2.module.base.service.support.BaseDictMapStore;
import com.zhb.wms2.module.io.model.entity.IoApply;
import com.zhb.wms2.module.io.model.entity.IoOrder;
import com.zhb.wms2.module.io.service.IoApplyService;
import com.zhb.wms2.module.io.service.IoOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * DeliverymanServiceImpl 服务实现
 *
 * @author zhb
 * @since 2026/3/26
 */
@Service
@RequiredArgsConstructor
public class DeliverymanServiceImpl extends ServiceImpl<DeliverymanMapper, Deliveryman> implements DeliverymanService {

    private final IoApplyService ioApplyService;
    private final IoOrderService ioOrderService;
    private final BaseDictMapStore baseDictMapStore;

    /**
     * 新增送货员并清理字典缓存。
     */
    @Override
    public void saveChecked(Deliveryman deliveryman) {
        if (!super.save(deliveryman)) {
            throw new BaseException("送货员新增失败");
        }
        applyDefaultSortOrder(deliveryman.getId());
        // 送货员属于字典缓存数据，新增后立即清理缓存。
        baseDictMapStore.clearDeliverymanMap();
    }

    /**
     * 分页查询送货员。
     */
    @Override
    public IPage<Deliveryman> pageQuery(DeliverymanQuery query) {
        // 分页只做基础查询，范围适配逻辑交给业务使用方判断。
        LambdaQueryWrapper<Deliveryman> wrapper = buildWrapper(query);
        return page(new Page<>(query.getCurrent(), query.getSize()), wrapper);
    }

    /**
     * 按适用范围查询送货员，同时兼容“不限”范围。
     */
    @Override
    public List<Deliveryman> listAllByScope(Integer scope) {
        LambdaQueryWrapper<Deliveryman> wrapper = new LambdaQueryWrapper<Deliveryman>()
                .orderByDesc(Deliveryman::getSortOrder)
                .orderByDesc(Deliveryman::getId);
        // 同时返回“不限”范围数据，保证入库/出库下拉框都能复用。
        return list(wrapper.and(w -> w.in(Deliveryman::getScope, ScopeEnum.COMMON.getCode(), scope)));
    }

    /**
     * 修改送货员并清理字典缓存。
     */
    @Override
    public void updateByIdChecked(Deliveryman deliveryman) {
        if (!updateById(deliveryman)) {
            throw new BaseException("送货员不存在");
        }
        // 修改后清缓存，避免下游单据继续命中过期送货员信息。
        baseDictMapStore.clearDeliverymanMap();
    }

    /**
     * 批量修改送货员排序并清理字典缓存。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSortOrderBatch(List<BaseSortUpdateDTO> dtoList) {
        if (CollUtil.isEmpty(dtoList)) {
            throw new BaseException("排序列表不能为空");
        }
        Set<Long> idSet = new HashSet<>();
        List<Deliveryman> updateList = dtoList.stream().map(dto -> {
            if (!idSet.add(dto.getId())) {
                throw new BaseException("送货员ID不能重复");
            }
            Deliveryman deliveryman = new Deliveryman();
            deliveryman.setId(dto.getId());
            deliveryman.setSortOrder(dto.getSortOrder());
            return deliveryman;
        }).toList();
        long count = count(new LambdaQueryWrapper<Deliveryman>().in(Deliveryman::getId, idSet));
        if (count != idSet.size()) {
            throw new BaseException("存在不存在的送货员");
        }
        if (!updateBatchById(updateList)) {
            throw new BaseException("送货员排序修改失败");
        }
        baseDictMapStore.clearDeliverymanMap();
    }

    /**
     * 删除送货员前校验是否被申请单或出入库单引用。
     */
    @Override
    public void removeByIdChecked(Long id) {
        // 送货员会被申请和出入库单同时引用，删除前要把两条链路都拦住。
        long inboundApplyCount = ioApplyService.count(
                new LambdaQueryWrapper<IoApply>()
                        .eq(IoApply::getDeliverymanId, id)
                        .eq(IoApply::getOrderType, IoBizTypeEnum.INBOUND.getCode()));
        if (inboundApplyCount > 0) {
            throw new BaseException("该送货员已被入库申请使用，无法删除");
        }
        long outboundApplyCount = ioApplyService.count(
                new LambdaQueryWrapper<IoApply>()
                        .eq(IoApply::getDeliverymanId, id)
                        .eq(IoApply::getOrderType, IoBizTypeEnum.OUTBOUND.getCode()));
        if (outboundApplyCount > 0) {
            throw new BaseException("该送货员已被出库申请使用，无法删除");
        }
        long ioOrderCount = ioOrderService.count(
                new LambdaQueryWrapper<IoOrder>().eq(IoOrder::getDeliverymanId, id));
        if (ioOrderCount > 0) {
            throw new BaseException("该送货员已被出入库单使用，无法删除");
        }
        if (!removeById(id)) {
            throw new BaseException("送货员不存在");
        }
        // 删除后清缓存，保持基础资料映射一致。
        baseDictMapStore.clearDeliverymanMap();
    }

    /**
     * 构建送货员分页查询条件。
     */
    private LambdaQueryWrapper<Deliveryman> buildWrapper(DeliverymanQuery query) {
        // 列表页只支持名称、电话和适用范围过滤。
        return new LambdaQueryWrapper<Deliveryman>()
                .like(StrUtil.isNotBlank(query.getName()), Deliveryman::getName, query.getName())
                .like(StrUtil.isNotBlank(query.getPhone()), Deliveryman::getPhone, query.getPhone())
                .eq(query.getScope() != null, Deliveryman::getScope, query.getScope())
                .orderByDesc(Deliveryman::getSortOrder)
                .orderByDesc(Deliveryman::getId);
    }

    /**
     * 新增后按主键回填默认排序。
     */
    private void applyDefaultSortOrder(Long id) {
        Deliveryman updateEntity = new Deliveryman();
        updateEntity.setId(id);
        updateEntity.setSortOrder(Math.toIntExact(id));
        if (!updateById(updateEntity)) {
            throw new BaseException("送货员默认排序回填失败");
        }
    }
}
