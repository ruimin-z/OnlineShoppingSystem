package com.qiuzhitech.onlineshopping.service;

import com.qiuzhitech.onlineshopping.db.dao.OnlineShoppingCommodityDao;
import com.qiuzhitech.onlineshopping.db.po.OnlineShoppingCommodity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

@Service
@Slf4j
public class SearchService {
    @Resource
    OnlineShoppingCommodityDao commodityDao; // 通过数据库实现like模糊搜索

    @Resource
    EsService esService;  // 通过elastic search倒排索引实现

    public List<OnlineShoppingCommodity> searchCommodityDDB(String keyword) {
        return commodityDao.searchCommodityByKeyword(keyword);
    }

    public List<OnlineShoppingCommodity> searchCommodityByES(String keyWord) throws IOException {
        return esService.searchCommodityByKeyword(keyWord, 0, 10);
    }
}