package com.zhb.wms2.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * R 模型
 *
 * @author zhb
 * @since 2026/3/26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class R<T> {

    /**
     * 响应状态码。
     */
    private int code;

    /**
     * 响应消息。
     */
    private String msg;

    /**
     * 响应数据。
     */
    private T data;

    /**
     * 构造成功响应。
     */
    public static <T> R<T> ok(T data) {
        return new R<>(200, "ok", data);
    }

    /**
     * 构造无返回数据的成功响应。
     */
    public static <T> R<T> optOk() {
        return new R<>(200, "操作成功", null);
    }

    /**
     * 构造失败响应。
     */
    public static <T> R<T> error(String msg) {
        return new R<>(500, msg, null);
    }

    /**
     * 按指定状态码和消息构造响应。
     */
    public static <T> R<T> of(int code, String msg, T data) {
        return new R<>(code, msg, data);
    }


}
