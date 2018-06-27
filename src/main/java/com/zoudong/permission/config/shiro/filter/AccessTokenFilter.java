package com.zoudong.permission.config.shiro.filter;

import com.alibaba.fastjson.JSONObject;
import com.zoudong.permission.config.shiro.JwtAuthenticationToken;
import com.zoudong.permission.constant.JwtConstant;
import com.zoudong.permission.utils.jwt.JwtUtil;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.apache.shiro.web.filter.authz.AuthorizationFilter;
import org.apache.shiro.web.util.WebUtils;
import sun.misc.BASE64Decoder;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
public class AccessTokenFilter extends AccessControlFilter {

    @Override
    protected boolean isAccessAllowed(ServletRequest servletRequest, ServletResponse servletResponse, Object o) throws Exception {

        JwtAuthenticationToken token = JwtUtil.createToken(servletRequest, servletResponse);
        try {
            Subject subject = getSubject(servletRequest, servletResponse);
            subject.login(token);//认证
        } catch (AuthenticationException e) {//认证失败，发送401状态并附带异常信息
            log.error(e.getMessage(),e);
            WebUtils.toHttp(servletResponse).sendError(HttpServletResponse.SC_UNAUTHORIZED,e.getMessage());
            return false;
        }

        if (null != getSubject(servletRequest, servletResponse)
                && getSubject(servletRequest, servletResponse).isAuthenticated()&&permsCheck((String[]) o, getSubject(servletRequest, servletResponse))) {
            return true;
        }
        WebUtils.toHttp(servletResponse).sendError(HttpServletResponse.SC_UNAUTHORIZED,"token认证未通过");
        return false;
    }


    @Override
    protected boolean onAccessDenied(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        return false;

    }


    private boolean permsCheck(String[] mappedValue, Subject subject) {
        String[] perms = mappedValue;
        boolean isPermitted = true;
        if (perms != null && perms.length > 0) {
            if (perms.length == 1) {
                if (!subject.isPermitted(perms[0])) {
                    isPermitted = false;
                }
            } else {
                if (!subject.isPermittedAll(perms)) {
                    isPermitted = false;
                }
            }
        }
        return isPermitted;
    }


}
