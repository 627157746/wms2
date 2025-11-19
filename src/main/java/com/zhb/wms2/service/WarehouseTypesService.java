package com.zhb.wms2.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhb.wms2.model.WarehouseTypes;
import com.zhb.wms2.model.dto.WarehouseTypesQuery;

/**
 * 出入库类型服务接口
 *
 * @author zhb
 */
public interface WarehouseTypesService extends IService<WarehouseTypes> {

    /**
     * 添加出入库类型
     */
    Long addWarehouseTypes(WarehouseTypes warehouseTypes);

    /**
     * 修改出入库类型
     */
    void updateWarehouseTypes(WarehouseTypes warehouseTypes);

    /**
     * 分页查询出入库类型
     */
    IPage<WarehouseTypes> queryPage(WarehouseTypesQuery query);

}
