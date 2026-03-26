package com.zhb.wms2.module.io.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhb.wms2.module.io.model.dto.IoApplyCreateDTO;
import com.zhb.wms2.module.io.model.dto.IoApplyUpdateDTO;
import com.zhb.wms2.module.io.model.entity.IoApply;
import com.zhb.wms2.module.io.model.query.IoApplyQuery;
import com.zhb.wms2.module.io.model.vo.IoApplyPageVO;

/**
 * IoApplyService 服务接口
 *
 * @author zhb
 * @since 2026/3/26
 */
public interface IoApplyService extends IService<IoApply> {

    /**
     * 分页查询出入库申请。
     */
    IPage<IoApplyPageVO> pageQuery(IoApplyQuery query);

    /**
     * 查询出入库申请详情。
     */
    IoApplyPageVO getDetailById(Long id);

    /**
     * 新增出入库申请。
     */
    Long saveApply(IoApplyCreateDTO dto);

    /**
     * 修改出入库申请。
     */
    void updateApply(IoApplyUpdateDTO dto);

    /**
     * 审批出入库申请。
     */
    void approveById(Long id);

    /**
     * 取消出入库申请审批。
     */
    void cancelApproveById(Long id);

    /**
     * 删除出入库申请。
     */
    void removeByIdChecked(Long id);
}
