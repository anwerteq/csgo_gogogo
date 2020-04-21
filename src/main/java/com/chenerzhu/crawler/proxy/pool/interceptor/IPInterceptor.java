package com.chenerzhu.crawler.proxy.pool.interceptor;

import com.chenerzhu.crawler.proxy.pool.util.IPUtils;
import com.chenerzhu.crawler.proxy.pool.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created Date by 2020/4/21 0021.
 *
 * @author Parker
 */
@Slf4j
public class IPInterceptor implements HandlerInterceptor{

    /** Redis ip key */
    private static final String REDIS_IP_KEY = "ip_white_list";

    @Value("${ip-interceptor.errorMsg}")
    private String errorMsg;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //过滤ip,若用户在白名单内，则放行
        String ipAddress= IPUtils.getRealIP(request);
        log.info("USER IP ADDRESS IS => {}",ipAddress);
        if(!StringUtils.isNotBlank(ipAddress)) {
            return false;
        }

        // 等于 本地IP 直接放行
        if("127.0.0.1".equals(ipAddress) || "localhost".equals(ipAddress)){
            return true;
        }

        // 白名单是否存在
        boolean flag = RedisUtil.sHasKey(REDIS_IP_KEY, ipAddress);
        if(!flag){
            response.setHeader("Content-type", "text/html;charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().append("<h1 style=\"text-align:center;\">"+errorMsg+"</h1>");
            return false;
        }
        return true;
    }


    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }


}
