package com.qiuzhitech.onlineshopping.service;

import com.alibaba.fastjson.JSON;
import com.qiuzhitech.onlineshopping.db.dao.OnlineShoppingCommodityDao;
import com.qiuzhitech.onlineshopping.db.dao.OnlineShoppingOrderDao;
import com.qiuzhitech.onlineshopping.db.po.OnlineShoppingCommodity;
import com.qiuzhitech.onlineshopping.db.po.OnlineShoppingOrder;
import com.qiuzhitech.onlineshopping.service.mq.RocketMQService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@Service
@Slf4j
public class OrderService {

    @Resource
    OnlineShoppingOrderDao orderDao;

    @Resource
    OnlineShoppingCommodityDao commodityDao;

    @Resource
    RedisService redisService;

    @Resource
    private RocketMQService rocketMQService;

    @Deprecated
    public OnlineShoppingOrder processOrder(long commodityId, long userId) {
        OnlineShoppingCommodity commodityDetail = commodityDao.getCommodityDetail(commodityId);
        Integer availableStock = commodityDetail.getAvailableStock();
        if (availableStock > 0) {
            availableStock -= 1;
            log.info("Process successful for commodityId: " + commodityId + ", Current available stock: " + availableStock);
            OnlineShoppingOrder order = OnlineShoppingOrder.builder()
                    .commodityId(commodityId)
                    .userId(userId)
                    .orderNo(UUID.randomUUID().toString())
                    .createTime(new Date())
                    .orderAmount(commodityDetail.getPrice().longValue())
                    .orderStatus(1)
                    .build();
            orderDao.insertOrder(order);
            commodityDetail.setAvailableStock(availableStock);
            commodityDao.updateCommodity(commodityDetail);
            return order;
        } else {
            log.info("Process order failed due to no available stock, commodityId: " + commodityId);
            return null;
        }
    }

    @Deprecated
    public OnlineShoppingOrder processOrderOneSQL(long commodityId, long userId) {
        OnlineShoppingCommodity commodityDetail = commodityDao.getCommodityDetail(commodityId);
        Integer availableStock = commodityDetail.getAvailableStock();
        if (availableStock > 0) {
            int result = commodityDao.deductStock(commodityId); // try stock -1. success if result > 0
            if (result > 0) {
                log.info("Process successful for commodityId: " + commodityId + ", Current available stock: " + availableStock);
                OnlineShoppingOrder order = OnlineShoppingOrder.builder()
                        .commodityId(commodityId)
                        .userId(userId)
                        .orderNo(UUID.randomUUID().toString())
                        .createTime(new Date())
                        .orderAmount(commodityDetail.getPrice().longValue())
                        .orderStatus(1)
                        .build();
                orderDao.insertOrder(order);
                return order;
            }
        }
        log.info("Process order failed due to no available stock, commodityId: " + commodityId);
        return null;
    }

    public OnlineShoppingOrder processOrderStoredProcedure(long commodityId, long userId) {
        // 用ST把多个操作合称为原子操作
        OnlineShoppingCommodity commodityDetail = commodityDao.getCommodityDetail(commodityId);
        int availableStock = commodityDetail.getAvailableStock();
        if (availableStock > 0) {
            int result = commodityDao.deductStockSP(commodityId);
            if (result > 0) {
                log.info("Process successful for commodityId:" + commodityId + ",Current available stock:" + availableStock);
                OnlineShoppingOrder order = OnlineShoppingOrder.builder()
                        .userId(userId)
                        .commodityId(commodityId)
                        .orderNo(UUID.randomUUID().toString())
                        .orderAmount(commodityDetail.getPrice().longValue())
                        .createTime(new Date())
                        .orderStatus(1)
                        .build();
                orderDao.insertOrder(order);
                return order;
            }
        }
        log.info("Process order failed due to no available stock, commodityId:" + commodityId);
        return null;
    }

    public OnlineShoppingOrder processOrderRedis(long commodityId, long userId) {
        // 用Redis解决高并发，MySQL负载低和有single point failure
        // Redis存储库存信息
        String redisKey = "commodity:" + commodityId;
        long availableStock = redisService.stockDeduct(redisKey);
        if (availableStock >= 0) {
            int result = commodityDao.deductStock(commodityId);
            if (result > 0) {
                OnlineShoppingCommodity commodityDetail = commodityDao.getCommodityDetail(commodityId);
                log.info("Process successful for commodityId:" + commodityId + ",Current available stock:" + availableStock);
                OnlineShoppingOrder order = OnlineShoppingOrder.builder()
                        .userId(userId)
                        .commodityId(commodityId)
                        .orderNo(UUID.randomUUID().toString())
                        .orderAmount(commodityDetail.getPrice().longValue())
                        .createTime(new Date())
                        .orderStatus(1) // 1. already create order, pending for payment
                        .build();
                orderDao.insertOrder(order);
                return order;
            }
        }
        log.info("Process order failed due to no available stock, commodityId:" + commodityId);
        return null;
    }

    public OnlineShoppingOrder processOrderRocketMQ(long commodityId, long userId) throws Exception {
        // 削峰处理：加入MQ
        String redisKey = "commodity:" + commodityId;
        long availableStock = redisService.stockDeduct(redisKey);
        if (availableStock >= 0) {
            OnlineShoppingOrder order = OnlineShoppingOrder.builder()
                    .commodityId(commodityId)
                    .userId(userId)
                    .orderNo(UUID.randomUUID().toString())
                    .build();
            String msg = JSON.toJSONString(order);
            rocketMQService.sendMessage("createOrder", msg);
            return order;
        }
        log.info("Process order failed due to no available stock, commodityId:" + commodityId);
        return null;
    }



    public OnlineShoppingOrder processOrderDistributedLock(long commodityId, long userId) {
        // Redis分布式锁解决高并发，争抢资源机制，通用分布式解，但是吞吐量小
        // Redis锁住requestId
        // 缓存处理读取操作优化，一般缓存贵，硬盘/内存比较便宜，所以不把所有的数据放在缓存，而放最常用的数据
        String redisKey = "lock_commodity:" + commodityId;
        String requestID = UUID.randomUUID().toString();
        // 响应时间ttl太小超卖，ttl太大重复（因为有releaseLock所以应该没事），ttl要大于processing time
        boolean getLock = redisService.tryDistributedLock(redisKey, requestID, 5000); // true -> 拿到锁, 5000->ttl in miliseconds
        if (getLock) {
            int result = commodityDao.deductStockSP(commodityId);
            OnlineShoppingOrder order = null;
            if (result > 0) {
                log.info("Process successful for commodityId: " + commodityId);
                OnlineShoppingCommodity commodityDetail = commodityDao.getCommodityDetail(commodityId);
                order = OnlineShoppingOrder.builder()
                        .commodityId(commodityId)
                        .userId(userId)
                        .orderNo(UUID.randomUUID().toString())
                        .createTime(new Date())
                        .orderAmount(commodityDetail.getPrice().longValue())
                        .orderStatus(1)
                        .build();
                orderDao.insertOrder(order);
                log.info(order.getOrderNo());
            }
            redisService.releaseDistributedLock(redisKey, requestID);
            return order;
        }
        log.info("Please try again later for commodityID: " + commodityId);
        return null;
    }

    public OnlineShoppingOrder getOrderByOrderNo(String orderNo) {
        return orderDao.queryOrderByOrderNo(orderNo);
    }

    public int payOrder(String orderNum) {
        OnlineShoppingOrder order = getOrderByOrderNo(orderNum);
        order.setOrderStatus(2);
        order.setPayTime(new Date());
        return orderDao.updateOrder(order);
    }
}
