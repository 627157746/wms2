package com.zhb.wms2.common.enums;

import java.util.Arrays;
import java.util.Objects;

/**
 * 盘点任务状态枚举。
 *
 * @author zhb
 * @since 2026/4/12
 */
public enum StockCheckTaskStatusEnum {

    /**
     * 盘点中。
     */
    COUNTING(1, "盘点中"),

    /**
     * 已盘点。
     */
    COUNTED(2, "已盘点"),

    /**
     * 已调整。
     */
    ADJUSTED(3, "已调整");

    private final int code;

    private final String desc;

    StockCheckTaskStatusEnum(int code, String desc) {
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
                .map(StockCheckTaskStatusEnum::getDesc)
                .findFirst()
                .orElse(null);
    }
}
