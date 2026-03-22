package com.zhb.wms2.module.base.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zhb.wms2.common.model.BaseModel;
import com.zhb.wms2.common.validated.Update;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @Author zhb
 * @Description
 * @Date 2026/3/17 19:34
 */

/**
 * 客户
 */
@Schema(description = "客户")
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "customer")
public class Customer extends BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "主键ID")
    @NotNull(groups = Update.class, message = "ID不能为空")
    private Long id;

    /**
     * 名称
     */
    @TableField(value = "`name`")
    @Schema(description = "名称")
    @NotBlank(message = "客户名称不能为空")
    private String name;

    /**
     * 手机
     */
    @TableField(value = "phone")
    @Schema(description = "手机")
    private String phone;

    /**
     * 座机
     */
    @TableField(value = "tel")
    @Schema(description = "座机")
    private String tel;

    /**
     * 微信号
     */
    @TableField(value = "wechat")
    @Schema(description = "微信号")
    private String wechat;

    /**
     * 地址
     */
    @TableField(value = "address")
    @Schema(description = "地址")
    private String address;

    /**
     * 备注
     */
    @TableField(value = "remark")
    @Schema(description = "备注")
    private String remark;
}
