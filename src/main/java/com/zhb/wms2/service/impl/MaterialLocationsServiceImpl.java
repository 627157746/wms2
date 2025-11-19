package com.zhb.wms2.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhb.wms2.common.exception.BaseException;
import com.zhb.wms2.mapper.MaterialLocationsMapper;
import com.zhb.wms2.model.MaterialLocations;
import com.zhb.wms2.model.dto.MaterialLocationsQuery;
import com.zhb.wms2.service.MaterialLocationsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 物料位置信息服务实现类
 */
@Service
@RequiredArgsConstructor
public class MaterialLocationsServiceImpl extends ServiceImpl<MaterialLocationsMapper, MaterialLocations> implements MaterialLocationsService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addMaterialLocation(MaterialLocations materialLocation) {
        // 1. 自动生成物料位编码
        materialLocation.generateLocationCode();

        // 2. 业务规则检查
        if (isLocationCodeExists(materialLocation.getLocationCode(), null)) {
            throw new BaseException("物料位编码已存在");
        }

        // 3. 保存数据
        save(materialLocation);

        return materialLocation.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMaterialLocation(MaterialLocations materialLocation) {
        // 1. 检查物料位是否存在
        MaterialLocations existLocation = getById(materialLocation.getId());
        if (existLocation == null) {
            throw new BaseException("物料位信息不存在");
        }

        // 2. 自动生成物料位编码（如果rowNo或sectionNo发生了变化）
        if (!existLocation.getRowNo().equals(materialLocation.getRowNo()) ||
            !existLocation.getSectionNo().equals(materialLocation.getSectionNo())) {
            materialLocation.generateLocationCode();
        }

        // 3. 业务规则检查
        if (isLocationCodeExists(materialLocation.getLocationCode(), materialLocation.getId())) {
            throw new BaseException("物料位编码已存在");
        }

        // 4. 更新数据
        updateById(materialLocation);
    }

    @Override
    public IPage<MaterialLocations> queryPage(MaterialLocationsQuery query) {
        // 1. 构建查询条件
        LambdaQueryWrapper<MaterialLocations> wrapper = buildQueryWrapper(query);

        // 2. 执行分页查询
        Page<MaterialLocations> page = new Page<>(query.getCurrent().intValue(), query.getSize().intValue());
        return page(page, wrapper);
    }

    /**
     * 检查物料位编码是否存在
     */
    public boolean isLocationCodeExists(String locationCode, Long excludeId) {
        if (StrUtil.isBlank(locationCode)) {
            return false;
        }

        LambdaQueryWrapper<MaterialLocations> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MaterialLocations::getLocationCode, locationCode);

        if (excludeId != null) {
            wrapper.ne(MaterialLocations::getId, excludeId);
        }

        return count(wrapper) > 0;
    }

    /**
     * 构建查询条件
     */
    private LambdaQueryWrapper<MaterialLocations> buildQueryWrapper(MaterialLocationsQuery query) {
        LambdaQueryWrapper<MaterialLocations> wrapper = new LambdaQueryWrapper<>();

        // 精确查询
        wrapper.eq(StrUtil.isNotBlank(query.getLocationCode()), MaterialLocations::getLocationCode, query.getLocationCode())
               .eq(StrUtil.isNotBlank(query.getRowNo()), MaterialLocations::getRowNo, query.getRowNo())
               .eq(StrUtil.isNotBlank(query.getSectionNo()), MaterialLocations::getSectionNo, query.getSectionNo());

        // 模糊查询
        wrapper.like(StrUtil.isNotBlank(query.getLocationCode()), MaterialLocations::getLocationCode, query.getLocationCode())
               .like(StrUtil.isNotBlank(query.getRowNo()), MaterialLocations::getRowNo, query.getRowNo())
               .like(StrUtil.isNotBlank(query.getSectionNo()), MaterialLocations::getSectionNo, query.getSectionNo());

        // 排序
        wrapper.orderByDesc(MaterialLocations::getCreateTime);

        return wrapper;
    }
}
