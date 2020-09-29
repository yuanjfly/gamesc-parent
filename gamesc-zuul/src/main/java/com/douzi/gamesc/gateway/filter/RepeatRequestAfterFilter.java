package com.douzi.gamesc.gateway.filter;

import com.douzi.gamesc.utils.RedisUtils;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;


@Component
public class RepeatRequestAfterFilter extends ZuulFilter {

    private static final Logger logger = LoggerFactory.getLogger(RepeatRequestFilter.class);

    @Autowired(required = false)
    private RedisUtils redisUtils;

    @Override
    public String filterType() {
        return FilterConstants.POST_TYPE;
    }

    @Override
    public int filterOrder() {
        return -6;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        if (request.getAttribute(RepeatRequestFilter.REPEAT_FLAG) != null) {
            String key = request.getAttribute(RepeatRequestFilter.REPEAT_FLAG).toString();
            redisUtils.del(key);
            request.removeAttribute(RepeatRequestFilter.REPEAT_FLAG);
        }
        return null;
    }
}
