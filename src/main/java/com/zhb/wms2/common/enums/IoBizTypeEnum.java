package com.zhb.wms2.common.enums;

import java.util.Arrays;
import java.util.Objects;

public enum IoBizTypeEnum {

    INBOUND(1, "入库"),
    OUTBOUND(2, "出库");

    private final int code;
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

    public boolean matches(Integer code) {
        return Objects.equals(this.code, code);
    }

    public static IoBizTypeEnum fromCode(Integer code) {
        return Arrays.stream(values())
                .filter(item -> item.matches(code))
                .findFirst()
                .orElse(null);
    }

    public static String getDesc(Integer code) {
        IoBizTypeEnum item = fromCode(code);
        return item == null ? null : item.getDesc();
    }
}
