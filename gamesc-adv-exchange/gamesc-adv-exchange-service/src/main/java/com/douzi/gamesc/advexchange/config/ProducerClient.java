package com.douzi.gamesc.advexchange.config;

import com.aliyun.openservices.ons.api.bean.ProducerBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProducerClient {

    @Autowired
    private MqConfig mqConfig;

    @Autowired
    private MqGameConfig mqGameConfig;

    @Bean(value ="producer" ,initMethod = "start", destroyMethod = "shutdown")
    public ProducerBean buildProducer() {
        ProducerBean producer = new ProducerBean();
        producer.setProperties(mqConfig.getMqPropertie());
        return producer;
    }

    @Bean(value = "gameProducer",initMethod = "start", destroyMethod = "shutdown")
    public ProducerBean buildGameProducer() {
        ProducerBean producer = new ProducerBean();
        producer.setProperties(mqGameConfig.getMqPropertie());
        return producer;
    }

}
