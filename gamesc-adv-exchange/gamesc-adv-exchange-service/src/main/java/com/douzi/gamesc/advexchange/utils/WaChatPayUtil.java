package com.douzi.gamesc.advexchange.utils;

import com.alibaba.fastjson.JSONObject;
import com.douzi.gamesc.common.pojo.exhange.ExchangeRedbagRecord;
import com.douzi.gamesc.advexchange.utils.WXXStreamHelper;
import com.douzi.gamesc.advexchange.vo.WechatChange;
import com.douzi.gamesc.advexchange.vo.WechatChangeBack;
import com.douzi.gamesc.user.utils.DateExtendUtil;
import com.douzi.gamesc.user.utils.Md5Utils;
import com.douzi.gamesc.user.utils.RandomChars;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.security.KeyStore;
import java.util.Date;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;

public class WaChatPayUtil {

	static final Logger logger = LoggerFactory.getLogger(WaChatPayUtil.class);
	/**
	 *  APP生产企业零钱付款类
	 * @param userOpenId
	 * @param redBag
	 * @param config
	 * @return
	 */
	public static WechatChange createWechatChange(String userOpenId,ExchangeRedbagRecord redBag,JSONObject config)
	{
		WechatChange msgObj = new WechatChange();
		msgObj.setMchAppid(config.getString("appid"));
		msgObj.setMchId(config.getString("mchid"));
		msgObj.setNonceStr(RandomChars.getRandomChars(32).toUpperCase());
		msgObj.setPartnerTradeno(msgObj.getMchId()+redBag.getOrderNo());
		msgObj.setOpenid(userOpenId);
		msgObj.setCheckName("NO_CHECK");
		msgObj.setAmount(""+redBag.getMoney());
		msgObj.setSpbillCreateIp(redBag.getIp());
		msgObj.setDesc("福利红包");
		try {
			String param = msgObj.createParam(config.getString("mchkey"));
			logger.info(config.getString("mchkey")+"===="+param);
			msgObj.setSign(Md5Utils.md5(param).toUpperCase());
			logger.info("企业零钱参数："+msgObj.toString());
		} catch (Exception e1) {
			logger.error("企业零钱支付生产WechatChange异常:"+e1.getMessage());
			e1.printStackTrace();
		}
		return msgObj;
	}

	/**
	 * 生成xml数据
	 * @param pay
	 * @return
	 */
	public static String getXmlByWechatChange(WechatChange pay)
	{
		XStream xStream = WXXStreamHelper.createXstream();
		xStream.processAnnotations(WechatChange.class);
		String xmlInfo = xStream.toXML(pay);
		xmlInfo = xmlInfo.replaceAll("@", "_");
		return xmlInfo;
	}
	

	
	public static WechatChangeBack weChatChangeSend(String xmlStr,JSONObject config)
	{
		WechatChangeBack rs = new WechatChangeBack() ;
		try
		{
			KeyStore keyStore  = KeyStore.getInstance("PKCS12");
	        FileInputStream instream = new FileInputStream(new File(config.getString("sign_path")));
	        try {
	        	System.out.println(config.getString("mchid")+"------"+config.getString("sign_path")+"----------"+instream);
	            keyStore.load(instream, config.getString("mchid").toCharArray());
	        } catch(Exception e){
	        	e.printStackTrace();
	        }finally {
	            instream.close();
	        }
	        // Trust own CA and all self-signed certs
	        SSLContext sslcontext = SSLContexts.custom().loadKeyMaterial(keyStore, config.getString("mchid").toCharArray()).build();
	        // Allow TLSv1 protocol only
	        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
	                sslcontext,
	                new String[] { "TLSv1" },
	                null,
	                SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
	        CloseableHttpClient httpclient = HttpClients.custom()
	                .setSSLSocketFactory(sslsf)
	                .build();
	        try {
	        	
	            HttpPost post = new HttpPost(config.getString("small_change_path"));
	            System.out.println("executing request" + post.getRequestLine());
	            System.out.println(xmlStr);
	            StringEntity xmlEntity = new StringEntity(xmlStr,"UTF-8");
	            post.setEntity(xmlEntity);
	            post.setHeader("Content-Type","text/xml;charset=UTF-8");
	            CloseableHttpResponse response = httpclient.execute(post);
	            try {
	                HttpEntity entity = response.getEntity();
	                StringBuffer  backXml= new StringBuffer();
	                if (entity != null) {
	                    System.out.println("Response content length: " + entity.getContentLength());
	                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(entity.getContent(),"UTF-8"));
	                    String text;
	                    while ((text = bufferedReader.readLine()) != null) {
	                    	backXml.append(text);
	                    }
	                }
	                EntityUtils.consume(entity);
	                System.out.println(backXml.toString());
	                rs = WXXStreamHelper.resolvebackXml(backXml.toString(), WechatChangeBack.class);
	                System.out.println(rs.toString());
	            } finally {
	                response.close();
	            }
	        } finally {
	            httpclient.close();
	        }
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return rs;
	}
	

	public static void main(String[] args) {
		try
		{
			KeyStore keyStore  = KeyStore.getInstance("PKCS12");
	        FileInputStream instream = new FileInputStream(new File("F:/7pmi/apiclient_cert.p12"));
	        try {
	        	
	            keyStore.load(instream,"1247692301".toCharArray());
	            System.out.println(keyStore.toString());
	        } finally {
	            instream.close();
	        }
	        
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
