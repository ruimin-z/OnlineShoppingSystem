package com.qiuzhitech.onlineshopping.service.mq;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class RocketMQService {
    @Resource
    private RocketMQTemplate rocketMQTemplate;

    public void sendMessage(String topic, String msg) throws MQBrokerException, RemotingException, InterruptedException, MQClientException {
        Message message = new Message(topic, msg.getBytes());
        rocketMQTemplate.getProducer().send(message);
    }

    public void sendDelayMessage(String topic, String msg, int delayTimeLevel) throws MQBrokerException, RemotingException, InterruptedException, MQClientException {
        // Message Delay Time and Level: https://rocketmq.apache.org/docs/4.x/producer/04message3/
        // messageDelayLevel=1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h
        Message message = new Message(topic, msg.getBytes());
        message.setDelayTimeLevel(delayTimeLevel);
        rocketMQTemplate.getProducer().send(message);
    }



}
