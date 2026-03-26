package com.zhb.wms2.common.exception;

/**
 * 业务异常。
 *
 * @author zhb
 * @since 2026/3/26
 */
public class BaseException extends RuntimeException {

    /**
     * 使用业务提示信息构造异常。
     */
    public BaseException(String msg) {
        super(msg);
    }
}
