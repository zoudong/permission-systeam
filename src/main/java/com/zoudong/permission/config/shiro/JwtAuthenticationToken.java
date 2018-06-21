package com.zoudong.permission.config.shiro;

import org.apache.shiro.authc.AuthenticationToken;

import java.util.Map;

/**
 * @author zd
 * @description class
 * @date 2018/6/21 10:41
 */
public class JwtAuthenticationToken implements AuthenticationToken {
    private String account;
    private String token;
    //private Map<String, ?> params;
    public JwtAuthenticationToken(String account,String token){
        this.account=account;
        this.token=token;
    }
    @Override
    public Object getPrincipal() {
        return account;
    }

    @Override
    public Object getCredentials() {
        return token;
    }
}
