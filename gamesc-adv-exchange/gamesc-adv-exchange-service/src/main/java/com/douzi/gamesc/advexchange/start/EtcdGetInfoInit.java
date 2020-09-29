package com.douzi.gamesc.advexchange.start;

import com.douzi.gamesc.advexchange.service.EtcdConfigService;
import com.douzi.gamesc.advexchange.service.UserPropService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class EtcdGetInfoInit implements CommandLineRunner{

    @Autowired
    private EtcdConfigService etcdConfigServiceImpl;
    @Autowired
    private UserPropService userPropServiceImpl;

    @Override
    public void run(String... args) throws Exception {
        etcdConfigServiceImpl.getEtcdConfig();
        userPropServiceImpl.getUserVipLevelConfig();
        //启动一个线程进行监听
        etcdConfigServiceImpl.addListenerCfg();
        userPropServiceImpl.addListenerCfg();
    }

}
