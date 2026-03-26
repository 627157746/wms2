package com.zhb.wms2.common.enums;

import java.util.Arrays;
import java.util.Objects;

/**
 * ApproveStatusEnum 枚举
 *
 * @author zhb
 * @since 2026/3/26
 */
public enum ApproveStatusEnum {

    /**
     * 未审批。
     */
    UNAPPROVED(0, "未审批"),

    /**
     * 已审批。
     */
    APPROVED(1, "已审批");

    /**
     * 状态编码。
     */
    private final int code;

    /**
     * 状态描述。
     */
    private final String desc;

    ApproveStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    /**
     * 判断当前枚举值是否与给定编码匹配。
     */
    public boolean matches(Integer code) {
        return Objects.equals(this.code, code);
    }

    /**
     * 根据编码获取描述。
     */
    public static String getDesc(Integer code) {
        return Arrays.stream(values())
                .filter(item -> item.matches(code))
                .map(ApproveStatusEnum::getDesc)
                .findFirst()
                .orElse(null);
    }
}
