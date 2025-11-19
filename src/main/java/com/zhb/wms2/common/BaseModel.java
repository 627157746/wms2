package com.zhb.wms2.common;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 基础模型类
 * 所有业务实体都应继承此类
 *
 * @author zhb
 * @since 1.0
 */
@Data
public class BaseModel {

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 创建人
     */
    @TableField("create_by")
    private String createBy;

    /**
     * 更新人
     */
    @TableField("update_by")
    private String updateBy;

    /**
     * 逻辑删除标记
     * 0-未删除，时间戳-已删除
     */
    @JsonIgnore
    @TableLogic
    @TableField("delete_flag")
    private Long deleteFlag;
}
