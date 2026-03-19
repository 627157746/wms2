package com.zhb.wms2.module.inventory.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhb.wms2.module.inventory.model.entity.InventoryDetail;
import com.zhb.wms2.module.inventory.model.vo.InventoryDetailVO;

import java.util.List;

/**
 * @Author zhb
 * @Description 
 * @Date 2026/3/17 19:07
 */
public interface InventoryDetailService extends IService<InventoryDetail>{

    List<InventoryDetailVO> listByProductId(Long productId);

}
