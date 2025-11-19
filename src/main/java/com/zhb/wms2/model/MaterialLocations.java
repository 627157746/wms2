package com.zhb.wms2.model;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zhb.wms2.common.BaseModel;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @Author zhb
 * @Description
 * @Date 2025/11/19 16:18
 */
/**
 * 物料位信息表
 */
@Schema(description="物料位信息表")
@Data
@EqualsAndHashCode(callSuper=true)
@TableName(value = "material_locations")
public class MaterialLocations extends BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 物料位ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description="物料位ID")
    private Long id;

    /**
     * 物料位编码
     */
    @TableField(value = "location_code")
    @Schema(description="物料位编码")
    private String locationCode;

    /**
     * 排号
     */
    @TableField(value = "row_no")
    @Schema(description="排号")
    @NotBlank(message = "排号不能为空")
    @Size(max = 20, message = "排号长度不能超过20个字符")
    private String rowNo;

    /**
     * 段号
     */
    @TableField(value = "section_no")
    @Schema(description="段号")
    @NotBlank(message = "段号不能为空")
    @Size(max = 20, message = "段号长度不能超过20个字符")
    private String sectionNo;

    /**
     * 自动生成物料位编码
     * 格式：rowNo + "-" + sectionNo
     * 例如：rowNo="10", sectionNo="1" -> locationCode="10-1"
     */
    public void generateLocationCode() {
        if (StrUtil.isNotBlank(this.rowNo) && StrUtil.isNotBlank(this.sectionNo)) {
            this.locationCode = this.rowNo + "-" + this.sectionNo;
        }
    }

}
