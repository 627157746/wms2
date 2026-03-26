package com.zhb.wms2.module.base.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhb.wms2.common.enums.ScopeEnum;
import com.zhb.wms2.common.exception.BaseException;
import com.zhb.wms2.module.base.mapper.IoTypeMapper;
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

import java.util.List;

@Service
@RequiredArgsConstructor
public class IoTypeServiceImpl extends ServiceImpl<IoTypeMapper, IoType> implements IoTypeService {

    private final IoApplyService ioApplyService;
    private final IoOrderService ioOrderService;
    private final BaseDictMapStore baseDictMapStore;

    @Override
    public void saveChecked(IoType ioType) {
        validateNameUnique(ioType.getName(), ioType.getScope(), null);
        if (!super.save(ioType)) {
            throw new BaseException("出入库类型新增失败");
        }
        baseDictMapStore.clearIoTypeMap();
    }

    @Override
    public IPage<IoType> pageQuery(IoTypeQuery query) {
        return page(new Page<>(query.getCurrent(), query.getSize()), buildWrapper(query));
    }

    @Override
    public List<IoType> listAllByScope(Integer scope) {
        LambdaQueryWrapper<IoType> wrapper = new LambdaQueryWrapper<IoType>()
                .orderByDesc(IoType::getId);
        return list(wrapper.and(w -> w.in(IoType::getScope, ScopeEnum.COMMON.getCode(), scope)));
    }

    @Override
    public void updateByIdChecked(IoType ioType) {
        if (getById(ioType.getId()) == null) {
            throw new BaseException("出入库类型不存在");
        }
        validateNameUnique(ioType.getName(), ioType.getScope(), ioType.getId());
        if (!updateById(ioType)) {
            throw new BaseException("出入库类型不存在");
        }
        baseDictMapStore.clearIoTypeMap();
    }

    @Override
    public void removeByIdChecked(Long id) {
        IoType ioType = getById(id);
        if (ioType == null) {
            throw new BaseException("出入库类型不存在");
        }
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
        baseDictMapStore.clearIoTypeMap();
    }

    private LambdaQueryWrapper<IoType> buildWrapper(IoTypeQuery query) {
        return new LambdaQueryWrapper<IoType>()
                .like(StrUtil.isNotBlank(query.getName()), IoType::getName, query.getName())
                .eq(query.getScope() != null, IoType::getScope, query.getScope())
                .orderByDesc(IoType::getId);
    }

    private void validateNameUnique(String name, Integer scope, Long excludeId) {
        LambdaQueryWrapper<IoType> wrapper = new LambdaQueryWrapper<IoType>()
                .eq(IoType::getName, name)
                .eq(IoType::getScope, scope)
                .ne(excludeId != null, IoType::getId, excludeId);
        if (count(wrapper) > 0) {
            throw new BaseException("同适用范围下名称已存在");
        }
    }
}
