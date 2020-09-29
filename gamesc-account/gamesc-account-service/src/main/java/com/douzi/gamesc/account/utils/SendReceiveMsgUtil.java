package com.douzi.gamesc.account.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

import com.esms.MOMsg;
import com.esms.MessageData;
import com.esms.PostMsg;
import com.esms.common.entity.Account;
import com.esms.common.entity.AccountInfo;
import com.esms.common.entity.GsmsResponse;
import com.esms.common.entity.MTPack;
import com.esms.common.entity.MTPack.MsgType;
import com.esms.common.entity.MTPack.SendType;



/**
 * 
 *  Class Name: SendMsg.java
 *  Function:玄武短信接口
 *  
 *     Modifications:   
 *  
 *  @author dul  DateTime 2014-4-1 上午9:17:50    
 *  @version 1.0
 */
public class SendReceiveMsgUtil {

    public static Account getAccount(String account,String password){
        return new Account(account, password);
    }
	/**
	 * 
	 *  Function:设置上行下行端口
	 * 
	 *  @author dul  DateTime 2014-3-31 上午10:33:34
	 *  @return
	 */
	public static PostMsg getPostMsg(){
		PostMsg pm = new PostMsg();
		pm.getCmHost().setHost("211.147.239.62",9080);//设置下行端口
		pm.getWsHost().setHost("211.147.239.62",9070);//设置上行端口
		return pm;
	}

	private static MTPack getMTPack(String[] mobiles,String msgContent,String customNum){
		MTPack pack = new MTPack();
		pack.setBatchID(UUID.randomUUID());
		pack.setBatchName("短信发送");
		pack.setMsgType(MsgType.SMS);
		pack.setBizType(1);
		//一个内容多个号码
		pack.setSendType(SendType.MASS);
		pack.setDistinctFlag(false);
		if(customNum!=null){
			pack.setCustomNum(customNum);//添加扩展码
		}
		ArrayList<MessageData> msgs = new ArrayList<MessageData>();
		//设置短息内容
		String content = msgContent;
		//设置号码的正则表达式
		Pattern pattern=Pattern.compile("[0-9]*");
		for (int i = 0; i < mobiles.length; i++) {
			if(mobiles[i]!=null && mobiles[i].trim()!=null &&pattern.matcher(mobiles[i].trim()).matches()==true){//校验手机号
				String mobile = mobiles[i];
				msgs.add(new MessageData(mobile, content));
			}
		}
		//设置信息
		pack.setMsgs(msgs);
		return pack;
	}

	/**
	 * 
	 *  Function:发送信息，一个内容对应多个手机号
	 * 
	 *  @author dul  DateTime 2014-3-31 上午10:34:06
	 *  @param mobiles
	 *  @param msgContent
	 *  @return
	 *  @throws Exception
	 */
	public static int md5SendSMSPost(String[] mobiles,String msgContent,String account,String password) throws Exception{
		
		//获取账号
		Account ac = getAccount(account,password);
		//设置上行下行端口
		PostMsg pm = SendReceiveMsgUtil.getPostMsg();
		//设置短信内容以及要发送的手机号
        MTPack pack = SendReceiveMsgUtil.getMTPack(mobiles,msgContent,null );
		//发送信息
	    GsmsResponse resp= pm.post(ac, pack);
	    System.out.println(resp.toString()+"=======");
	    return resp.getAttributes().indexOf("<string>success_count</string>")>0?0:-1;
	}
	

	/**
	 * 
	 *  Function:获取账号余额
	 * 
	 *  @author dul  DateTime 2014-3-31 上午10:32:57
	 *  @return
	 *  @throws Exception
	 */
	public static Long getRemainFee(String account,String password) throws Exception{
		//获取账号
		Account ac = getAccount(account,password);
		//设置上行下行端口
		PostMsg pm = SendReceiveMsgUtil.getPostMsg();
		AccountInfo accountInfo = pm.getAccountInfo(ac);
		long balance = accountInfo.getBalance();
		return balance;
	}
	
	/**
	 * 
	 *  Function:获取玩家发送短信(定时刷新)
	 *  默认获取100条未读短信
	 *  @author dul  DateTime 2014-3-31 上午10:32:34
	 * @throws Exception
	 */
	public static MOMsg[] receiveSMS(String account,String password,int num) throws Exception{
		//获取账号
		Account ac = getAccount(account,password);
		//设置上行下行端口
		PostMsg pm = SendReceiveMsgUtil.getPostMsg();
		//获取上行短信,100表示读取100条未读短信
		MOMsg[] moMsgs = pm.getMOMsgs(ac, num);
		return moMsgs;
	}
	
	/**
	 * 
	 *  Function:修改密码
	 * 
	 *  @author dul  DateTime 2014-4-1 上午10:58:15
	 */
	public static void exchangePassword(String account,String password,String newPassword){
		//根据站点ID获取
		Account ac = getAccount(account,password);
		//设置上行下行端口
		PostMsg pm = SendReceiveMsgUtil.getPostMsg();
		try {
			pm.modifyPassword(ac, newPassword);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		try {
			String[] mobiles = {"13918506906"};
			int i= SendReceiveMsgUtil.md5SendSMSPost(mobiles,"8756（验证码）。官方不会以任何形式索要您的验证码和密码，请提高防范意识，注意账户安全。","gmdz@shad","02e0iCz9");
			System.out.println(i);
			/*long balance = SendReceiveMsgUtil.getRemainFee(100001);
			System.out.println(balance);*/
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
