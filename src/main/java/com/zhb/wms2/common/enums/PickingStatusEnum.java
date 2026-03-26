package com.zhb.wms2.common.enums;

import java.util.Arrays;
import java.util.Objects;

/**
 * PickingStatusEnum 枚举
 *
 * @author zhb
 * @since 2026/3/26
 */
public enum PickingStatusEnum {

    /**
     * 未拣货。
     */
    UNPICKED(0, "未拣货"),

    /**
     * 已拣货。
     */
    PICKED(1, "已拣货");

    /**
     * 状态编码。
     */
    private final int code;

    /**
     * 状态描述。
     */
    private final String desc;

    PickingStatusEnum(int code, String desc) {
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
                .map(PickingStatusEnum::getDesc)
                .findFirst()
                .orElse(null);
    }
}
