package com.zhb.wms2.common.enums;

import java.util.Arrays;
import java.util.Objects;

public enum ApproveStatusEnum {

    UNAPPROVED(0, "未审批"),
    APPROVED(1, "已审批");

    private final int code;
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

    public boolean matches(Integer code) {
        return Objects.equals(this.code, code);
    }

    public static String getDesc(Integer code) {
        return Arrays.stream(values())
                .filter(item -> item.matches(code))
                .map(ApproveStatusEnum::getDesc)
                .findFirst()
                .orElse(null);
    }
}
