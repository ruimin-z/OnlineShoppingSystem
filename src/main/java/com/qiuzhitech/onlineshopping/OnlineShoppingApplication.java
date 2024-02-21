package com.qiuzhitech.onlineshopping;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan("com.qiuzhitech.onlineshopping.db.mappers") //配置启动类扫描
@ComponentScan(basePackages = "com.qiuzhitech") //配置启动类扫描
public class OnlineShoppingApplication {

    public static void main(String[] args) {
        SpringApplication.run(OnlineShoppingApplication.class, args);
    }

}
