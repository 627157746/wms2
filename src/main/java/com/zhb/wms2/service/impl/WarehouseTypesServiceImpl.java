package com.zhb.wms2.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhb.wms2.common.exception.BaseException;
import com.zhb.wms2.mapper.WarehouseTypesMapper;
import com.zhb.wms2.model.WarehouseTypes;
import com.zhb.wms2.model.dto.WarehouseTypesQuery;
import com.zhb.wms2.service.WarehouseTypesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 出入库类型服务实现类
 */
@Service
@RequiredArgsConstructor
public class WarehouseTypesServiceImpl extends ServiceImpl<WarehouseTypesMapper, WarehouseTypes> implements WarehouseTypesService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addWarehouseTypes(WarehouseTypes warehouseTypes) {
        // 1. 业务规则检查
        if (isTypeNameExists(warehouseTypes.getTypeName(), warehouseTypes.getTypeCategory(), null)) {
            throw new BaseException("该分类下类型名称已存在");
        }

        // 2. 保存数据
        save(warehouseTypes);

        return warehouseTypes.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateWarehouseTypes(WarehouseTypes warehouseTypes) {
        // 1. 业务规则检查
        if (isTypeNameExists(warehouseTypes.getTypeName(), warehouseTypes.getTypeCategory(), warehouseTypes.getId())) {
            throw new BaseException("该分类下类型名称已存在");
        }

        // 2. 更新数据
        updateById(warehouseTypes);
    }

    @Override
    public IPage<WarehouseTypes> queryPage(WarehouseTypesQuery query) {
        // 1. 构建查询条件
        LambdaQueryWrapper<WarehouseTypes> wrapper = buildQueryWrapper(query);

        // 2. 执行分页查询
        Page<WarehouseTypes> page = new Page<>(query.getCurrent().intValue(), query.getSize().intValue());
        return page(page, wrapper);
    }


    private boolean isTypeNameExists(String typeName, Integer typeCategory, Long excludeId) {
        if (StrUtil.isBlank(typeName)) {
            return false;
        }

        LambdaQueryWrapper<WarehouseTypes> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WarehouseTypes::getTypeName, typeName);

        if (typeCategory != null) {
            wrapper.eq(WarehouseTypes::getTypeCategory, typeCategory);
        }

        if (excludeId != null) {
            wrapper.ne(WarehouseTypes::getId, excludeId);
        }

        return count(wrapper) > 0;
    }


    /**
     * 构建查询条件
     */
    private LambdaQueryWrapper<WarehouseTypes> buildQueryWrapper(WarehouseTypesQuery query) {
        LambdaQueryWrapper<WarehouseTypes> wrapper = new LambdaQueryWrapper<>();

        // 模糊查询
        wrapper.like(StrUtil.isNotBlank(query.getTypeName()), WarehouseTypes::getTypeName, query.getTypeName());

        // 精确查询
        wrapper.eq(query.getTypeCategory() != null, WarehouseTypes::getTypeCategory, query.getTypeCategory());

        // 排序
        wrapper.orderByAsc(WarehouseTypes::getSortOrder);

        return wrapper;
    }
}
