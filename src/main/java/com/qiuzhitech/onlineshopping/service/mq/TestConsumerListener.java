package com.qiuzhitech.onlineshopping.service.mq;

import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RocketMQMessageListener(topic = "consumerTopic", consumerGroup = "consumerGroup")
/*
收到message做什么
 */
public class TestConsumerListener implements RocketMQListener<MessageExt> {

    @Override
    public void onMessage(MessageExt messageExt) {
        String body = new String(messageExt.getBody());
        log.info("Received message, msg body: "+ body);
    }



}
