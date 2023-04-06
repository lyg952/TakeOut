package com.sasu.takeout.filter;



import com.alibaba.fastjson.JSON;
import com.sasu.takeout.common.Result;
import com.sasu.takeout.utils.BaseContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {

    //Spring路径匹配器，校验路径匹配
    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        //请求响应转化为Http请求与相应
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //设置不需要拦截的请求URI
        String[] url = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",   //放行静态资源的访问，使用通配符 需要配合AntPathMacth使用
                "/front/**",
                "/user/sendMsg",
                "/user/login"
        };
        //1.获取请求访问路径
        String requestURI = request.getRequestURI();
        log.info("拦截到请求：{}",requestURI);

        //2.判定访问路径是否放行,放行
        if (urlCheck(requestURI,url)){
            log.info("放行的请求有：{}",requestURI);
            filterChain.doFilter(request,response);
            return;
        }

        //3.判定是否为登录状态，登录状态放行
        if (request.getSession().getAttribute("employee") != null){
            log.info("用户已登录，用户id为:{}",request.getSession().getAttribute("employee"));

            Long empId = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);
            filterChain.doFilter(request,response);
            return;
        }

        if (request.getSession().getAttribute("user") != null){
            log.info("用户已登录，用户id为:{}",request.getSession().getAttribute("user"));

            Long userId = (Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);
            filterChain.doFilter(request,response);
            return;
        }

        //不为登录状态，页面跳转
        log.info("监测到session{}",request.getSession().getAttribute("employee"));
        log.info("用户未登录，页面访问失败,拦截到请求{}",requestURI);
        response.getWriter().write(JSON.toJSONString(Result.error("NOTLOGIN")));

    }

    /**
     * 判断访问路径是否放行
     * @param requestURI
     * @param urls
     * @return
     */
    private boolean urlCheck(String requestURI,String[] urls) {
        log.info("进入到url访问路径匹配、、、、、、、、、{}",requestURI);
        for (String s : urls) {
            boolean match = PATH_MATCHER.match(s, requestURI);
            if (match) {
                log.info("此次路径匹配成功：应该放行路径为：{}，，此次请求路径为：{}",s,requestURI);
                return true;
            }
            log.info("此次路径匹配不成功：应该放行路径为：{}，，此次请求路径为：{}",s,requestURI);
        }
        log.info("路径匹配失败，拦截该路径:{}",requestURI);
        return false;
    }


}
