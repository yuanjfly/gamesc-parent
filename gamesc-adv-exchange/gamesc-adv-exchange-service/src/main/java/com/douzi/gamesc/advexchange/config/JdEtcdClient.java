package com.douzi.gamesc.advexchange.config;

import java.net.URI;
import mousio.etcd4j.EtcdClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JdEtcdClient {

    @Value("${zalando.etcd.location}")
    private String endPoins;
    @Bean
    public EtcdClient build(){
        EtcdClient client = new  EtcdClient (URI.create(endPoins));
        return client;
    }
}
