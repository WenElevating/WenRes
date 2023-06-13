package com.wmm.filter;

import com.alibaba.fastjson.JSON;
import com.wmm.common.R;
import com.wmm.utils.BaseContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
@Component
public class LoginCheckFilter implements Filter {
    // 路径匹配器
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // 获取本次请求的URI
        String requestURI = request.getRequestURI();

        log.info("uri = {}",requestURI);

        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/emailCode",
                "/user/login"
        };

        boolean check = checkURI(requestURI,urls);

        log.info("校验结果：{}",check);

        // 不在拦截范围，直接放行
        if(check){
            filterChain.doFilter(request,response);
            return;
        }

        // 在拦截范围且session不为空，已登录正常放行
        if(request.getSession().getAttribute("employee") != null){
            Long empId = (Long) request.getSession().getAttribute("employee");

            log.info("employeeId = {}",empId);

            BaseContext.setCurrentId(empId);

            filterChain.doFilter(request,response);
            return;
        }
        // 移动端，已登录正常放行
        if(request.getSession().getAttribute("user") != null){

            Long userId = (Long) request.getSession().getAttribute("user");

            log.info("userId = {}",userId);

            BaseContext.setCurrentId(userId);

            filterChain.doFilter(request,response);

            return;
        }

        // session为空，返回错误信息，进行页面跳转
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
    }

    
    public boolean checkURI(String requestURI,String[]urls){
        for (String url:urls){
            if(PATH_MATCHER.match(url,requestURI)){
                return true;
            }
        }
        return false;
    }
}
