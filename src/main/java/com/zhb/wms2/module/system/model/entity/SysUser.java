package com.zhb.wms2.module.system.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zhb.wms2.common.model.BaseModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 系统用户
 *
 * @author zhb
 * @since 2026/3/26
 */
@Schema(description = "系统用户")
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
public class SysUser extends BaseModel implements Serializable {
    /**
     * 序列化版本号。
     */
    private static final long serialVersionUID = 1L;

    /**
     * 主键 ID。
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;

    /**
     * 用户名。
     */
    @TableField("username")
    @Schema(description = "用户名")
    private String username;

    /**
     * 密码。
     */
    @JsonIgnore
    @TableField("password")
    @Schema(description = "密码")
    private String password;
}
