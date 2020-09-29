package com.douzi.gamesc.account.start;

import com.douzi.gamesc.account.service.EtcdConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class EtcdGetInfoInit implements CommandLineRunner{

    @Autowired
    private EtcdConfigService etcdConfigServiceImpl;


    @Override
    public void run(String... args) throws Exception {
        etcdConfigServiceImpl.getEtcdConfig();
        //启动一个线程进行监听
        etcdConfigServiceImpl.addListenerCfg();
    }

}
