package com.douzi.gamesc.user.controller;

import com.alibaba.fastjson.JSONObject;
import com.douzi.gamesc.common.pojo.Result;
import com.douzi.gamesc.common.pojo.game.GameUserMasterHistory;
import com.douzi.gamesc.http.HttpResult;
import com.douzi.gamesc.user.service.UserMasterHistoryService;
import com.douzi.gamesc.user.service.UserService;
import com.douzi.gamesc.user.utils.RedisKeyUtils;
import com.douzi.gamesc.user.utils.UserRedisUtils;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/userMaster")
@Slf4j
public class UserMasterHistoryController {

    @Autowired
    private UserService userServiceImpl;

    @Autowired
    private UserRedisUtils userRedisUtils;

    @Autowired
    private UserMasterHistoryService userMasterHistoryServiceImpl;

    @RequestMapping("/getRecord/{userId}/{listDate}")
    public HttpResult getRecord(@PathVariable("userId")Long userId,
            @PathVariable("listDate")String listDate) {
        try {
            if(userId==null||listDate==null){
                return HttpResult.error(Result.REQUEST_PARAM_NULL.getCode(),Result.REQUEST_PARAM_NULL.getMsg());
            }
            GameUserMasterHistory record = userMasterHistoryServiceImpl.getGameUserMasterHistoryByListDate(userId,listDate);
            if(record!=null){
                return HttpResult.ok(record);
            }else{
                return HttpResult.error("兑换记录不存在");
            }
        }catch (Exception e) {
            log.error("userMaster get record   error....:"+e.getMessage());
            e.printStackTrace();
         }
        return HttpResult.error(Result.SERVER_ERROR.getCode(),Result.SERVER_ERROR.getMsg());
    }

    @RequestMapping("/updateRecord/{userId}/{listDate}")
    public HttpResult updateRecord(@PathVariable("userId")Long userId,
            @PathVariable("listDate")String listDate) {
        try {
            if(userId==null||listDate==null){
                return HttpResult.error(Result.REQUEST_PARAM_NULL.getCode(),Result.REQUEST_PARAM_NULL.getMsg());
            }
            GameUserMasterHistory record = new  GameUserMasterHistory();
            record.setIfreceive(1);
            record.setUpdateTime(System.currentTimeMillis()/1000);
            int rs = userMasterHistoryServiceImpl.updateGameUserMasterHistory(record,userId,listDate);
            if(rs>0){
                //更新redis中的状态
                Map<Object, Object> userMasterList = userServiceImpl.getGameUserProperty(String.format(RedisKeyUtils.USER_MASTER_LIST,userId));
                if(userMasterList!=null&&userMasterList.containsKey(listDate)){
                    String value = (String)userMasterList.get(listDate);
                    if(value!=null){
                        JSONObject oldValue = JSONObject.parseObject(value);
                        oldValue.put("ifreceive",1);
                        oldValue.put("update_time",record.getUpdateTime());
                        String newValue  = oldValue.toJSONString();
                        userRedisUtils.hset(String.format(RedisKeyUtils.USER_MASTER_LIST,userId),listDate,newValue);
                    }
                }
                return HttpResult.ok();
            }
            return HttpResult.error("更新状态失败");
        }catch (Exception e) {
            log.error("userMaster updateRecord   error....:"+e.getMessage());
            e.printStackTrace();
        }
        return HttpResult.error(Result.SERVER_ERROR.getCode(),Result.SERVER_ERROR.getMsg());
    }
}
