package com.qiuzhitech.onlineshopping.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
class HelloControllerTest {

    @Resource
    HelloController helloController;

    @Mock
    DependencyA fakeDependencyA;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void helloTest() {
//        helloController = new HelloController(new DependencyA());
        String res = helloController.hello();
        assertEquals("Hello world!!!", res);
    }

    @Test
    void helloMockTest() {
        helloController = new HelloController();
        helloController.TestController(fakeDependencyA);
        when(fakeDependencyA.send(any()))
                .thenReturn("ABC");
        String res = helloController.hello();
        assertEquals("ABC", res);

        // 创建一个 Mockito ArgumentCaptor 对象，用于捕获 String 类型的参数
        ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
        // 验证在 fakeDependencyA 对象上的 send 方法是否被调用，并捕获传递给该方法的参数
        verify(fakeDependencyA).send(argument.capture());
        // 断言捕获到的参数值是否等于 "hello2"
        assertEquals("Hello world!!!", argument.getValue());
    }
}