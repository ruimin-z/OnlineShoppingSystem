package com.qiuzhitech.onlineshopping.db.dao.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qiuzhitech.onlineshopping.db.dao.OnlineShoppingCommodityDao;
import com.qiuzhitech.onlineshopping.db.po.OnlineShoppingCommodity;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Slf4j
class OnlineShoppingCommodityDaoImplTest {

    @Resource
    OnlineShoppingCommodityDao commodityDao;
    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void insertCommodity() throws JsonProcessingException {
        OnlineShoppingCommodity onlineShoppingCommodity =
                OnlineShoppingCommodity.builder()
//                        .commodityId(125L)
                        .price(124)
                        .commodityDesc("desc")
                        .commodityName("name")
                        .creatorUserId(123L)
                        .availableStock(111)
                        .totalStock(200)
                        .lockStock(0)
                        .build();
        commodityDao.insertCommodity(onlineShoppingCommodity);
        log.info(objectMapper.writeValueAsString(commodityDao.getCommodityDetail(123L)));
    }

    @Test
    void listCommodities() throws JsonProcessingException {
        List<OnlineShoppingCommodity> onlineShoppingCommodities =
                commodityDao.listCommodities();
        log.info(objectMapper.writeValueAsString(onlineShoppingCommodities));
    }

    @Test
    void listCommodityByUserId() throws JsonProcessingException {
        List<OnlineShoppingCommodity> onlineShoppingCommodities =
                commodityDao.listCommodityByUserId(123L);
        log.info(objectMapper.writeValueAsString(onlineShoppingCommodities));
    }

    @Test
    void deleteCommodityById() throws JsonProcessingException {
        commodityDao.deleteCommodityById(123L);
        log.info(objectMapper.writeValueAsString(commodityDao.listCommodityByUserId(123L)));
    }

}