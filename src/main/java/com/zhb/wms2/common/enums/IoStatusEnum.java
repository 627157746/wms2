package com.zhb.wms2.common.enums;

import java.util.Arrays;
import java.util.Objects;

/**
 * IoStatusEnum 枚举
 *
 * @author zhb
 * @since 2026/3/26
 */
public enum IoStatusEnum {

    /**
     * 未执行。
     */
    PENDING(0, "未执行"),

    /**
     * 已执行。
     */
    DONE(1, "已执行");

    /**
     * 状态编码。
     */
    private final int code;

    /**
     * 状态描述。
     */
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

    /**
     * 判断当前枚举值是否与给定编码匹配。
     */
    public boolean matches(Integer code) {
        return Objects.equals(this.code, code);
    }

    /**
     * 根据编码获取对应枚举。
     */
    public static IoStatusEnum fromCode(Integer code) {
        return Arrays.stream(values())
                .filter(item -> item.matches(code))
                .findFirst()
                .orElse(null);
    }

    /**
     * 根据单据类型返回申请单场景下的状态描述。
     */
    public static String getApplyDesc(Integer code, Integer orderType) {
        IoStatusEnum statusEnum = fromCode(code);
        IoBizTypeEnum bizTypeEnum = IoBizTypeEnum.fromCode(orderType);
        if (statusEnum == null || bizTypeEnum == null) {
            return null;
        }
        if (DONE == statusEnum) {
            return IoBizTypeEnum.INBOUND == bizTypeEnum ? "已入库" : "已出库";
        }
        return IoBizTypeEnum.INBOUND == bizTypeEnum ? "未入库" : "未出库";
    }
}
