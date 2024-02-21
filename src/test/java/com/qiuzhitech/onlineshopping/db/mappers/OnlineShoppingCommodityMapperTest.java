package com.qiuzhitech.onlineshopping.db.mappers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper; // Java 中用于处理 JSON 数据的类库
import com.qiuzhitech.onlineshopping.db.po.OnlineShoppingCommodity;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
@Slf4j
class OnlineShoppingCommodityMapperTest {

    @Resource
    OnlineShoppingCommodityMapper commodityMapper;

    ObjectMapper objectMapper = new ObjectMapper(); // java对象转json字符串

    @Test
    void insert() throws JsonProcessingException {
        commodityMapper.deleteByPrimaryKey(123L);
        OnlineShoppingCommodity onlineShoppingCommodity =
                OnlineShoppingCommodity.builder()
                        .commodityId(123L)
                        .price(123)
                        .commodityDesc("desc")
                        .commodityName("name")
                        .creatorUserId(123L)
                        .availableStock(111)
                        .totalStock(200)
                        .lockStock(0)
                        .build();
        commodityMapper.insert(onlineShoppingCommodity);
        log.info(objectMapper.writeValueAsString(commodityMapper.selectByPrimaryKey(123L)));
    }

    @Test
    void selectByPrimaryKey() throws JsonProcessingException {
        OnlineShoppingCommodity commodity = commodityMapper.selectByPrimaryKey(123L);
        log.info(objectMapper.writeValueAsString(commodity));
    }
}