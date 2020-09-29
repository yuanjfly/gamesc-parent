package com.douzi.gamesc.advexchange;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * Created by on 2019/4/19.
 */
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.douzi.gamesc.advexchange.mapper")
public class AdvExchangeServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdvExchangeServiceApplication.class, args);
    }
}
