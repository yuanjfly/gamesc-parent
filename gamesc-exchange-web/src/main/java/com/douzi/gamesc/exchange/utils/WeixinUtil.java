package com.douzi.gamesc.exchange.utils;

import com.alibaba.fastjson.JSONObject;
import com.douzi.gamesc.user.utils.HttpClient;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public class WeixinUtil {

    private static final String TOKEN_URI = "https://api.weixin.qq.com/sns/oauth2/access_token";
    private static final String REFRESH_TOKEN_URI = "https://api.weixin.qq.com/sns/oauth2/refresh_token";
    private static final String SNS_USER_INFO_URL = "https://api.weixin.qq.com/sns/userinfo";

    /**
     * 获取token
     * @param code
     * @param appid
     * @param secret
     * @return
     * @throws Exception
     */
    public static String getToken(String code, String appid, String secret)
            throws Exception {
        Map params = new HashMap();
        params.put("appid", appid);
        params.put("secret", secret);
        params.put("code", code);
        params.put("grant_type", "authorization_code");
        HttpClient httpClient = new HttpClient(TOKEN_URI,params);
        httpClient.setHttps(true);
        httpClient.get();
        return  httpClient.getContent();
    }

    /**
     * 获取用户信息
     * @param openid
     * @param accessToken
     * @return
     * @throws Exception
     */
    public static UserInfo getSnsUserInfo(String openid, String accessToken)
            throws Exception {
        Map params = new HashMap();
        params.put("access_token", accessToken);
        params.put("openid", openid);
        params.put("lang", "zh_CN");
        HttpClient httpClient = new HttpClient(SNS_USER_INFO_URL,params);
        httpClient.setHttps(true);
        httpClient.get();
        String jsonStr = httpClient.getContent();
        if (StringUtils.isNotEmpty(jsonStr)) {
            JSONObject obj = JSONObject.parseObject(jsonStr);
            if (obj.get("errcode") != null) {
                throw new Exception(obj.getString("errmsg"));
            } else {
                UserInfo user = (UserInfo) JSONObject.toJavaObject(obj, WeixinUtil.UserInfo.class);
                return user;
            }
        } else {
            return null;
        }
    }

    public static class UserInfo{
        public  UserInfo(){

        }
        private int id;
        private int subscribe;
        private String openid;
        private String nickname;
        private int sex;
        private String language;
        private String city;
        private String province;
        private String country;
        private String headimgurl;
        private Long subscribe_time;
        private String unionid;
        private int siteID;
        private String siteOpenID;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getSiteID() {
            return siteID;
        }

        public void setSiteID(int siteID) {
            this.siteID = siteID;
        }

        public String getSiteOpenID() {
            return siteOpenID;
        }

        public void setSiteOpenID(String siteOpenID) {
            this.siteOpenID = siteOpenID;
        }

        public int getSubscribe() {
            return subscribe;
        }

        public void setSubscribe(int subscribe) {
            this.subscribe = subscribe;
        }

        public String getOpenid() {
            return openid;
        }

        public void setOpenid(String openid) {
            this.openid = openid;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public int getSex() {
            return sex;
        }

        public void setSex(int sex) {
            this.sex = sex;
        }

        public String getLanguage() {
            return language;
        }

        public void setLanguage(String language) {
            this.language = language;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getProvince() {
            return province;
        }

        public void setProvince(String province) {
            this.province = province;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getHeadimgurl() {
            return headimgurl;
        }

        public void setHeadimgurl(String headimgurl) {
            this.headimgurl = headimgurl;
        }

        public Long getSubscribe_time() {
            return subscribe_time;
        }

        public void setSubscribe_time(Long subscribe_time) {
            this.subscribe_time = subscribe_time;
        }

        public String getUnionid() {
            return unionid;
        }

        public void setUnionid(String unionid) {
            this.unionid = unionid;
        }
    }
}
