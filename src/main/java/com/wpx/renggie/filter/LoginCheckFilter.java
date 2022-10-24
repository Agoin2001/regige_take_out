package com.wpx.renggie.filter;

import com.alibaba.fastjson.JSON;
import com.wpx.renggie.common.Result;
import com.wpx.renggie.utils.BaseContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;


import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@WebFilter(filterName = "LoginCheckFilter",urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {

    //路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER=new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest request1=(HttpServletRequest) request;
        HttpServletResponse response1=(HttpServletResponse) response;
       // log.info("拦截到请求：{}",request1.getRequestURI());


//        1、获取本次请求的URI
        final String requestURI = request1.getRequestURI();

//        定义不需要处理的请求路径
        String[] urls=new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/user/sendMsg",
                "/user/login"
        };

        // 2、判断本次请求是否需要处理
        final boolean check = check(urls, requestURI);

        // 3、如果不需要处理，则直接放行
        if (check){
         //   log.info("本次请求{}不需要处理",requestURI);
            chain.doFilter(request1,response1);
            return;
        }
        // 4-1、判断登录状态，如果已登录，则直接放行
        if (request1.getSession().getAttribute("employee")!=null) {
          //  log.info("用户已登录，用户id为：{}",request1.getSession().getAttribute("employee"));

            final long id = Thread.currentThread().getId();
            log.info("线程id为{}",id);

            Long empId= (Long) request1.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);

            chain.doFilter(request1,response1);
            return;
        }

        //        4-2、判断登录状态，如果已登录，则直接放行
        if (request1.getSession().getAttribute("user") != null) {
            log.info("用户已登录，用户id为：{}", request1.getSession().getAttribute("user"));

            Long userId= (Long) request1.getSession().getAttribute("user");

            BaseContext.setCurrentId(userId);

            chain.doFilter(request, response);
            return;
        }

        //   log.info("用户未登录");

//        5、如果未登录则返回未登录结果,通过输出流向客户端页面响应数据
        response1.getWriter().write(JSON.toJSONString(Result.error("NOTLOGIN")));

        return;
    }

    //路径匹配，检查本次请求是否需要放行
    public boolean check(String[] urls,String requestURI) {
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if (match == true) {
                return true;
            }

        }
        return  false;
    }
}
