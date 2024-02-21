package com.qiuzhitech.onlineshopping.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class HelloController {

    @Resource
    DependencyA dependencyA;
    public void TestController(DependencyA dependencyA) {
        this.dependencyA = dependencyA;
    }
    @GetMapping("/hello")
    public String hello() {
        return dependencyA.send("Hello world!!!");
    }

    @GetMapping("/echo/{text}")
    public String echo(@PathVariable("text") String abc) {
        return "You just input: " + abc;
    }

}
