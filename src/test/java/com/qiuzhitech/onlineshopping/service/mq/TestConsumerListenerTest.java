package com.qiuzhitech.onlineshopping.service.mq;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import java.util.Date;


@SpringBootTest
class TestConsumerListenerTest {

    @Resource
    RocketMQService rocketMQService;

    @Test
    public void testMessage() throws Exception {
        int i = 5; // 一次发5个message
        while(i>0) {
            i--;
            rocketMQService.sendMessage("consumerTopicMultiple", "Today is " + new Date());
        }
    }
}