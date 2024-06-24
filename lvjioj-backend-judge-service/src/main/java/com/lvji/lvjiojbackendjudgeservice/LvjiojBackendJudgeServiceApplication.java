package com.lvji.lvjiojbackendjudgeservice;

import com.lvji.lvjiojbackendjudgeservice.message.InitRabbitMq;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@ComponentScan("com.lvji")
@EnableFeignClients("com.lvji.lvjiojbackendserviceclient.service")
@EnableDiscoveryClient
public class LvjiojBackendJudgeServiceApplication {

    public static void main(String[] args) {
        // 在程序启动前,创建交换机和队列
        InitRabbitMq.doInit();
        SpringApplication.run(LvjiojBackendJudgeServiceApplication.class, args);
    }

}
