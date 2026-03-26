package com.zhb.wms2.common.lock;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 方法级互斥锁。
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MethodLock {

    /**
     * 锁名前缀，未配置时默认使用类名+方法名。
     */
    String name() default "";

    /**
     * SpEL 锁 key，例如 #p0、#p0.id、#p0.productId。
     */
    String key() default "";

    /**
     * 等待时长，小于 0 表示一直等待直到拿到锁。
     */
    long waitTime() default -1L;

    /**
     * waitTime 的时间单位。
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * 获取锁失败时返回的业务提示。
     */
    String message() default "当前操作正在处理中，请稍后重试";
}
