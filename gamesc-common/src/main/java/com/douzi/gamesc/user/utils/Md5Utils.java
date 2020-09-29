package com.douzi.gamesc.user.utils;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Md5Utils {

    /**
     * MD5 加密字符串
     * @param strTemp
     * @return
     * @throws Exception
     */
    public static String md5(String strTemp) throws Exception {
        MessageDigest mdAlgorithm = MessageDigest.getInstance("MD5");
        mdAlgorithm.update(strTemp.getBytes());
        byte digest[] = mdAlgorithm.digest();
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < digest.length; i++) {
            strTemp = Integer.toHexString(255 & digest[i]);
            if (strTemp.length() < 2)
                strTemp = (new StringBuilder("0")).append(strTemp).toString();
            hexString.append(strTemp);
        }

        return hexString.toString();
    }

    /**
     * 对参数进行排序生成字符串进行加密
     * 格式 param1=value1&param2=value2...&keyField=key
     * @param map
     * @param keyField
     * @param key
     * @return
     * @throws Exception
     */
    public static String paramByMapToUrlString(Map<String, String> map,String keyField,String key) throws Exception{
        if (map == null || map.isEmpty()) {
            return "";
        }
        List<Entry<String, String>> list = new ArrayList<Entry<String, String>>(map.entrySet());

        StringBuilder params = new StringBuilder();

        Collections.sort(list, new Comparator<Entry<String, String>>() {
            public int compare(Map.Entry<String, String> mapping1, Map.Entry<String, String> mapping2) {
                return mapping1.getKey().compareTo(mapping2.getKey());
            }
        });

        for (Map.Entry<String, String> entry : list) {
            params.append(entry.getKey()).append("=").append(String.valueOf(entry.getValue()))
                    .append("&");
        }
        String result = params.append(keyField+"=").append(key).toString();
        System.out.println("this is sign string is: "+result);
        return md5(result);
    }
}
