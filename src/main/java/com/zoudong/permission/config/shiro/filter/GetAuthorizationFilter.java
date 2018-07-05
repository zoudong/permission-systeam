package com.zoudong.permission.config.shiro.filter;

import com.zoudong.permission.config.shiro.JwtAuthenticationToken;
import com.zoudong.permission.config.shiro.ShiroThreadLocal;
import com.zoudong.permission.utils.jwt.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
public class GetAuthorizationFilter implements Filter {


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if(WebUtils.toHttp(servletRequest).getHeader(AUTHORIZATION)!=null) {
            ShiroThreadLocal.AUTHORIZATIONKEY.set(WebUtils.toHttp(servletRequest).getHeader(AUTHORIZATION));
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }
}
