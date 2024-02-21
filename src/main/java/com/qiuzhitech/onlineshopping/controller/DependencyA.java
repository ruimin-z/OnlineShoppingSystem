package com.qiuzhitech.onlineshopping.controller;

import org.springframework.stereotype.Service;

@Service // dont know whats for
public class DependencyA {

    public static String Ruimin = "ruimin";

    public String send(String body) {
        // comment 传什么数据就输出什么数据
        return body;
    }
}
