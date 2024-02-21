package com.qiuzhitech.onlineshopping.service.mq;

import com.alibaba.fastjson.JSON;
import com.qiuzhitech.onlineshopping.db.dao.OnlineShoppingCommodityDao;
import com.qiuzhitech.onlineshopping.db.dao.OnlineShoppingOrderDao;
import com.qiuzhitech.onlineshopping.db.po.OnlineShoppingOrder;
import com.qiuzhitech.onlineshopping.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RocketMQMessageListener(topic = "checkOrder", consumerGroup = "checkOrderGroup")
public class CheckOrderListener implements RocketMQListener<MessageExt> {
    @Resource
    OnlineShoppingOrderDao orderDao;
    @Resource
    RedisService redisService;
    @Resource
    OnlineShoppingCommodityDao commodityDao;
    @Override
    public void onMessage(MessageExt messageExt) {
        // 先查看能否收到message
        String message = new String(messageExt.getBody(), StandardCharsets.UTF_8);
        log.info("paymentCheck Message body: " + message);
        OnlineShoppingOrder orderFromMessage = JSON.parseObject(message, OnlineShoppingOrder.class); // 去序列化
        // 去数据库查询order，查看是否付款
        OnlineShoppingOrder order = orderDao.queryOrderByOrderNo(orderFromMessage.getOrderNo());
        if (order == null) {
            log.error("Can't find order in DB");
            return;
        }
        // 1. check current Order status in DB
        // Status as below:
        // 0: Invalid order, Since no available stock
        // 1: already create order, pending for payment
        // 2: finished payment
        // 99: invalid order due to payment proceed overtime
        if (order.getOrderStatus() != 2) {
            //2. change order status to 0, invalid the order
            log.info("Didn't pay the order on time, order number：" + order.getOrderNo());

            order.setOrderStatus(99);
            orderDao.updateOrder(order);
            commodityDao.revertStock(order.getCommodityId()); // Update DB - MySQL status
            String redisKey = "commodity:" + order.getCommodityId();
            redisService.revertStock(redisKey); // Update Cache Redis
        } else {
            log.info("Skip operation for order:" + JSON.toJSON(order));
        }
    }
}
