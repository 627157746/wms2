package com.zhb.wms2;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.zhb.wms2.mapper")
@SpringBootApplication
public class Wms2Application {

    public static void main(String[] args) {
        SpringApplication.run(Wms2Application.class, args);
    }

}
