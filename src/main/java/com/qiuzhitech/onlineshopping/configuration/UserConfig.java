package com.qiuzhitech.onlineshopping.configuration;

import com.qiuzhitech.onlineshopping.model.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*
Configuration Annotation in Spring Boot
User-defined new instance for User
 */

@Configuration
public class UserConfig {

    @Bean(name = "nobody")
    public User getNobody() {
        return new User("Nobody", "Nobody@");
    }

    @Bean(name = "somebody")
    public User getSomebody() {
        return new User("Somebody", "Somebody@");
    }
}

