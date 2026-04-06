package com.zhb.wms2.module.base.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhb.wms2.common.exception.BaseException;
import com.zhb.wms2.module.base.mapper.SalesmanMapper;
import com.zhb.wms2.module.base.model.entity.Salesman;
import com.zhb.wms2.module.base.model.query.SalesmanQuery;
import com.zhb.wms2.module.base.service.SalesmanService;
import com.zhb.wms2.module.base.service.support.BaseDictMapStore;
import com.zhb.wms2.module.io.model.entity.IoApply;
import com.zhb.wms2.module.io.model.entity.IoOrder;
import com.zhb.wms2.module.io.service.IoApplyService;
import com.zhb.wms2.module.io.service.IoOrderService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * SalesmanServiceImpl 服务实现
 *
 * @author zhb
 * @since 2026/3/26
 */
@Service
@RequiredArgsConstructor
public class SalesmanServiceImpl extends ServiceImpl<SalesmanMapper, Salesman> implements SalesmanService {

    private final IoApplyService ioApplyService;
    private final IoOrderService ioOrderService;
    private final BaseDictMapStore baseDictMapStore;

    /**
     * 新增业务员并清理字典缓存。
     */
    @Override
    public void saveChecked(Salesman salesman) {
        if (!super.save(salesman)) {
            throw new BaseException("业务员新增失败");
        }
        applyDefaultSortOrder(salesman.getId());
        // 业务员字典被出库申请和出库单复用，新增后立即清缓存。
        baseDictMapStore.clearSalesmanMap();
    }

    /**
     * 分页查询业务员。
     */
    @Override
    public IPage<Salesman> pageQuery(SalesmanQuery query) {
        // 业务员分页只做基础筛选。
        return page(new Page<>(query.getCurrent(), query.getSize()), buildWrapper(query));
    }

    /**
     * 查询全部业务员。
     */
    @Override
    public List<Salesman> listAll() {
        // 下拉场景直接使用全量业务员列表。
        return list(new LambdaQueryWrapper<Salesman>()
                .orderByDesc(Salesman::getSortOrder)
                .orderByDesc(Salesman::getId));
    }

    /**
     * 修改业务员并清理字典缓存。
     */
    @Override
    public void updateByIdChecked(Salesman salesman) {
        if (!updateById(salesman)) {
            throw new BaseException("业务员不存在");
        }
        // 修改后清缓存，保证业务单据读取到最新业务员信息。
        baseDictMapStore.clearSalesmanMap();
    }

    /**
     * 删除业务员前校验是否被申请单或出入库单引用。
     */
    @Override
    public void removeByIdChecked(Long id) {
        // 业务员会被申请和单据引用，删除前必须同时校验两边。
        long applyCount = ioApplyService.count(
                new LambdaQueryWrapper<IoApply>().eq(IoApply::getSalesmanId, id));
        if (applyCount > 0) {
            throw new BaseException("该业务员已被出入库申请使用，无法删除");
        }
        long orderCount = ioOrderService.count(
                new LambdaQueryWrapper<IoOrder>().eq(IoOrder::getSalesmanId, id));
        if (orderCount > 0) {
            throw new BaseException("该业务员已被出入库单使用，无法删除");
        }
        if (!removeById(id)) {
            throw new BaseException("业务员不存在");
        }
        // 删除后同步失效缓存。
        baseDictMapStore.clearSalesmanMap();
    }

    /**
     * 构建业务员分页查询条件。
     */
    private LambdaQueryWrapper<Salesman> buildWrapper(SalesmanQuery query) {
        // 列表页仅支持名称和电话模糊检索。
        return new LambdaQueryWrapper<Salesman>()
                .like(StrUtil.isNotBlank(query.getName()), Salesman::getName, query.getName())
                .like(StrUtil.isNotBlank(query.getPhone()), Salesman::getPhone, query.getPhone())
                .orderByDesc(Salesman::getSortOrder)
                .orderByDesc(Salesman::getId);
    }

    /**
     * 新增后按主键回填默认排序。
     */
    private void applyDefaultSortOrder(Long id) {
        Salesman updateEntity = new Salesman();
        updateEntity.setId(id);
        updateEntity.setSortOrder(Math.toIntExact(id));
        if (!updateById(updateEntity)) {
            throw new BaseException("业务员默认排序回填失败");
        }
    }
}
