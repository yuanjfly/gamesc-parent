package com.douzi.gamesc.pay.sdk.wechatoffice;

import com.douzi.gamesc.common.pojo.order.PlatChannelMaster;
import com.douzi.gamesc.common.pojo.order.PlatOrderPre;
import com.douzi.gamesc.pay.sdk.wechatoffice.vo.WeiXinCallBackResp;
import com.douzi.gamesc.pay.sdk.wechatoffice.vo.WeiXinPayRespVo;
import com.douzi.gamesc.pay.sdk.wechatoffice.vo.WxRequest;
import com.douzi.gamesc.pay.sdk.wechatoffice.vo.WxResponse;
import com.douzi.gamesc.user.utils.BeanUtils;
import com.douzi.gamesc.user.utils.Md5Utils;
import com.douzi.gamesc.user.utils.RandomChars;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.StringTokenizer;
import java.util.TreeMap;
import javax.xml.XMLConstants;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.InputSource;

@Slf4j
public class WechatOrderHelp {

    /**
     * 返回给微信的对象处理
     * @param weiXinCallBackResp
     * @return
     */
    public static String getWechatRepXml(WeiXinCallBackResp weiXinCallBackResp)throws Exception {
        if (weiXinCallBackResp == null) {
            return "";
        }
        Map<String, String> map = BeanUtils.convertObjToMap(weiXinCallBackResp);
        SortedMap<String, String> sort = new TreeMap<String, String>(map);
        // 2.map转xml格式的String
        String strResp = tranRequestXml(sort);
        return strResp;
    }

    public static WeiXinPayRespVo getWeiXinPayRespVo(WxResponse wxResponse,PlatChannelMaster platChannelMaster)
            throws Exception{
        WeiXinPayRespVo weiXinPayRespVo = new WeiXinPayRespVo();
        weiXinPayRespVo.setAppid(platChannelMaster.getCpAppId());
        weiXinPayRespVo.setNoncestr(RandomChars.getRandomChars(32));
        //APP充值
        weiXinPayRespVo.setPackageValue("Sign=WXPay");
        weiXinPayRespVo.setPrepayid(wxResponse.getPrepay_id());
        weiXinPayRespVo.setPartnerid(platChannelMaster.getCpPayId());
        weiXinPayRespVo.setTimestamp(System.currentTimeMillis()/1000 + "");

        Map<String, String> weiXinJSPpayMap =  BeanUtils.convertObjToMap(weiXinPayRespVo);
        SortedMap<String, String> sortJSPpay = new TreeMap<String, String>(weiXinJSPpayMap);
        //获取签名
        String signAPP = createSign(sortJSPpay, platChannelMaster.getCpPaySecret());
        weiXinPayRespVo.setSign(signAPP);
        return weiXinPayRespVo;
    }

    public static String getRequestXml(String tradeType,String notifyUrl,PlatChannelMaster platChannelMaster,PlatOrderPre order)
            throws Exception{
        WxRequest wxRequest = createWxRequest(tradeType,notifyUrl,platChannelMaster,order);
        Map<String, String> map = BeanUtils.convertObjToMap(wxRequest);
        SortedMap<String, String> sort = new TreeMap<String, String>(map);
        String postXmlStr = tranRequestXml(sort);
        return  postXmlStr ;
    }

    public static WxResponse getWxResponseByRepXml(String xmlStr) throws Exception {
        Map<String, String> mapRest = WechatOrderHelp.weixinCallBack(xmlStr);
        WxResponse wxResponse = BeanUtils.convertMapToObject(mapRest,WxResponse.class);
        return wxResponse;
    }

    /**
     * 解析微信参数String格式的xml转map
     * @param xml
     * @return
     */
    public static Map<String,String> weixinCallBack(String xml) throws Exception{
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

    private static WxRequest createWxRequest(String tradeType,String notifyUrl,PlatChannelMaster platChannelMaster,PlatOrderPre order)
            throws Exception{

        WxRequest wxRequest = new WxRequest();
        wxRequest.setAppid(platChannelMaster.getCpAppId());//公众号ID
        wxRequest.setBody("游戏道具");//商品描述
        wxRequest.setMch_id(platChannelMaster.getCpPayId());//商户号
        wxRequest.setNonce_str(RandomChars.getRandomChars(32));//32位随机字符串
        wxRequest.setNotify_url(notifyUrl);//通知地址
        wxRequest.setOpenid("");//用户微信ID
        //订单号
        wxRequest.setOut_trade_no(order.getOrderNo());//订单号，24位
        wxRequest.setSpbill_create_ip(order.getIp());//IP
        wxRequest.setTotal_fee(order.getMoney()+"");//充值金额，单位分
        wxRequest.setTrade_type(tradeType);//订单类型
        wxRequest.setAttach(order.getUserId()+ "_" + order.getOrderNo());//自定义参数
        Map<String, String> wxRequestMap = BeanUtils.convertObjToMap(wxRequest);
        SortedMap<String, String> sortWxRequest = new TreeMap<String, String>(wxRequestMap);
        //获取签名
        String sign = createSign(sortWxRequest, platChannelMaster.getCpPaySecret());
        log.info("微信下单统一下单签名：" + sign);
        wxRequest.setSign(sign);//签名
        return wxRequest;
    }

    /**
     * 微信充值专用--map转xml格式的String
     * @param parameters
     * @return
     */
    private static String tranRequestXml(SortedMap<String,String> parameters){
        StringBuffer sb = new StringBuffer();
        sb.append("<xml>");
        Set es = parameters.entrySet();
        Iterator it = es.iterator();
        String sign="";
        while(it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            String k = (String)entry.getKey();
            String v = (String)entry.getValue();
            if(v!=null){
                if ("attach".equalsIgnoreCase(k)||"body".equalsIgnoreCase(k)) {
                    sb.append("<"+k+">"+"<![CDATA["+v+"]]></"+k+">");
                }else if("sign".equalsIgnoreCase(k)){
                    sign="<"+k+">"+"<![CDATA["+v+"]]></"+k+">";
                }
                else {
                    sb.append("<"+k+">"+v+"</"+k+">");
                }
            }
        }
        sb.append(sign);
        sb.append("</xml>");
        return sb.toString();
    }

    /**
     * 自动生成签名
     * @param parameters
     * @return
     * @throws Exception
     */
    public static String createSign(SortedMap<String,String> parameters,String key) throws Exception{
        StringBuffer sb = new StringBuffer();
        Set es = parameters.entrySet();
        Iterator it = es.iterator();
        while(it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            String k = (String)entry.getKey();
            String v = (String)entry.getValue();
            if("packageValue".equals(k)){
                k = "package";
            }
            if(null != v && !"".equals(v)
                    && !"sign".equals(k) && !"key".equals(k)) {
                sb.append(k + "=" + v + "&");
            }

        }
        sb.append("key=" + key);//xxxxxx换成你的API密钥
        log.info("加密字符串："+sb.toString());
        String sign = Md5Utils.md5(sb.toString()).toUpperCase();
        return sign;
    }
}
