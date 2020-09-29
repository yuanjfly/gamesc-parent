package com.douzi.gamesc.advexchange.start;

import com.alibaba.fastjson.JSONObject;
import com.douzi.gamesc.advexchange.config.SocketPool;
import com.douzi.gamesc.advexchange.vo.SocketInfo;
import com.googlecode.jsonrpc4j.JsonRpcClient;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.PropertiesUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PropOperateClient implements CommandLineRunner{


    @Autowired
    SocketPool socketPool;

    @Override
    public void run(String... args) throws Exception {
        socketPool.initSocket(false);
    }

    @PreDestroy
    public void destory(){

    }

    public JSONObject sendData(String methodName,JSONObject data){
        try {
            synchronized (this){
                SocketInfo socketInfo = socketPool.getSocketInfo();
                InputStream ips = socketInfo.getSocket().getInputStream();
                OutputStream ops = socketInfo.getSocket().getOutputStream();
                JsonRpcClient rpcClient = new JsonRpcClient();
                JSONObject rs = rpcClient.invokeAndReadResponse(methodName, new Object[]{data.toJSONString()}, JSONObject.class, ops, ips);
                socketPool.distorySocket(socketInfo);
                return rs;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return  null ;
    }

}
