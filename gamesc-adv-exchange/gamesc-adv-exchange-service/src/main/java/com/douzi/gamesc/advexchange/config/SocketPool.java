package com.douzi.gamesc.advexchange.config;

import com.douzi.gamesc.advexchange.vo.SocketInfo;
import java.io.IOException;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class SocketPool {

    static int SOCKET_DEFAULT_COUNT = 5;
    static int SOCKET_CHECK_TIME = 20000;
    static ConcurrentHashMap<Integer, SocketInfo> socketMap = new ConcurrentHashMap<Integer, SocketInfo>();

    @Value("${rpc.manager.host}")
    private String prop_host= "";
    @Value("${rpc.manager.port}")
    private String prop_port= "";

    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public SocketPool buildSocketPool(){
        SocketPool socketPool = new  SocketPool();
        return socketPool;
    }

    public void start(){
        initSocket(true);
    }

    public void shutdown() throws IOException {
        if(socketMap.size() > 0){
            for (Map.Entry<Integer, SocketInfo> entry : socketMap.entrySet()) {
                SocketInfo socketInfo = entry.getValue();
                if(socketInfo.getSocket()!=null){
                    socketInfo.getSocket().close();
                }
            }
            socketMap.clear();
        } else {
            log.error("nameserver:socketInfo FAIL 名字服务器socket连接池数量为零。");
        }
    }
    /**
     * @DateTime 2014-8-25 下午3:18:52
     * @User liuxingmi
     * @Desc 初始化链接池
     * @param isAllReInit  是否全部重新初始化
     * @return void
     */
    public  void initSocket(boolean isAllReInit){
        log.info("nameserver:initSocket OK 开始初始化分布式名字服务器连接数：" + SOCKET_DEFAULT_COUNT);
        for (int i = 0; i < SOCKET_DEFAULT_COUNT; i++) {
            if(isAllReInit){
                socketMap.put(i, setSocketInfo( i, true, false));
            } else {
                if(socketMap.get(i) == null || socketMap.get(i).isClosed()){
                    socketMap.put(i, setSocketInfo( i, true, false));
                }
            }
        }
        log.info("nameserver:initSocket OK 完成初始化分布式名字服务器连接数");
        if(isAllReInit){
            new CheckSocketThread().start();
        }
    }

    /**
     * @DateTime 2014-8-26 上午10:06:13
     * @User liuxingmi
     * @Desc 设置socketInfo值
     * @param key
     * @param isFree
     * @param isClosed
     * @return SocketInfo
     */
    private SocketInfo setSocketInfo(Integer key, boolean isFree, boolean isClosed){
        SocketInfo socketInfo = new SocketInfo();
        Socket socket = createSocket();
        socketInfo.setFree(isFree);
        socketInfo.setSocket(socket);
        socketInfo.setSocketId(key);
        socketInfo.setClosed(isClosed);
        return socketInfo;
    }

    /**
     * @DateTime 2014-8-25 下午3:19:06
     * @User liuxingmi
     * @Desc 获取名字服务器链接
     * @return
     * SocketInfo
     */
    public SocketInfo getSocketInfo(){

        SocketInfo socketInfo = null;
        if(socketMap.size() < SOCKET_DEFAULT_COUNT){
            initSocket(false);
        }
        if(socketMap.size() > 0){
            for (Map.Entry<Integer, SocketInfo> entry : socketMap.entrySet()) {
                socketInfo = entry.getValue();
                if(socketInfo.isFree() && ! socketInfo.getSocket().isClosed()){
                    socketInfo.setFree(false);
                    return socketInfo;
                }
            }
        } else {
            log.error("nameserver:socketInfo FAIL 名字服务器socket连接池数量为零。");
            return null;
        }
        log.info("nameserver:socketInfo OK 所有名字服务器socket链接都忙，创建临时链接。");
        socketInfo = setSocketInfo(-1, true, true);
        log.info("nameserver:socketInfo OK 成功创建服务器socket临时链接。");
        return socketInfo;

    }

    /**
     * 释放socket
     * @param socketId
     */
    public  void distorySocket(Integer socketId){
        log.debug("nameserver:distorySocket OK 释放名字服务器socket链接。");
        SocketInfo socketInfo = socketMap.get(socketId);
        socketInfo.setFree(true);

    }

    /**
     * @DateTime 2014-8-25 下午3:19:42
     * @User liuxingmi
     * @Desc 释放socket
     * @param socketInfo
     * void
     */
    public  void distorySocket(SocketInfo socketInfo){

        if(socketInfo == null) return;
        if( ! socketInfo.isClosed()){
            log.debug("nameserver:distorySocket OK 链接池socket，释放资源。");
            distorySocket(socketInfo.getSocketId());
            return;
        }
        log.debug("nameserver:distorySocket OK 可关闭临时链接，关闭socket");
        try {
            if(socketInfo.getSocket() != null){
                socketInfo.getSocket().close();
            }
        } catch (IOException e) {
            log.error("nameserver:distorySocket FAIL 关闭名字服务器socket链接失败", e);
        }
        socketInfo = null;
    }
    /**
     * @DateTime 2014-8-25 下午3:19:51
     * @User liuxingmi
     * @Desc 创建socket
     * @return
     * Socket
     */
    public  Socket createSocket(){
        Socket socket = null;
        try {// 尝试通过ip1第一次建立连接
            socket = new Socket(prop_host, Integer.valueOf(prop_port));
            log.info("nameserver:login OK nameServerip1:" + prop_host + ", namServerport1:" + prop_port);
        } catch (IOException e) {
            log.error("nameserver:login FAIL first link fail nameServerip1:" + prop_host + ", namServerport1:" + prop_port, e);
            try {
                // 如果第一次通过ip1建立连接失败，则进行第二次连接
                socket = new Socket(prop_host, Integer.valueOf(prop_port));
                log.info("nameserver:login OK nameServerip2:" + prop_host + ", namServerport2:" + prop_port);
            } catch (IOException e1) {
                log.error("nameserver:login FAIL second link fail nameServerip2:" + prop_host + ", namServerport2:" + prop_port, e);
                return null;
            }
        }
        return socket;
    }

    class CheckSocketThread extends Thread{
        @Override
        public void run() {
            while (true) {
                log.info("nameserver:checkSocket OK 开始检测分布式链接状态。");
                if(socketMap.size() < SOCKET_DEFAULT_COUNT){
                    log.info("nameserver:checkSocket OK 分布式名字服务器socket链接小于默认链接数，增加socket链接。");
                    initSocket(false);
                }
                for (Map.Entry<Integer, SocketInfo> entry : socketMap.entrySet() ) {
                    SocketInfo socketInfo = entry.getValue();
                    if(socketInfo.getSocket() == null || socketInfo.isClosed()){
                        log.error("nameserver:checkSocket FAIL 第"+ entry.getKey()+"个socket链接已关闭，重新连接分布式。");
                        socketInfo.setSocket(createSocket());
                        socketInfo.setClosed(false);
                        socketInfo.setFree(true);
                    } else {
                        if(socketInfo.isFree()){
                            try {//发送检测是否断开
                                socketInfo.getSocket().sendUrgentData(0xFF);
                            } catch (Exception e) {//断开产生异常，关闭对象
                                socketInfo.setClosed(true);
                            }
                        }
                        log.info("nameserver:checkSocket OK 第"+ entry.getKey()+"个socket链接正常。");
                    }
                }
                try {
                    sleep(Long.valueOf(SOCKET_CHECK_TIME));
                } catch (Exception e) {

                }
            }
        }
    }
}
