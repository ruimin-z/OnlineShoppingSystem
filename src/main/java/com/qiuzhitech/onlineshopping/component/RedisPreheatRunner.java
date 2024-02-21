package com.qiuzhitech.onlineshopping.component;

import com.qiuzhitech.onlineshopping.db.dao.OnlineShoppingCommodityDao;
import com.qiuzhitech.onlineshopping.db.po.OnlineShoppingCommodity;
import com.qiuzhitech.onlineshopping.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Component // 不被其他Controller，Service或Repository调用
// 查询之前，必须要将缓存信息与数据库进行同步，这个步骤就叫缓存预热
public class RedisPreheatRunner implements ApplicationRunner {  // ApplicationRunner 是 Spring Boot 中用于在 Spring Boot 应用程序启动后执行特定逻辑的接口。
    @Resource
    RedisService redisService;
    @Resource
    OnlineShoppingCommodityDao commodityDao;

    /**
     * update stock information from DB to Redis
     *
     * @param args
     * @throws Exception
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<OnlineShoppingCommodity> onlineShoppingCommodities = commodityDao.listCommodityByUserId(123);
        for (OnlineShoppingCommodity commodity : onlineShoppingCommodities) {
            redisService.setValue("commodity:" + commodity.getCommodityId(), (long) commodity.getAvailableStock());
            log.info("PreHeat starting, initialize commodity:" + commodity.getCommodityId());
        }
    }
}