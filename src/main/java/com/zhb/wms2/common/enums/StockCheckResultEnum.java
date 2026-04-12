package com.zhb.wms2.common.enums;

import java.util.Arrays;
import java.util.Objects;

/**
 * 盘点结果枚举。
 *
 * @author zhb
 * @since 2026/4/12
 */
public enum StockCheckResultEnum {

    /**
     * 未盘。
     */
    UNCOUNTED(0, "未盘"),

    /**
     * 无差异。
     */
    MATCHED(1, "无差异"),

    /**
     * 盘盈。
     */
    PROFIT(2, "盘盈"),

    /**
     * 盘亏。
     */
    LOSS(3, "盘亏");

    private final int code;

    private final String desc;

    StockCheckResultEnum(int code, String desc) {
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
                .map(StockCheckResultEnum::getDesc)
                .findFirst()
                .orElse(null);
    }

    /**
     * 根据差异数量计算盘点结果。
     */
    public static Integer fromDiffQty(Long diffQty) {
        if (diffQty == null) {
            return UNCOUNTED.getCode();
        }
        if (diffQty > 0) {
            return PROFIT.getCode();
        }
        if (diffQty < 0) {
            return LOSS.getCode();
        }
        return MATCHED.getCode();
    }
}
