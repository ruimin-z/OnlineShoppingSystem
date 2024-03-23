package com.qiuzhitech.onlineshopping.service.mq;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.locks.LockSupport;

@Component
@Slf4j
@RocketMQMessageListener(topic = "consumerTopicMultiple", consumerGroup = "consumerGroupMultiple")
/*
收到message做什么
 */
public class TestConsumerListener implements RocketMQListener<MessageExt>, RocketMQPushConsumerLifecycleListener {

    @Override
    public void onMessage(MessageExt messageExt) {
        // update: purposefully fail
        if (messageExt.getReconsumeTimes() < 3){
            // message always fail when retry time < 3
            throw new RuntimeException("Message Reconsume Time is less than 3 times");
        }
        // message retry time = 3, continue with following logic
        String body = new String(messageExt.getBody());
        // wait for 3 seconds
        long nanos = 3_000_000_000L; // 3 seconds in nanoseconds
        LockSupport.parkNanos(nanos);
        // print message content
        log.info("Received message, msg body: "+ body);
    }

    @Override
    public void prepareStart(DefaultMQPushConsumer consumer) {
        // 在此方法中可以设置一些消费者的属性
        consumer.setMaxReconsumeTimes(3);  // 设置最大重试次数 retry maximum 3 times
        // if did not receive ACK from consumer in 5s, MQ assume consumer died, and put the message back to MQ
        consumer.setConsumeTimeout(5000); // 设置消费超时时间为5秒
        consumer.setConsumeThreadMin(2);
        consumer.setConsumeThreadMax(2);  // maximum consumer number
    }


}
