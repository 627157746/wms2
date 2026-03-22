package com.zhb.wms2.module.inventory.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhb.wms2.module.inventory.model.entity.Inventory;
import com.zhb.wms2.module.inventory.model.query.InventoryDistributionQuery;
import com.zhb.wms2.module.inventory.model.vo.InventoryDistributionGroupVO;

import java.util.List;

/**
 * @Author zhb
 * @Description
 * @Date 2026/3/17 19:07
 */
public interface InventoryService extends IService<Inventory> {

    List<InventoryDistributionGroupVO> listDistribution(InventoryDistributionQuery query);

}
