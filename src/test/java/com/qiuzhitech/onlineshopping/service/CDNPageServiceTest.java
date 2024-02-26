package com.qiuzhitech.onlineshopping.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class CDNPageServiceTest {

    @Resource
    CDNPageService cdnPageService;
    @Test
    void createHtml() {
        cdnPageService.createHtml(12311L);
    }
}