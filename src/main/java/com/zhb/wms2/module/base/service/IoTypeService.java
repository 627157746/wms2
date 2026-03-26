package com.zhb.wms2.module.base.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhb.wms2.module.base.model.entity.IoType;
import com.zhb.wms2.module.base.model.query.IoTypeQuery;
import java.util.List;

/**
 * IoTypeService 服务接口
 *
 * @author zhb
 * @since 2026/3/26
 */
public interface IoTypeService extends IService<IoType> {

    /**
     * 新增出入库类型。
     */
    void saveChecked(IoType ioType);

    /**
     * 分页查询出入库类型。
     */
    IPage<IoType> pageQuery(IoTypeQuery query);

    /**
     * 按适用范围查询出入库类型。
     */
    List<IoType> listAllByScope(Integer scope);

    /**
     * 修改出入库类型。
     */
    void updateByIdChecked(IoType ioType);

    /**
     * 删除出入库类型。
     */
    void removeByIdChecked(Long id);
}
