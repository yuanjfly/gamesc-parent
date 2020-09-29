package com.douzi.gamesc.pay.controller.sdk;

import com.douzi.gamesc.http.HttpResult;
import com.douzi.gamesc.pay.feign.ApiPayFeign;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.XMLConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.xml.sax.InputSource;

@Slf4j
@RestController
@RequestMapping("/pay/wechat")
public class WechatOfficePayCallBackController {

    @Autowired
    private ApiPayFeign apiPayFeign;

    @ResponseBody
    @RequestMapping("/payCallback")
    public String payCallback(HttpServletRequest request,HttpServletResponse response) throws Exception {
        String str=IOUtils.toString(request.getInputStream(),"UTF-8");
        response.setContentType("text/html;charset=utf-8");// 此句必须在response.getWriter()之前
        log.info("wehcat sdk payback--微信回调字符串str:"+str);
        try{
            Map<String, String> mapRest = weixinCallBack(str);
            HttpResult result = apiPayFeign.wechatSdkCallback(mapRest);
            return  result.getMsg();
        }catch (Exception e){
            log.error("wechat call back error:"+e.getMessage());
            return "error";
        }

    }

    /**
     * 解析微信参数String格式的xml转map
     * @param xml
     * @return
     */
    private static Map<String,String> weixinCallBack(String xml) throws Exception{
        Map<String,String> map = new HashMap<String,String>();
        Document doc = null;
        try {
            String encoding = null;
            SAXReader saxReader = new SAXReader();
            saxReader.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            saxReader.setFeature("http://xml.org/sax/features/external-general-entities", false);
            saxReader.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            saxReader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            saxReader.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            String text = xml.trim();
            if (text.startsWith("<?xml")) {
                int end = text.indexOf("?>");
                String sub = text.substring(0, end);
                StringTokenizer tokens = new StringTokenizer(sub, " =\"\'");
                while (tokens.hasMoreTokens()) {
                    String token = tokens.nextToken();
                    if ("encoding".equals(token)) {
                        if (tokens.hasMoreTokens()) {
                            encoding = tokens.nextToken();
                        }
                        break;
                    }
                }
            }
            InputSource source = new InputSource(new StringReader(xml));
            source.setEncoding(encoding);
            doc = saxReader.read(source);
            if (doc.getXMLEncoding() == null) {
                doc.setXMLEncoding(encoding);
            }
            //doc = DocumentHelper.parseText(xml); // 将字符串转为XML
            List list = doc.selectNodes("//xml");
            Iterator iter = list.iterator();
            // 遍历子节点
            while (iter.hasNext()) {
                Element itemEle = (Element) iter.next();
                List<Element> listElement =itemEle.elements();
                for (Element object : listElement) {
                    map.put(object.getName(), object.getText());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }
}
