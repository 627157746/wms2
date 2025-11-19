package com.zhb.wms2.config;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.filter.SaServletFilter;
import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import com.zhb.wms2.common.R;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Author zhb
 * @Description Sa-Token 权限认证配置类 暂时不用
 * @Date 2025/8/5 10:59
 */
//@Configuration
public class SaTokenConfigure implements WebMvcConfigurer {

    private static final String[] excludePath = {"/favicon.ico",
            "/**doc.*",
            "/**swagger-ui.*",
            "/**swagger-resources",
            "/**webjars/**",
            "/**v3/api-docs/**",
            "/auth/login",
            "/auth/logout",
            "/auth/register",
            "/captcha"};

    // 注册 Sa-Token 拦截器，打开注解式鉴权功能
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册 Sa-Token 拦截器，打开注解式鉴权功能
        registry.addInterceptor(new SaInterceptor(handle -> StpUtil.checkLogin()))
                .addPathPatterns("/**")
                .excludePathPatterns(excludePath);
    }

    /**
     * 注册 [Sa-Token全局过滤器]
     */
    @Bean
    public SaServletFilter getSaServletFilter() {
        return new SaServletFilter()

                // 指定 拦截路由 与 放行路由
                .addInclude("/**").addExclude(excludePath)

                // 认证函数: 每次请求执行
                .setAuth(obj -> {
                    // 具体的路由验证由拦截器处理，此处可以添加额外的全局验证
                    SaRouter.match("/**", () -> {
                        // 可以在这里添加全局验证逻辑
                    });
                })
                // 异常处理函数：每次认证函数发生异常时执行此函数
                .setError(e -> {
                    // 设置响应头
                    SaHolder.getResponse().setHeader("Content-Type", "application/json;charset=UTF-8");
                    // 返回错误信息
                    return R.error("认证失败：" + e.getMessage());
                })
                // 前置函数：在每次认证函数之前执行（BeforeAuth 不受 includeList 与 excludeList 的限制，所有请求都会进入）
                .setBeforeAuth(r -> {
                    // ---------- 设置一些安全响应头 ----------
                    SaHolder.getResponse()
                            // 服务器名称
                            .setServer("sa-server")
                            // 是否可以在iframe显示视图： DENY=不可以 | SAMEORIGIN=同域下可以 | ALLOW-FROM uri=指定域名下可以
                            .setHeader("X-Frame-Options", "SAMEORIGIN")
                            // 是否启用浏览器默认XSS防护： 0=禁用 | 1=启用 | 1; mode=block 启用, 并在检查到XSS攻击时，停止渲染页面
                            .setHeader("X-XSS-Protection", "1; mode=block")
                            // 禁用浏览器内容嗅探
                            .setHeader("X-Content-Type-Options", "nosniff");
                });
    }
}
