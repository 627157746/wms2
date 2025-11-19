package com.zhb.wms2.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhb.wms2.model.MaterialLocations;
import com.zhb.wms2.model.dto.MaterialLocationsQuery;

/**
 * 物料位置信息服务接口
 *
 * @author zhb
 */
public interface MaterialLocationsService extends IService<MaterialLocations> {

    /**
     * 添加物料位置信息
     */
    Long addMaterialLocation(MaterialLocations materialLocation);

    /**
     * 修改物料位置信息
     */
    void updateMaterialLocation(MaterialLocations materialLocation);

    /**
     * 分页查询物料位置信息
     */
    IPage<MaterialLocations> queryPage(MaterialLocationsQuery query);
}
