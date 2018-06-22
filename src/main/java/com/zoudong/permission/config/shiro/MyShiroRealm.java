package com.zoudong.permission.config.shiro;

import com.zoudong.permission.mapper.*;
import com.zoudong.permission.model.*;
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
    public boolean supports(AuthenticationToken token) {
        //仅支持JwtAuthenticationToken类型的Token
        return token instanceof JwtAuthenticationToken;
    }

    public MyShiroRealm() {
       /* // 认证
        super.setAuthenticationCachingEnabled(true);
        // 授权
        super.setAuthorizationCacheName("test");*/
    }

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    @Autowired
    private SysRolePermissionMapper sysRolePermissionMapper;

    @Autowired
    private SysPermissionMapper sysPermissionMapper;

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
            //填充角色
            authorizationInfo.addRole(role.getRoleCode());
            //权限去重
            List<SysPermission> sysPermissionList = removeDuplicate(role.getSysPermissions());
            //填充权限
            for (SysPermission p : sysPermissionList) {
                authorizationInfo.addStringPermission(p.getPermissionCode());
            }
        }
        return authorizationInfo;
    }

    /**
     * 验证用户加载权限（空了缓存到redis）
     *
     * @param token
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token)
            throws AuthenticationException {
        //获取用户的输入的账号.
        String account = (String) token.getPrincipal();

        if (account == null) {
            log.info("shiro Realm获取用户名失败！");
            return null;
        }
        //token.getCredentials();
        SysUser sysUser = new SysUser();
        sysUser.setAccount(account);
        SysUser userInfo = sysUserMapper.selectOne(sysUser);
        if (userInfo == null) {
            log.info("shiro Realm获取用户信息失败！");
            return null;
        }

        if (userInfo.getStatus() == 1) { //账户冻结
            throw new LockedAccountException();
        }

        //找角色关联
        Example example = new Example(SysUserRole.class);
        example.createCriteria().andEqualTo("userId", userInfo.getId());
        List<SysUserRole> sysUserRoleList = sysUserRoleMapper.selectByExample(example);
        if (sysUserRoleList.isEmpty()) {
            log.info("shiro Realm加载用户角色信息失败！");
            return null;
        }
        List<Long> roleIds = new ArrayList<>();
        for (SysUserRole sysUserRole : sysUserRoleList) {
            roleIds.add(sysUserRole.getId());
        }

        //找角色详情信息
        Example roleExample = new Example(SysRole.class);
        roleExample.createCriteria().andIn("id", roleIds);
        List<SysRole> sysRoles = sysRoleMapper.selectByExample(roleExample);
        if (sysRoles.isEmpty()) {
            log.info("shiro Realm加载用户角色详情信息失败！");
            return null;
        }


        for (SysRole sysRole : sysRoles) {

            //找角色权限关联信息
            Example rolePermissionExample = new Example(SysRolePermission.class);
            rolePermissionExample.createCriteria().andEqualTo("roleId", sysRole.getId());
            List<SysRolePermission> sysRolePermissions = sysRolePermissionMapper.selectByExample(rolePermissionExample);
            if (sysRolePermissions.isEmpty()) {
                log.info("shiro Realm加载用户权限详情信息失败！");
                return null;
            }

            List<Long> permissionIds = new ArrayList<>();
            for (SysRolePermission sysRolePermission : sysRolePermissions) {
                permissionIds.add(sysRolePermission.getPermissionId());
            }

            Example permissionExample = new Example(SysPermission.class);
            permissionExample.createCriteria().andIn("id", permissionIds);
            List<SysPermission> sysPermissions = sysPermissionMapper.selectByExample(permissionExample);

            sysRole.setSysPermissions(sysPermissions);
        }


        userInfo.setRoleList(sysRoles);

        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(
                userInfo, //用户信息
                token.getCredentials(), //密码
                getName()  //realm name  
        );
        return authenticationInfo;
    }

    /**
     * 通过某个属性去重复对象
     *
     * @param list
     * @return
     */
    public static List<SysPermission> removeDuplicate(List<SysPermission> list) {
        for (int i = 0; i < list.size() - 1; i++) {
            for (int j = list.size() - 1; j > i; j--) {
                if (list.get(j).getPermissionCode().equals(list.get(i).getPermissionCode())) {
                    list.remove(j);
                }
            }
        }
        return list;
    }

}  