package com.douzi.gamesc.exchange;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients("com.douzi.gamesc.*")
@EnableDiscoveryClient
public class ExchangeWebAppliaction {
    public static void main(String[] args) {
        SpringApplication.run(ExchangeWebAppliaction.class,args);
    }

}
