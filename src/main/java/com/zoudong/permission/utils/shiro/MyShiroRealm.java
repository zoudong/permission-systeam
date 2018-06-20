package com.zoudong.permission.utils.shiro;

import com.zoudong.permission.mapper.SysRoleMapper;
import com.zoudong.permission.mapper.SysUserMapper;
import com.zoudong.permission.mapper.SysUserRoleMapper;
import com.zoudong.permission.model.SysPermission;
import com.zoudong.permission.model.SysRole;
import com.zoudong.permission.model.SysUser;
import com.zoudong.permission.model.SysUserRole;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义Realm
 */
@Slf4j
@Component
public class MyShiroRealm extends AuthorizingRealm {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    /**
     * 获取用户装载权限
     *
     * @param principals
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        SysUser userInfo = (SysUser) principals.getPrimaryPrincipal();
        for (SysRole role : userInfo.getRoleList()) {
            authorizationInfo.addRole(role.getRoleCode());
            for (SysPermission p : role.getSysPermissions()) {
                authorizationInfo.addStringPermission(p.getPermissionCode());
            }
        }
        return authorizationInfo;
    }

    /**
     * 验证用户加载权限（空了缓存到redis）
     * @param token
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token)
            throws AuthenticationException {
        //获取用户的输入的账号.
        String account = (String) token.getPrincipal();
        if(account==null){
            log.info("shiro Realm获取用户名失败！");
            return null;
        }
        //token.getCredentials();
        SysUser sysUser = new SysUser();
        sysUser.setAccount(account);
        SysUser userInfo = sysUserMapper.selectOne(sysUser);
        if(userInfo==null){
            log.info("shiro Realm获取用户信息失败！");
            return null;
        }

        if (userInfo.getStatus() == 1) { //账户冻结
            throw new LockedAccountException();
        }

        //找角色关联
        Example example=new Example(SysUserRole.class);
        example.createCriteria().andEqualTo("userId",sysUser.getId());
        List<SysUserRole> sysUserRoleList=sysUserRoleMapper.selectByExample(example);
        if(sysUserRoleList.isEmpty()){
            log.info("shiro Realm加载用户角色信息失败！");
            return null;
        }
        List<Long> roleIds=new ArrayList<>();
        for(SysUserRole sysUserRole:sysUserRoleList){
            roleIds.add(sysUserRole.getId());
        }

        //找角色详情信息
        Example roleExample=new Example(SysRole.class);
        roleExample.createCriteria().andIn("sysRole",roleIds);
        List<SysRole> sysRoles=sysRoleMapper.selectByExample(roleExample);
        if(sysRoles.isEmpty()){
            log.info("shiro Realm加载用户角色详情信息失败！");
            return null;
        }

        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(
                userInfo, //用户信息
                userInfo.getPassword(), //密码  
                ByteSource.Util.bytes(userInfo.getAccount()),//salt=username+salt
                getName()  //realm name  
        );
        return authenticationInfo;
    }

}  