package com.zhb.wms2;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Wms2Application
 *
 * @author zhb
 * @since 2026/3/26
 */
@MapperScan("com.zhb.wms2.module.*.mapper")
@SpringBootApplication
public class Wms2Application {

    /**
     * 启动 Spring Boot 应用。
     */
    public static void main(String[] args) {
        SpringApplication.run(Wms2Application.class, args);
    }

}
