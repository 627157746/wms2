package com.zhb.wms2.module.base.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zhb.wms2.common.model.BaseModel;
import com.zhb.wms2.common.validated.Update;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

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
     * 适用范围：0-不限 1-出库 2-入库
     */
    @TableField(value = "`scope`")
    @Schema(description = "适用范围：0-不限 1-出库 2-入库")
    @Min(value = 0, message = "适用范围不正确")
    @Max(value = 2, message = "适用范围不正确")
    private Integer scope;

    /**
     * 备注
     */
    @TableField(value = "remark")
    @Schema(description = "备注")
    private String remark;
}
