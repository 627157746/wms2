package com.zhb.wms2.module.base.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhb.wms2.common.enums.ScopeEnum;
import com.zhb.wms2.common.exception.BaseException;
import com.zhb.wms2.module.base.mapper.IoTypeMapper;
import com.zhb.wms2.module.base.model.dto.BaseSortUpdateDTO;
import com.zhb.wms2.module.base.model.entity.IoType;
import com.zhb.wms2.module.base.model.query.IoTypeQuery;
import com.zhb.wms2.module.base.service.IoTypeService;
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
 * IoTypeServiceImpl 服务实现
 *
 * @author zhb
 * @since 2026/3/26
 */
@Service
@RequiredArgsConstructor
public class IoTypeServiceImpl extends ServiceImpl<IoTypeMapper, IoType> implements IoTypeService {

    private final IoApplyService ioApplyService;
    private final IoOrderService ioOrderService;
    private final BaseDictMapStore baseDictMapStore;

    /**
     * 新增出入库类型并校验同范围名称唯一。
     */
    @Override
    public void saveChecked(IoType ioType) {
        // 出入库类型按适用范围做唯一控制，避免入库和出库重名带来歧义。
        validateNameUnique(ioType.getName(), ioType.getScope(), null);
        if (!super.save(ioType)) {
            throw new BaseException("出入库类型新增失败");
        }
        applyDefaultSortOrder(ioType.getId());
        // 类型字典被多个模块复用，保存后立即失效缓存。
        baseDictMapStore.clearIoTypeMap();
    }

    /**
     * 分页查询出入库类型。
     */
    @Override
    public IPage<IoType> pageQuery(IoTypeQuery query) {
        // 列表页只负责基础过滤，不在这里叠加业务类型换算逻辑。
        return page(new Page<>(query.getCurrent(), query.getSize()), buildWrapper(query));
    }

    /**
     * 按适用范围查询出入库类型，同时兼容“不限”范围。
     */
    @Override
    public List<IoType> listAllByScope(Integer scope) {
        LambdaQueryWrapper<IoType> wrapper = new LambdaQueryWrapper<IoType>()
                .orderByDesc(IoType::getSortOrder)
                .orderByDesc(IoType::getId);
        // “不限”范围类型同时对入库和出库可见。
        return list(wrapper.and(w -> w.in(IoType::getScope, ScopeEnum.COMMON.getCode(), scope)));
    }

    /**
     * 修改出入库类型并校验同范围名称唯一。
     */
    @Override
    public void updateByIdChecked(IoType ioType) {
        if (getById(ioType.getId()) == null) {
            throw new BaseException("出入库类型不存在");
        }
        // 修改时排除自身后再校验名称唯一。
        validateNameUnique(ioType.getName(), ioType.getScope(), ioType.getId());
        if (!updateById(ioType)) {
            throw new BaseException("出入库类型不存在");
        }
        // 修改后清缓存，保证申请单和出入库单校验读取到最新范围。
        baseDictMapStore.clearIoTypeMap();
    }

    /**
     * 批量修改出入库类型排序并清理字典缓存。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSortOrderBatch(List<BaseSortUpdateDTO> dtoList) {
        if (CollUtil.isEmpty(dtoList)) {
            throw new BaseException("排序列表不能为空");
        }
        Set<Long> idSet = new HashSet<>();
        List<IoType> updateList = dtoList.stream().map(dto -> {
            if (!idSet.add(dto.getId())) {
                throw new BaseException("出入库类型ID不能重复");
            }
            IoType ioType = new IoType();
            ioType.setId(dto.getId());
            ioType.setSortOrder(dto.getSortOrder());
            return ioType;
        }).toList();
        long count = count(new LambdaQueryWrapper<IoType>().in(IoType::getId, idSet));
        if (count != idSet.size()) {
            throw new BaseException("存在不存在的出入库类型");
        }
        if (!updateBatchById(updateList)) {
            throw new BaseException("出入库类型排序修改失败");
        }
        baseDictMapStore.clearIoTypeMap();
    }

    /**
     * 删除出入库类型前校验是否被申请单或出入库单引用。
     */
    @Override
    public void removeByIdChecked(Long id) {
        IoType ioType = getById(id);
        if (ioType == null) {
            throw new BaseException("出入库类型不存在");
        }
        // 出入库类型被申请和单据直接引用，删除前必须同时校验两侧。
        long applyCount = ioApplyService.count(
                new LambdaQueryWrapper<IoApply>()
                        .eq(IoApply::getIoTypeId, id));
        if (applyCount > 0) {
            throw new BaseException("该出入库类型已被出入库申请使用，无法删除");
        }
        long orderCount = ioOrderService.count(
                new LambdaQueryWrapper<IoOrder>().eq(IoOrder::getIoTypeId, id));
        if (orderCount > 0) {
            throw new BaseException("该出入库类型已被出入库单使用，无法删除");
        }
        if (!removeById(id)) {
            throw new BaseException("出入库类型不存在");
        }
        // 删除后同步失效缓存。
        baseDictMapStore.clearIoTypeMap();
    }

    /**
     * 构建出入库类型分页查询条件。
     */
    private LambdaQueryWrapper<IoType> buildWrapper(IoTypeQuery query) {
        // 查询层只拼装数据库条件，不做额外数据转换。
        return new LambdaQueryWrapper<IoType>()
                .like(StrUtil.isNotBlank(query.getName()), IoType::getName, query.getName())
                .eq(query.getScope() != null, IoType::getScope, query.getScope())
                .orderByDesc(IoType::getSortOrder)
                .orderByDesc(IoType::getId);
    }

    /**
     * 校验同适用范围下名称唯一。
     */
    private void validateNameUnique(String name, Integer scope, Long excludeId) {
        // 编辑场景排除当前记录，避免自身命中唯一校验。
        LambdaQueryWrapper<IoType> wrapper = new LambdaQueryWrapper<IoType>()
                .eq(IoType::getName, name)
                .eq(IoType::getScope, scope)
                .ne(excludeId != null, IoType::getId, excludeId);
        if (count(wrapper) > 0) {
            throw new BaseException("同适用范围下名称已存在");
        }
    }

    /**
     * 新增后按主键回填默认排序。
     */
    private void applyDefaultSortOrder(Long id) {
        IoType updateEntity = new IoType();
        updateEntity.setId(id);
        updateEntity.setSortOrder(Math.toIntExact(id));
        if (!updateById(updateEntity)) {
            throw new BaseException("出入库类型默认排序回填失败");
        }
    }
}
