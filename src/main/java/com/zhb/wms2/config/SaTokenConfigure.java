package com.zhb.wms2.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Author zhb
 * @Description Sa-Token 权限认证配置类 暂时不用
 * @Date 2025/8/5 10:59
 */
@Configuration
public class SaTokenConfigure implements WebMvcConfigurer {

    private static final String[] excludePath = {"/favicon.ico",
            "/**doc.*",
            "/**swagger-ui.*",
            "/**swagger-resources",
            "/**webjars/**",
            "/**v3/api-docs/**",
            "/system/user/login"};

    // 注册 Sa-Token 拦截器，打开注解式鉴权功能
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册 Sa-Token 拦截器，打开注解式鉴权功能
        registry.addInterceptor(new SaInterceptor(handle -> StpUtil.checkLogin()))
                .addPathPatterns("/**")
                .excludePathPatterns(excludePath);
    }
}
