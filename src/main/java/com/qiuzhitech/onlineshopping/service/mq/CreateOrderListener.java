package com.qiuzhitech.onlineshopping.service.mq;

import com.alibaba.fastjson.JSON;
import com.qiuzhitech.onlineshopping.db.dao.OnlineShoppingCommodityDao;
import com.qiuzhitech.onlineshopping.db.dao.OnlineShoppingOrderDao;
import com.qiuzhitech.onlineshopping.db.po.OnlineShoppingCommodity;
import com.qiuzhitech.onlineshopping.db.po.OnlineShoppingOrder;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
@Slf4j
@RocketMQMessageListener(topic = "createOrder", consumerGroup = "createOrderGroup")
public class CreateOrderListener implements RocketMQListener<MessageExt> {
    @Resource
    RocketMQService rocketMQService;
    @Resource
    OnlineShoppingOrderDao orderDao;
    @Resource
    OnlineShoppingCommodityDao commodityDao;


    @Override
    public void onMessage(MessageExt messageExt) {
        //实现在数据库中扣减库存，发送 delay message查看订单是不是超时
        String message = new String(messageExt.getBody(), StandardCharsets.UTF_8);
        log.info("CreateOrder Message Body:" + message);
        // String类型message转换成Order
        OnlineShoppingOrder onlineShoppingOrder = JSON.parseObject(message, OnlineShoppingOrder.class);
        Long commodityId = onlineShoppingOrder.getCommodityId();
        int res = commodityDao.deductStock(commodityId);
        if (res > 0) {
            OnlineShoppingCommodity commodityDetail = commodityDao.getCommodityDetail(commodityId);
            log.info("Process succesful for commodityId:" + commodityId + ",Current available stock:" + commodityDetail.getAvailableStock());
            onlineShoppingOrder.setOrderAmount(commodityDetail.getPrice().longValue());
            onlineShoppingOrder.setCreateTime(new Date());
            onlineShoppingOrder.setOrderStatus(1);
            orderDao.insertOrder(onlineShoppingOrder);
            try {
                // Send delay message to MQ for check payment is overtime or not
                rocketMQService.sendDelayMessage("checkOrder", JSON.toJSONString(onlineShoppingOrder), 3);
            } catch (Exception e) {
                throw new RuntimeException("Failed to send Delay message to CheckOrder topic");
            }
        }

    }
}