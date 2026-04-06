package com.zhb.wms2.module.base.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhb.wms2.module.base.model.dto.BaseSortUpdateDTO;
import com.zhb.wms2.module.base.model.entity.Salesman;
import com.zhb.wms2.module.base.model.query.SalesmanQuery;
import java.util.List;

/**
 * SalesmanService 服务接口
 *
 * @author zhb
 * @since 2026/3/26
 */
public interface SalesmanService extends IService<Salesman> {

    /**
     * 新增业务员。
     */
    void saveChecked(Salesman salesman);

    /**
     * 分页查询业务员。
     */
    IPage<Salesman> pageQuery(SalesmanQuery query);

    /**
     * 查询全部业务员。
     */
    List<Salesman> listAll();

    /**
     * 修改业务员。
     */
    void updateByIdChecked(Salesman salesman);

    /**
     * 批量修改排序。
     */
    void updateSortOrderBatch(List<BaseSortUpdateDTO> dtoList);

    /**
     * 删除业务员。
     */
    void removeByIdChecked(Long id);
}
