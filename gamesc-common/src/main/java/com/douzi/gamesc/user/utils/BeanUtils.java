package com.douzi.gamesc.user.utils;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName BeanUtils
 * @Description TODO
 * @Author wesker
 * @Date 7/19/2019 5:21 PM
 * @Version 1.0
 **/
public class BeanUtils {


    private BeanUtils() {
    }

    public static <T> T convertMapToObject(Map dataMap, Class<T> t) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(dataMap);
        T obj = JSON.parseObject(json, t);
        return obj;
    }

    /**
     * 对象转成map
     * @param obj
     * @return
     */
    public static Map<String,String> convertObjToMap(Object obj) throws Exception{
        Map<String,String> reMap = new HashMap<String,String>();
        if (obj == null)
            return null;
        Field[] fields = obj.getClass().getDeclaredFields();
        try {
            for(int i=0;i<fields.length;i++){
                Field f = obj.getClass().getDeclaredField(fields[i].getName());
                f.setAccessible(true);
                String o = f.get(obj)+"";
                if(o!=null&&!o.equals("null")){
                    reMap.put(fields[i].getName(), o);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reMap;
    }
}
