package com.douzi.gamesc.pay.sdk.qutt;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.lang.String;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Sign {

    public static void main(String[] args) {
        Map<String,String> Values = new HashMap<>();
        Values.put("app_id", "1111");
        Values.put("a", "d");
        Values.put("43", "da");
        Values.put("ad", "fas");
        Values.put("time", Long.toString(new Date().getTime()/1000));
        String s = Sign.sign(Values,"111");
        System.out.println(s);
        Values.put("sign", s);
        try {
            System.out.println(new Sign().checkSign(Values,"111"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static  String getMD5(String need2Encode) throws NoSuchAlgorithmException {
        byte[] buf = need2Encode.getBytes();
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.update(buf);
        byte[] tmp = md5.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : tmp) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }

    public static String sign(Map<String,String> val,String appKey) {
        val.remove("sign");
        val.put("app_key", appKey);
        ArrayList<String> keys = new ArrayList<>();
        for (String key : val.keySet()) {
            keys.add(key);
        }
        keys.sort(new Comparator<String>() {
            @Override
            public int compare(String l,String r) {
                int i = l.compareTo(r);
                if (i>0) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });
        String r = "";
        String hashed = "";
        for (String i : keys) {
            r += i+val.get(i);
        }
        try {
            hashed = getMD5(r);
        } catch (Exception e) {
            e.printStackTrace();
        }
        val.remove("app_key");
        return hashed;
    }

    public static boolean checkSign(Map<String,String> val,String appKey) throws Exception {
        String sign1 = val.get("sign");
        if (sign1 == "") {
            throw new Exception("sign error");
        }
        String sign2 = sign(val,appKey);
        System.out.println(sign2);
        val.remove("app_key");
        if (sign1.equals(sign2)==false) {
            return false ;
        }
        return true;
    }
}
