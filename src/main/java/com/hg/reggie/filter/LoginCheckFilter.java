package com.hg.reggie.filter;


import com.alibaba.fastjson.JSON;
import com.hg.reggie.common.BaseContext;
import com.hg.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author hougen
 * @program Reggie
 * @description 登录过滤器
 * @create 2022-11-13 14:15
 */
@WebFilter(filterName = "LoginCheckFilter",urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {
    /**
     * 路径匹配器,支持通配符
     */
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        //向上转型
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //获取本次请求路径
        String requestURI = request.getRequestURI();
        log.info("截取到  {}  请求...",requestURI);

        //定义放行请求的路径
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/user/sendMsg",
                "/user/login"
        };

        //是否放行
        if ( this.check(urls,requestURI) ) {
            filterChain.doFilter(request, response);
            return;
        }

        //如果登录直接放行
        if ( request.getSession().getAttribute("employee") != null ) {
            log.info("当前员工已登录       [id={}]",request.getSession().getAttribute("employee"));

            //将当前登录的员工id存放到treadLocal中
            BaseContext.setCurrentId( (Long)request.getSession().getAttribute("employee"));

            filterChain.doFilter(request, response);
            return;
        }

        if ( request.getSession().getAttribute("user") != null ) {
            log.info("当前用户已登录       [id={}]",request.getSession().getAttribute("user"));

            //将当前登录的用户id存放到treadLocal中
            BaseContext.setCurrentId( (Long)request.getSession().getAttribute("user"));

            filterChain.doFilter(request, response);
            return;
        }

        log.info("用户没有登录...");
        //没有登陆,通过输出流方式向客户端页面响应数据
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
    }


    /**
     * 路径匹配,检查本次请求是否需要放行
     * @param urls
     * @return
     */
    public boolean check(String[] urls,String requestUri){
        for (String url : urls) {
            if ( PATH_MATCHER.match(url,requestUri) ) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("LoginCheckFilter启动...");
    }

    @Override
    public void destroy() {
        log.info("LoginCheckFilter销毁...");
    }
}


