package com.douzi.gamesc.http;

import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;

public class IpUtils {

    static String UNKNOWN_IP = "unknown";

    public static String getIpAddr(HttpServletRequest paramHttpServletRequest)
    {
        String str = null;
        str = paramHttpServletRequest.getHeader("X-Forwarded-For");
        if (isRealIP(str))
            return getRealIp(str);
        str = paramHttpServletRequest.getHeader("Proxy-Client-IP");
        if (isRealIP(str))
            return getRealIp(str);
        str = paramHttpServletRequest.getHeader("WL-Proxy-Client-IP");
        if (isRealIP(str))
            return getRealIp(str);
        str = paramHttpServletRequest.getHeader("HTTP_CLIENT_IP");
        if (isRealIP(str))
            return getRealIp(str);
        str = paramHttpServletRequest.getHeader("HTTP_X_FORWARDED_FOR");
        if (isRealIP(str))
            return getRealIp(str);
        str = paramHttpServletRequest.getParameter("__fromReferIP");
        if (isRealIP(str))
            return getRealIp(str);
        str = paramHttpServletRequest.getHeader("X-Real-IP");
        if (isRealIP(str))
            return getRealIp(str);
        str = paramHttpServletRequest.getRemoteAddr();
        return str;
    }
    public static boolean isRealIP(String paramString)
    {
        return (StringUtils.isNotBlank(paramString)) && (!UNKNOWN_IP.equalsIgnoreCase(paramString));
    }

    public static String getRealIp(String paramString)
    {
        if (paramString.indexOf(",") != -1)
            return StringUtils.left(paramString.split(",")[0], 15);
        return paramString;
    }
}
