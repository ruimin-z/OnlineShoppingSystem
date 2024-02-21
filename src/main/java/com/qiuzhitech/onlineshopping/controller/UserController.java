package com.qiuzhitech.onlineshopping.controller;

import com.qiuzhitech.onlineshopping.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Controller
public class UserController {

    Map<String, User> users = new HashMap<>();
    @Resource(name = "somebody")
    User nobodyUser;

    @PostMapping("/users")
    public String createUser(@RequestParam("name") String name,
                             @RequestParam("email") String email,
                             Map<String, Object> resultMap) {
        User user = new User(name, email);
        users.put(name, user);
        resultMap.put("user", user);
        return "user_detail";
    }

    @GetMapping("/users")
    public String getUser(@RequestParam("name") String name,
                          Map<String, Object> resultMap){
        User user = users.getOrDefault(name, nobodyUser);
        resultMap.put("user", user);
        return "user_detail";
    }

    @PutMapping("/users")
    public String updateUser(@RequestParam("name") String name,
                             @RequestParam("email") String email,
                             Map<String, Object> resultMap){
        User user = users.get(name);
        user.email = email;
        users.put(name, user);
        resultMap.put("user", user);
        return "user_detail";
    }
}



