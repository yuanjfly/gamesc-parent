package com.douzi.gamesc.advexchange.utils;

import com.alibaba.fastjson.JSONObject;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import mousio.client.promises.ResponsePromise;
import mousio.etcd4j.EtcdClient;
import mousio.etcd4j.requests.EtcdKeyGetRequest;
import mousio.etcd4j.responses.EtcdKeysResponse;
import mousio.etcd4j.responses.EtcdKeysResponse.EtcdNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JdEtcdClientUtils {

    @Autowired
    private EtcdClient client;

    /**
     * 获取节点的key值，如果是非尾部借点返回空
     * @param key
     * @return
     * @throws Exception
     */
    public String getValueByKey(String key) throws Exception {
        EtcdKeyGetRequest etcdKeyGetRequest = client.get(key).consistent();
        EtcdKeysResponse dataTree  = etcdKeyGetRequest.send().get();
        if(dataTree==null||dataTree.getNode()==null||dataTree.getNode().getValue()==null)
        {
            return  null ;
        }
        return dataTree.getNode().getValue();
    }

    /**
     * 通过父节点递归
     * @param key
     * @return
     * @throws Exception
     */
    public JSONObject getNodeListValue(String key) throws Exception {
        EtcdKeyGetRequest etcdKeyGetRequest = client.getDir(key).consistent();
        EtcdKeysResponse dataTree  = etcdKeyGetRequest.send().get();
        EtcdNode node = dataTree.getNode();
        if(node ==null){
            return  null ;
        }
        JSONObject result = new JSONObject();
        if(node.getNodes()!=null){//存在子节点
            getNodeByParentNode(result,node.getNodes());
        }else{//不存在子节点
            if(node.getValue()!=null&&!"".equals(node.getValue())){
                result.put(node.getKey(),node.getValue());
            }
        }
        return  result;
    }
    /**
     * 循环递归获取子节点
     * @param info
     * @param nodes
     */
    private void getNodeByParentNode(JSONObject info,List<EtcdNode> nodes) throws Exception {
        if(nodes==null||nodes.size()<=0)
        {
            return;
        }
        for(EtcdKeysResponse.EtcdNode node:nodes){
            if(node.getNodes()!=null){
                getNodeByParentNode(info,node.getNodes());
            }else if(node.getValue()!=null&&!"".equals(node.getValue())){
                info.put(node.getKey(),node.getValue());
            }
        }
    }

    public void startListenerThread(String key,ExchangeRedisUtils redisUtils,String redisKey,long times){
        new Thread(()->{
            startListener(key,redisUtils,redisKey,times);
        }).start();
    }

    private void startListener(String key,ExchangeRedisUtils redisUtils,String redisKey,long times){
        ResponsePromise promise =null;
        try {
            promise = client.getDir(key).recursive().waitForChange().consistent().send();
            promise.addListener(promisea -> {
                log.info("found ETCD's config:{}cause change",key);
                try {
                    String cfgStr = getValueByKey(key);
                    if(cfgStr!=null&&!"".equals(cfgStr)){
                        redisUtils.set(redisKey,cfgStr,times);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    log.info("listen etcd 's config change cause exception:{}",e.getMessage());
                }
                startListener(key,redisUtils,redisKey,times);
            });
        } catch (Exception e) {
            startListener(key,redisUtils,redisKey,times);
            System.out.println("listen etcd 's config change cause exception:"+e.getMessage());
            e.printStackTrace();

        }
    }

}
