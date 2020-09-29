package com.douzi.gamesc.user.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MobileCheckUtil {

	public static boolean isMobile(String mobile) {
		Pattern pattern = Pattern.compile("^[1][1-9]\\d{9}$");
		Matcher isNum = pattern.matcher(mobile);
		return isNum.matches();
	}

	public static boolean isEmail(String email) {
		if (null==email || "".equals(email)){
			return false;
		}
		String regEx1 = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
		Pattern p = Pattern.compile(regEx1);
		Matcher m = p.matcher(email);
		if(m.matches()){
			return true;
		}else{
			return false;
		}
	}
}
