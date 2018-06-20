package com.zoudong.permission.config.shiro.filter;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.apache.shiro.web.filter.authz.AuthorizationFilter;
import sun.misc.BASE64Decoder;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

@Slf4j
public class AccessTokenFilter extends AccessControlFilter {

    @Override
    protected boolean isAccessAllowed(ServletRequest servletRequest, ServletResponse servletResponse, Object o) throws Exception {
        if (null != getSubject(servletRequest, servletResponse)
                && getSubject(servletRequest, servletResponse).isAuthenticated()) {
            return true;
        }
        return false;
       /* HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String auth = (String) httpServletRequest.getAttribute("Authorization");
        System.out.println("auth encoded in base64 is " + getFromBASE64(auth));
        if ((auth != null) && (auth.length() > 6)) {
            auth = auth.substring(6, auth.length());
            String decodedAuth = getFromBASE64(auth);
            System.out.println("auth decoded from base64 is " + decodedAuth);
            return true;
        } else {
            return false;
        }*/
    }

    @Override
    protected boolean onAccessDenied(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {

        return false;
    }

    private String getFromBASE64(String s) {
        if (s == null)
            return null;
        BASE64Decoder decoder = new BASE64Decoder();
        try {
            byte[] b = decoder.decodeBuffer(s);
            return new String(b);
        } catch (Exception e) {
            return null;
        }
    }


}
