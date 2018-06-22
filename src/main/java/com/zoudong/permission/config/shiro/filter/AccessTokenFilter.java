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
        if (null != getSubject(servletRequest, servletResponse)
                && getSubject(servletRequest, servletResponse).isAuthenticated()) {
            return true;
        }
       return false;
    }


    @Override
    protected boolean onAccessDenied(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        JwtAuthenticationToken token = createToken(servletRequest, servletResponse);
        try {
            Subject subject = getSubject(servletRequest, servletResponse);
            subject.login(token);//认证
            return true;//认证成功，过滤器链继续
        } catch (AuthenticationException e) {//认证失败，发送401状态并附带异常信息
            log.error(e.getMessage(),e);
            WebUtils.toHttp(servletResponse).sendError(HttpServletResponse.SC_UNAUTHORIZED,e.getMessage());
            return false;
        }

    }

    JwtAuthenticationToken createToken(ServletRequest servletRequest, ServletResponse servletResponse){
        String jwt = WebUtils.toHttp(servletRequest).getHeader(AUTHORIZATION);
        String account=null;
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(DatatypeConverter.parseBase64Binary(JwtConstant.JWT_SECRET))
                    .parseClaimsJws(jwt)
                    .getBody();
           account= JSONObject.parseObject(claims.getSubject()).getString("account");// 用户名
        } catch (ExpiredJwtException e) {
            throw new AuthenticationException("JWT 令牌过期:" + e.getMessage());
        } catch (UnsupportedJwtException e) {
            throw new AuthenticationException("JWT 令牌无效:" + e.getMessage());
        } catch (MalformedJwtException e) {
            throw new AuthenticationException("JWT 令牌格式错误:" + e.getMessage());
        } catch (SignatureException e) {
            throw new AuthenticationException("JWT 令牌签名无效:" + e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new AuthenticationException("JWT 令牌参数异常:" + e.getMessage());
        } catch (Exception e) {
            throw new AuthenticationException("JWT 令牌错误:" + e.getMessage());
        }

        JwtAuthenticationToken jwtAuthenticationToken=new JwtAuthenticationToken(account,jwt);

        return jwtAuthenticationToken;
    }

}
