package com.zhb.wms2.module.base.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhb.wms2.common.enums.IoBizTypeEnum;
import com.zhb.wms2.common.exception.BaseException;
import com.zhb.wms2.module.base.mapper.WarehouseMapper;
import com.zhb.wms2.module.base.model.entity.Warehouse;
import com.zhb.wms2.module.base.model.query.WarehouseQuery;
import com.zhb.wms2.module.base.service.WarehouseService;
import com.zhb.wms2.module.base.service.support.BaseDictMapStore;
import com.zhb.wms2.module.io.model.entity.IoApply;
import com.zhb.wms2.module.io.model.entity.IoOrder;
import com.zhb.wms2.module.io.service.IoApplyService;
import com.zhb.wms2.module.io.service.IoOrderService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * WarehouseServiceImpl 服务实现
 *
 * @author zhb
 * @since 2026/3/27
 */
@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl extends ServiceImpl<WarehouseMapper, Warehouse> implements WarehouseService {

    private final IoApplyService ioApplyService;
    private final IoOrderService ioOrderService;
    private final BaseDictMapStore baseDictMapStore;

    /**
     * 新增仓库并校验名称唯一。
     */
    @Override
    public void saveChecked(Warehouse warehouse) {
        // 仓库名称用于基础资料选择，新增前先保证唯一。
        validateNameUnique(warehouse.getName(), null);
        if (!super.save(warehouse)) {
            throw new BaseException("仓库新增失败");
        }
        // 仓库字典变更后立即清缓存，避免下游继续读取旧数据。
        baseDictMapStore.clearWarehouseMap();
    }

    /**
     * 分页查询仓库。
     */
    @Override
    public IPage<Warehouse> pageQuery(WarehouseQuery query) {
        // 仓库分页仅按名称模糊过滤。
        LambdaQueryWrapper<Warehouse> wrapper = buildWrapper(query);
        return page(new Page<>(query.getCurrent(), query.getSize()), wrapper);
    }

    /**
     * 查询全部仓库。
     */
    @Override
    public List<Warehouse> listAll() {
        // 下拉场景直接返回全量仓库。
        return list(new LambdaQueryWrapper<Warehouse>().orderByDesc(Warehouse::getId));
    }

    /**
     * 修改仓库并校验名称唯一。
     */
    @Override
    public void updateByIdChecked(Warehouse warehouse) {
        if (getById(warehouse.getId()) == null) {
            throw new BaseException("仓库不存在");
        }
        // 修改时排除自身后再校验名称唯一。
        validateNameUnique(warehouse.getName(), warehouse.getId());
        if (!updateById(warehouse)) {
            throw new BaseException("仓库不存在");
        }
        // 修改后清缓存，保证基础资料映射及时更新。
        baseDictMapStore.clearWarehouseMap();
    }

    /**
     * 删除仓库前校验是否被入库申请或入库单引用。
     */
    @Override
    public void removeByIdChecked(Long id) {
        // 仓库当前仅在入库业务中使用，删除前需要校验申请单和单据引用。
        long applyCount = ioApplyService.count(new LambdaQueryWrapper<IoApply>()
                .eq(IoApply::getWarehouseId, id)
                .eq(IoApply::getOrderType, IoBizTypeEnum.INBOUND.getCode()));
        if (applyCount > 0) {
            throw new BaseException("该仓库已被入库申请使用，无法删除");
        }
        long orderCount = ioOrderService.count(new LambdaQueryWrapper<IoOrder>()
                .eq(IoOrder::getWarehouseId, id)
                .eq(IoOrder::getOrderType, IoBizTypeEnum.INBOUND.getCode()));
        if (orderCount > 0) {
            throw new BaseException("该仓库已被入库单使用，无法删除");
        }
        if (!removeById(id)) {
            throw new BaseException("仓库不存在");
        }
        // 删除后同步失效缓存，避免基础资料继续返回已删除仓库。
        baseDictMapStore.clearWarehouseMap();
    }

    /**
     * 构建仓库分页查询条件。
     */
    private LambdaQueryWrapper<Warehouse> buildWrapper(WarehouseQuery query) {
        // 仓库列表仅支持按名称模糊搜索。
        return new LambdaQueryWrapper<Warehouse>()
                .like(StrUtil.isNotBlank(query.getName()), Warehouse::getName, query.getName())
                .orderByDesc(Warehouse::getId);
    }

    /**
     * 校验仓库名称唯一。
     */
    private void validateNameUnique(String name, Long excludeId) {
        // 编辑场景排除当前记录，避免自身触发重复校验。
        LambdaQueryWrapper<Warehouse> wrapper = new LambdaQueryWrapper<Warehouse>()
                .eq(Warehouse::getName, name)
                .ne(excludeId != null, Warehouse::getId, excludeId);
        if (count(wrapper) > 0) {
            throw new BaseException("仓库名称已存在");
        }
    }
}
