package com.zhb.wms2.common.enums;

import java.util.Arrays;
import java.util.Objects;

/**
 * IoBizTypeEnum 枚举
 *
 * @author zhb
 * @since 2026/3/26
 */
public enum IoBizTypeEnum {

    /**
     * 入库业务。
     */
    INBOUND(1, "入库"),

    /**
     * 出库业务。
     */
    OUTBOUND(2, "出库");

    /**
     * 类型编码。
     */
    private final int code;

    /**
     * 类型描述。
     */
    private final String desc;

    IoBizTypeEnum(int code, String desc) {
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
    public static IoBizTypeEnum fromCode(Integer code) {
        return Arrays.stream(values())
                .filter(item -> item.matches(code))
                .findFirst()
                .orElse(null);
    }

    /**
     * 根据编码获取描述。
     */
    public static String getDesc(Integer code) {
        IoBizTypeEnum item = fromCode(code);
        return item == null ? null : item.getDesc();
    }
}
