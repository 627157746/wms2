package com.zhb.wms2.module.base.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhb.wms2.module.base.model.entity.Warehouse;
import com.zhb.wms2.module.base.model.query.WarehouseQuery;
import java.util.List;

/**
 * WarehouseService 服务接口
 *
 * @author zhb
 * @since 2026/3/27
 */
public interface WarehouseService extends IService<Warehouse> {

    /**
     * 新增仓库。
     */
    void saveChecked(Warehouse warehouse);

    /**
     * 分页查询仓库。
     */
    IPage<Warehouse> pageQuery(WarehouseQuery query);

    /**
     * 查询全部仓库。
     */
    List<Warehouse> listAll();

    /**
     * 修改仓库。
     */
    void updateByIdChecked(Warehouse warehouse);

    /**
     * 删除仓库。
     */
    void removeByIdChecked(Long id);
}
