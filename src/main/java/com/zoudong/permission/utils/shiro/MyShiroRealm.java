package com.zoudong.permission.utils.shiro;

import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;

import javax.annotation.Resource;

public class MyShiroRealm extends AuthorizingRealm {
/*    @Resource
    private UserInfoService userInfoService;  */

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
       /* UserInfo userInfo = (UserInfo) principals.getPrimaryPrincipal();
        for (SysRole role : userInfo.getRoleList()) {  
            authorizationInfo.addRole(role.getRole());  
            for (SysPermission p : role.getPermissions()) {  
                authorizationInfo.addStringPermission(p.getPermission());  
            }  
        } */
        return authorizationInfo;
    }

    /*主要是用来进行身份认证的，也就是说验证用户输入的账号和密码是否正确。*/
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token)
            throws AuthenticationException {
        //获取用户的输入的账号.
        String username = (String) token.getPrincipal();
        //token.getCredentials();
/*      UserInfo userInfo = userInfoService.findByUsername(username);
        if (userInfo == null) {
            return null;
        }
        if (userInfo.getState() == 1) { //账户冻结  
            throw new LockedAccountException();
        }
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(
                userInfo, //用户名  
                userInfo.getPassword(), //密码  
                ByteSource.Util.bytes(userInfo.getCredentialsSalt()),//salt=username+salt
                getName()  //realm name  
        );*/
        //return authenticationInfo;
        return null;
    }

}  