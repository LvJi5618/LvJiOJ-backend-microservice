package com.lvji.lvjiojbackendquestionservice.message;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class MyMessageProducer {

    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 生成者向消息队列发送消息
     * @param exchange
     * @param routingKey
     * @param message
     */
    public void sendMessage(String exchange,String routingKey,String message) {
        rabbitTemplate.convertAndSend(exchange,routingKey,message);
    }
}
