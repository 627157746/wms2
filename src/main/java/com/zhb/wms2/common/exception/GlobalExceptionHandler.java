package com.zhb.wms2.common.exception;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import cn.dev33.satoken.exception.SaTokenException;
import com.zhb.wms2.common.model.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * 全局异常处理器。
 *
 * @author zhb
 * @since 2026/3/26
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 处理业务异常并返回明确的业务提示。
     */
    @ExceptionHandler(BaseException.class)
    public R<Void> handleBaseException(BaseException e) {
        return R.error(e.getMessage());
    }

    /**
     * 兜底处理标准异常并记录错误日志。
     */
    @ExceptionHandler(Exception.class)
    public R<Void> handleException(Exception e) {
        log.error("系统异常", e);
        return R.error("系统异常");
    }

    /**
     * 兜底处理未被捕获的 Throwable。
     */
    @ExceptionHandler(Throwable.class)
    public R<Void> handleThrowable(Throwable e) {
        log.error("系统异常", e);
        return R.error("系统异常");
    }

    /**
     * 汇总参数校验错误，返回给前端统一展示。
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("参数校验失败: {}", e.getMessage());
        StringBuilder message = new StringBuilder();
        e.getBindingResult().getFieldErrors().forEach(error ->
                message.append(error.getField()).append(": ").append(error.getDefaultMessage()).append("; ")
        );
        return R.error(message.toString());
    }

    /**
     * 处理不支持的 HTTP 请求方法。
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public R<Void> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        return R.error("不支持的请求方法");
    }

    /**
     * 处理不存在的资源访问。
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public R<Void> handleNoResourceFoundException(NoResourceFoundException e) {
        return R.error("请求的资源不存在");
    }

    /**
     * 根据未登录原因返回更具体的认证提示。
     */
    @ExceptionHandler(NotLoginException.class)
    public R<Void> handleNotLoginException(NotLoginException e) {
        String message = "";
        switch (e.getType()) {
            case NotLoginException.NOT_TOKEN:
                message = "未提供Token";
                break;
            case NotLoginException.INVALID_TOKEN:
                message = "Token无效";
                break;
            case NotLoginException.TOKEN_TIMEOUT:
                message = "Token已过期";
                break;
            case NotLoginException.BE_REPLACED:
                message = "Token已被顶下线";
                break;
            case NotLoginException.KICK_OUT:
                message = "Token已被踢下线";
                break;
            default:
                message = "当前会话未登录";
                break;
        }
        return R.of(401, message, null);
    }

    /**
     * 处理权限不足异常。
     */
    @ExceptionHandler(NotPermissionException.class)
    public R<Void> handleNotPermissionException(NotPermissionException e) {
        log.warn("权限不足: {}", e.getPermission());
        return R.of(403, "权限不足：" + e.getPermission(), null);
    }

    /**
     * 处理角色不足异常。
     */
    @ExceptionHandler(NotRoleException.class)
    public R<Void> handleNotRoleException(NotRoleException e) {
        log.warn("角色不足: {}", e.getRole());
        return R.of(403, "角色不足：" + e.getRole(), null);
    }

    /**
     * 处理 Sa-Token 相关认证异常。
     */
    @ExceptionHandler(SaTokenException.class)
    public R<Void> handleSaTokenException(SaTokenException e) {
        log.error("Sa-Token异常: {}", e.getMessage());
        return R.error("认证异常：" + e.getMessage());
    }
}
