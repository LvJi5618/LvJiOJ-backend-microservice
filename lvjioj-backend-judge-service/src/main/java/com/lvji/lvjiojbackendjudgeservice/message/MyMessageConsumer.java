package com.lvji.lvjiojbackendjudgeservice.message;

import com.lvji.lvjiojbackendjudgeservice.judge.judgeservice.JudgeService;
import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 处理消息,执行判题
 */
@Component
@Slf4j
public class MyMessageConsumer {

    @Resource
    private JudgeService judgeService;

    @SneakyThrows
    // 指定消费者监听的消息队列和确认机制
    @RabbitListener(queues = "code_queue",ackMode = "MANUAL")
    public void receiveMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        log.info("receiveMessage message = {}", message);
        long questionSubmitId = Long.parseLong(message);
        try {
            // 调用判题服务，执行判题
            judgeService.doJudge(questionSubmitId);
            // 肯定确认,消息已被接受处理
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            // 否定确认，消息没有消费
            channel.basicNack(deliveryTag, false, false);
        }
    }
}
