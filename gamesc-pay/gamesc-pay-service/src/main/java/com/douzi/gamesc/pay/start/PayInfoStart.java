package com.douzi.gamesc.pay.start;

import com.douzi.gamesc.pay.service.PlatChannelMasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class PayInfoStart implements ApplicationRunner{

    @Autowired
    private PlatChannelMasterService platChannelMasterServiceImpl;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        platChannelMasterServiceImpl.initPlatChannelMaster();
    }
}
