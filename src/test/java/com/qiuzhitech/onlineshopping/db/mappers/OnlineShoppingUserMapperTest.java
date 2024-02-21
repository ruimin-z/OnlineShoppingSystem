package com.qiuzhitech.onlineshopping.db.mappers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qiuzhitech.onlineshopping.db.po.OnlineShoppingUser;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class OnlineShoppingUserMapperTest {

    @Resource
    OnlineShoppingUserMapper userMapper;

    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void insert() throws JsonProcessingException {
        userMapper.deleteByPrimaryKey(123L);
        OnlineShoppingUser user = OnlineShoppingUser.builder()
                .userId(123L)
                .address("Seattle")
                .name("zhangsan")
                .userType(1)
                .phone("111111")
                .email("zhangsan@gmail.com")
                .build();
        userMapper.insert(user);
        log.info(objectMapper.writeValueAsString(userMapper.selectByPrimaryKey(123L)));
    }
}