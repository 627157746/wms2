package com.zhb.wms2.common.lock;

import cn.hutool.core.util.StrUtil;
import com.zhb.wms2.common.exception.BaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Component;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Iterator;

/**
 * MethodLockAspect
 *
 * @author zhb
 * @since 2026/3/26
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class MethodLockAspect {

    private final LocalMethodLockManager localMethodLockManager;

    private final ExpressionParser expressionParser = new SpelExpressionParser();
    private final ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    /**
     * 为声明了方法锁注解的方法织入本地互斥控制。
     */
    @Around("@annotation(methodLock)")
    public Object around(ProceedingJoinPoint joinPoint, MethodLock methodLock) throws Throwable {
        Method method = getTargetMethod(joinPoint);
        String lockKey = buildLockKey(joinPoint, method, methodLock);
        LocalMethodLockManager.LockHandle lockHandle;
        try {
            lockHandle = localMethodLockManager.acquire(lockKey, methodLock.waitTime(), methodLock.timeUnit());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BaseException("获取执行锁被中断");
        }

        if (lockHandle == null) {
            throw new BaseException(methodLock.message());
        }

        try {
            return joinPoint.proceed();
        } finally {
            localMethodLockManager.release(lockHandle);
        }
    }

    /**
     * 获取代理对象实际执行的目标方法。
     */
    private Method getTargetMethod(ProceedingJoinPoint joinPoint) {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        return AopUtils.getMostSpecificMethod(method, joinPoint.getTarget().getClass());
    }

    /**
     * 结合注解配置与 SpEL 表达式生成最终锁键。
     */
    private String buildLockKey(ProceedingJoinPoint joinPoint, Method method, MethodLock methodLock) {
        String lockName = StrUtil.isNotBlank(methodLock.name())
                ? methodLock.name()
                : method.getDeclaringClass().getSimpleName() + "." + method.getName();
        if (StrUtil.isBlank(methodLock.key())) {
            return lockName;
        }

        MethodBasedEvaluationContext context = new MethodBasedEvaluationContext(
                joinPoint.getTarget(), method, joinPoint.getArgs(), parameterNameDiscoverer);
        Object keyValue = expressionParser.parseExpression(methodLock.key()).getValue(context);
        String key = stringify(keyValue);
        if (StrUtil.isBlank(key)) {
            throw new BaseException("方法锁key不能为空");
        }
        return lockName + ":" + key;
    }

    /**
     * 将 SpEL 结果统一转换为锁键字符串。
     */
    private String stringify(Object value) {
        if (value == null) {
            return "null";
        }
        if (value instanceof CharSequence charSequence) {
            return charSequence.toString();
        }
        if (value instanceof Iterable<?> iterable) {
            StringBuilder builder = new StringBuilder();
            Iterator<?> iterator = iterable.iterator();
            while (iterator.hasNext()) {
                if (!builder.isEmpty()) {
                    builder.append(',');
                }
                builder.append(stringify(iterator.next()));
            }
            return builder.toString();
        }
        if (value.getClass().isArray()) {
            int length = Array.getLength(value);
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < length; i++) {
                if (!builder.isEmpty()) {
                    builder.append(',');
                }
                builder.append(stringify(Array.get(value, i)));
            }
            return builder.toString();
        }
        return String.valueOf(value);
    }
}
