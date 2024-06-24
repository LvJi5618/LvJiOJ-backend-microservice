package com.lvji.lvjiojbackendjudgeservice.message;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;

/**
 * 创建交换机和队列（只在程序执行前执行一次）
 */
@Slf4j
public class InitRabbitMq {

    public static void doInit(){
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            // 交换机名称
            String EXCHANGE_NAME = "code_exchange";
            // 创建交换机
            channel.exchangeDeclare(EXCHANGE_NAME, "direct");
            // 队列名称
            String queueName = "code_queue";
            //创建队列
            channel.queueDeclare(queueName, true, false, false, null);
            //交换机与队列绑定
            channel.queueBind(queueName, EXCHANGE_NAME, "my_routingKey");
            log.info("消息队列启动成功");
        }catch (Exception e){
            log.error("消息队列启动失败");
        }
    }

}
