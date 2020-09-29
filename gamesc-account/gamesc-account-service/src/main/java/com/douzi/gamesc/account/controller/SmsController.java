package com.douzi.gamesc.account.controller;

import com.alibaba.fastjson.JSONObject;
import com.douzi.gamesc.account.service.EtcdConfigService;
import com.douzi.gamesc.account.service.SmsSendService;
import com.douzi.gamesc.common.pojo.Result;
import com.douzi.gamesc.http.HttpResult;
import com.douzi.gamesc.account.pojo.SmsVerifyCodeRecord;
import com.douzi.gamesc.account.service.MongoDbService;
import com.douzi.gamesc.user.utils.MobileCheckUtil;
import com.douzi.gamesc.user.utils.RandomChars;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sms")
@Slf4j
public class SmsController {

    @Autowired
    private MongoDbService<SmsVerifyCodeRecord> verifyCodeMongoDbServiceImpl;

    @Autowired
    private SmsSendService smsSendServiceImpl;

    @Autowired
    private EtcdConfigService etcdConfigServiceImpl;


    /**
     *发送验证码信息
     * @return
     */
    @RequestMapping("/send/code/{business}/{phone}")
    public HttpResult sendCode(@PathVariable("business")String business,@PathVariable("phone")String phone) {
        try {
            if(StringUtils.isAnyBlank(business,phone)){
                return HttpResult.error(Result.REQUEST_PARAM_NULL.getCode(),Result.REQUEST_PARAM_NULL.getMsg());
            }
            //验证手机号格式是否正确
            if ("".equals(phone) || !MobileCheckUtil.isMobile(phone)) {
                return HttpResult.error(Result.PHONE_FORMAT_ERROR.getCode(),Result.PHONE_FORMAT_ERROR.getMsg());
            }
            JSONObject smsCfg = etcdConfigServiceImpl.getSmsCfgByOpen();
            if(smsCfg==null||!smsCfg.containsKey("tag")){
                return HttpResult.error(Result.PHONE_SEND_FAIL.getCode(),Result.PHONE_SEND_FAIL.getMsg());
            }
            String verifyCode = RandomChars.getRandomNumber(5);
            SmsVerifyCodeRecord record = new SmsVerifyCodeRecord();
            record.setBusiness(business);
            record.setCode(verifyCode);
            record.setPhone(phone);
            record.setStatus(0);
            record.setCreateTime(new Date());
            verifyCodeMongoDbServiceImpl.saveObj(record);
            int result = smsSendServiceImpl.sendVerifyCodeByMajhong(smsCfg,phone,verifyCode);
            if(result!=0){
                log.error(phone+"send verifyCode   fail....:");
                return HttpResult.error(Result.PHONE_SEND_FAIL.getCode(),Result.PHONE_SEND_FAIL.getMsg());
            }
            return HttpResult.ok();
        }catch (Exception e) {
            log.error(phone+"send code   error....:"+e.getMessage());
            e.printStackTrace();
        }
        return HttpResult.error(Result.SERVER_ERROR.getCode(),Result.SERVER_ERROR.getMsg());
    }

    /**
     *验证短信验证码
     * @return
     */
    @RequestMapping("/verifyCode/{business}/{phone}/{code}")
    public HttpResult verifyCode(@PathVariable("business")String business,@PathVariable("phone")String phone,@PathVariable("code")String code) {
        try {
            if(StringUtils.isAnyBlank(business,phone,code)){
                return HttpResult.error(Result.REQUEST_PARAM_NULL.getCode(),Result.REQUEST_PARAM_NULL.getMsg());
            }
            //验证手机号格式是否正确
            if ("".equals(phone) || !MobileCheckUtil.isMobile(phone)) {
                return HttpResult.error(Result.PHONE_FORMAT_ERROR.getCode(),Result.PHONE_FORMAT_ERROR.getMsg());
            }
            //查询半小时以内的验证码
            Query query = new Query();
            query.addCriteria(
                    new Criteria().andOperator(
                            Criteria.where("code").is(code),
                            Criteria.where("phone").is(phone),
                            Criteria.where("business").is(business),
                            Criteria.where("createTime").gte(new Date(System.currentTimeMillis()-30*60*1000)),
                            Criteria.where("status").is(0)
                    )
            );
            //验证验证码是否正确
            SmsVerifyCodeRecord record  = verifyCodeMongoDbServiceImpl.findLastOne(query,"createTime","DESC");
            if(record==null){
                return HttpResult.error(Result.PHONE_CODE_ERROR.getCode(),Result.PHONE_CODE_ERROR.getMsg());
            }
            //验证成功更新记录
            record.setStatus(1);
            record.setUpdateTime(new Date());
            verifyCodeMongoDbServiceImpl.updateObject(record);
            return HttpResult.ok();
        }catch (Exception e) {
            log.error(phone+" verifyCode   error....:"+e.getMessage());
            e.printStackTrace();
        }
        return HttpResult.error(Result.SERVER_ERROR.getCode(),Result.SERVER_ERROR.getMsg());
    }




}
