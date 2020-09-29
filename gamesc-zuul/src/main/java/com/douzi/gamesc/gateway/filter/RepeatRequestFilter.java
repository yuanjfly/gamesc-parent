package com.douzi.gamesc.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.douzi.gamesc.http.IpUtils;
import com.douzi.gamesc.user.utils.Md5Utils;
import com.douzi.gamesc.utils.RedisUtils;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.http.ServletInputStreamWrapper;
import java.io.IOException;
import java.util.Map;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RepeatRequestFilter extends ZuulFilter {

    public static final String REPEAT_FLAG = "REPEAT_FLAG";


    @Autowired(required = false)
    private RedisUtils redisUtils;

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return -5;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        final RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        String url = request.getRemoteAddr() + request.getServletPath();
        try {
            String params = null;
            //表单中获取参数
            Map<String, String[]> parameterMap = request.getParameterMap();
            if (!parameterMap.isEmpty()) {
                params = JSON.toJSONString(parameterMap);
            }
            //body中获取参数
            String bodyParma = "";
            if (!ctx.isChunkedRequestBody()) {
                ServletInputStream inp = ctx.getRequest().getInputStream();
                if (inp != null) {
                    bodyParma = IOUtils.toString(inp, "utf-8");
                }
            }
            if (StringUtils.isEmpty(params)) {
                params = bodyParma;
            } else {
                params += bodyParma;
            }
            //取出后放回body参数
            if(!StringUtils.isEmpty(bodyParma)){
                byte[] requestEntityBytes = bodyParma.getBytes("utf-8");
                ctx.setRequest(new HttpServletRequestWrapper(ctx.getRequest()) {
                    @Override
                    public ServletInputStream getInputStream() throws IOException {
                        return new ServletInputStreamWrapper(requestEntityBytes);
                    }
                    @Override
                    public int getContentLength() {
                        return requestEntityBytes.length;
                    }
                    @Override
                    public long getContentLengthLong() {
                        return requestEntityBytes.length;
                    }
                });
            }
            String md5Key = Md5Utils.md5(url + params);
            String ip = IpUtils.getIpAddr(request);
            String key = "request:"+ip+":"+md5Key;
            request.setAttribute(REPEAT_FLAG, key);
            Object keyValue = redisUtils.get(key);
            if (keyValue==null||StringUtils.isEmpty((String)keyValue)) {
                redisUtils.set(key, md5Key, 5L);//默认5秒后可以再次请求同一接口
            }else {
                JSONObject apiResult = new JSONObject();
                apiResult.put("code","0");
                apiResult.put("message","您的请求太频繁，请稍后尝试！");
                ctx.getResponse().setCharacterEncoding("utf-8");
                ctx.setSendZuulResponse(false);// 过滤该请求，不对其进行路由
                ctx.setResponseStatusCode(200);// 返回错误码
                ctx.setResponseBody(apiResult.toJSONString());// 返回错误内容
                ctx.set("isSuccess", false);
                log.info("请求过于频繁");
                log.info("客户地址:{}  请求地址：{} 请求方式 {}", request.getRemoteHost(), request.getRequestURL().toString(), request.getMethod());
                log.info("params:{} ", params);
                return null;
            }
        } catch (Exception e) {
            log.warn("{}重复请求校验出错：{}", request.getRequestURL(), e.getMessage());
        }
        return null;
    }
}
