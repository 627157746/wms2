package com.zhb.wms2.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Sa-Token 鉴权配置。
 *
 * @author zhb
 * @since 2026/3/26
 */
@Configuration
public class SaTokenConfigure implements WebMvcConfigurer {

    /**
     * 无需登录即可访问的路径。
     */
    private static final String[] EXCLUDE_PATHS = {"/favicon.ico",
            "/**doc.*",
            "/**swagger-ui.*",
            "/**swagger-resources",
            "/**webjars/**",
            "/**v3/api-docs/**",
            "/system/user/login"};

    /**
     * 注册 Sa-Token 登录校验拦截器。
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor(handle -> StpUtil.checkLogin()))
                .addPathPatterns("/**")
                .excludePathPatterns(EXCLUDE_PATHS);
    }
}
