package com.zhb.wms2.common.enums;

import java.util.Objects;

public enum IoStatusEnum {

    PENDING(0, "未执行"),
    DONE(1, "已执行");

    private final int code;
    private final String desc;

    IoStatusEnum(int code, String desc) {
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

    public static String getApplyDesc(Integer code, Integer orderType) {
        if (DONE.matches(code)) {
            return IoBizTypeEnum.INBOUND.matches(orderType) ? "已入库" : "已出库";
        }
        return IoBizTypeEnum.INBOUND.matches(orderType) ? "未入库" : "未出库";
    }
}
