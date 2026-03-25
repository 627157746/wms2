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
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SalesmanServiceImpl extends ServiceImpl<SalesmanMapper, Salesman> implements SalesmanService {

    private final BaseDictMapStore baseDictMapStore;

    @Override
    public void saveChecked(Salesman salesman) {
        if (!super.save(salesman)) {
            throw new BaseException("业务员新增失败");
        }
        baseDictMapStore.clearSalesmanMap();
    }

    @Override
    public IPage<Salesman> pageQuery(SalesmanQuery query) {
        return page(new Page<>(query.getCurrent(), query.getSize()), buildWrapper(query));
    }

    @Override
    public List<Salesman> listAll() {
        return list(new LambdaQueryWrapper<Salesman>().orderByDesc(Salesman::getId));
    }

    @Override
    public void updateByIdChecked(Salesman salesman) {
        if (!updateById(salesman)) {
            throw new BaseException("业务员不存在");
        }
        baseDictMapStore.clearSalesmanMap();
    }

    @Override
    public void removeByIdChecked(Long id) {
        if (!removeById(id)) {
            throw new BaseException("业务员不存在");
        }
        baseDictMapStore.clearSalesmanMap();
    }

    private LambdaQueryWrapper<Salesman> buildWrapper(SalesmanQuery query) {
        return new LambdaQueryWrapper<Salesman>()
                .like(StrUtil.isNotBlank(query.getName()), Salesman::getName, query.getName())
                .like(StrUtil.isNotBlank(query.getPhone()), Salesman::getPhone, query.getPhone())
                .orderByDesc(Salesman::getId);
    }
}
