package com.zhb.wms2.common.enums;

import java.util.Arrays;
import java.util.Objects;

public enum PickingStatusEnum {

    UNPICKED(0, "未拣货"),
    PICKED(1, "已拣货");

    private final int code;
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

    public boolean matches(Integer code) {
        return Objects.equals(this.code, code);
    }

    public static String getDesc(Integer code) {
        return Arrays.stream(values())
                .filter(item -> item.matches(code))
                .map(PickingStatusEnum::getDesc)
                .findFirst()
                .orElse(null);
    }
}
