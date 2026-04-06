package com.zhb.wms2.module.base.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhb.wms2.module.base.model.dto.BaseSortUpdateDTO;
import com.zhb.wms2.module.base.model.entity.Deliveryman;
import com.zhb.wms2.module.base.model.query.DeliverymanQuery;
import java.util.List;

/**
 * DeliverymanService 服务接口
 *
 * @author zhb
 * @since 2026/3/26
 */
public interface DeliverymanService extends IService<Deliveryman> {

    /**
     * 新增送货员。
     */
    void saveChecked(Deliveryman deliveryman);

    /**
     * 分页查询送货员。
     */
    IPage<Deliveryman> pageQuery(DeliverymanQuery query);

    /**
     * 按适用范围查询送货员。
     */
    List<Deliveryman> listAllByScope(Integer scope);

    /**
     * 修改送货员。
     */
    void updateByIdChecked(Deliveryman deliveryman);

    /**
     * 批量修改排序。
     */
    void updateSortOrderBatch(List<BaseSortUpdateDTO> dtoList);

    /**
     * 删除送货员。
     */
    void removeByIdChecked(Long id);
}
