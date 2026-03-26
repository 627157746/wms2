package com.zhb.wms2.common.enums;

import java.util.Arrays;
import java.util.Objects;

/**
 * ScopeEnum 枚举
 *
 * @author zhb
 * @since 2026/3/26
 */
public enum ScopeEnum {

    /**
     * 不限范围。
     */
    COMMON(0, "不限"),

    /**
     * 仅入库适用。
     */
    INBOUND(1, "入库"),

    /**
     * 仅出库适用。
     */
    OUTBOUND(2, "出库");

    /**
     * 范围编码。
     */
    private final int code;

    /**
     * 范围描述。
     */
    private final String desc;

    ScopeEnum(int code, String desc) {
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
     * 根据编码获取对应枚举。
     */
    public static ScopeEnum fromCode(Integer code) {
        return Arrays.stream(values())
                .filter(item -> item.matches(code))
                .findFirst()
                .orElse(null);
    }

    /**
     * 判断适用范围是否支持指定业务类型。
     */
    public static boolean supportsBizType(Integer scope, Integer bizType) {
        return COMMON.matches(scope) || Objects.equals(scope, bizType);
    }
}
