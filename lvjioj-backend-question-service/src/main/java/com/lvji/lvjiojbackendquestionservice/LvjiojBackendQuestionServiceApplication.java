package com.lvji.lvjiojbackendquestionservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.lvji.lvjiojbackendquestionservice.mapper")
@EnableScheduling
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@ComponentScan("com.lvji")
@EnableFeignClients(basePackages = {"com.lvji.lvjiojbackendserviceclient.service"})
@EnableDiscoveryClient
public class LvjiojBackendQuestionServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(LvjiojBackendQuestionServiceApplication.class, args);
    }

}
