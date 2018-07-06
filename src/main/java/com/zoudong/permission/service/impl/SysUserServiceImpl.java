package com.zoudong.permission.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zoudong.permission.config.shiro.JwtAuthenticationToken;
import com.zoudong.permission.constant.JwtConstant;
import com.zoudong.permission.exception.BusinessException;
import com.zoudong.permission.mapper.SysUserMapper;
import com.zoudong.permission.model.SysRole;
import com.zoudong.permission.model.SysUser;
import com.zoudong.permission.param.user.login.SysUserLoginParam;
import com.zoudong.permission.param.user.query.QuerySysUserParam;
import com.zoudong.permission.service.api.SysUserService;
import com.zoudong.permission.utils.jwt.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zd
 * @description class
 * @date 2018/6/15 17:41
 */
@Slf4j
@Service
public class SysUserServiceImpl implements SysUserService {
    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public PageInfo<SysUser> queryAllSysUser(QuerySysUserParam querySysUserParam) throws Exception {
        PageHelper.startPage(querySysUserParam.getPageNum(), querySysUserParam.getPageSize());
        List<SysUser> sysUserList = sysUserMapper.selectAll();
        PageInfo<SysUser> pageInfo = new PageInfo<>(sysUserList);
        return pageInfo;
    }

    @Override
    public String apiLogin(SysUserLoginParam sysUserLoginParam) throws Exception {

        String account = sysUserLoginParam.getAccount();
        //登录成功后签发token
        if (account == null) {
            log.info("shiro Realm获取用户名失败！");
            throw new UnauthorizedException();
        }
        //token.getCredentials();
        SysUser sysUser = new SysUser();
        sysUser.setAccount(account);
        SysUser userInfo = sysUserMapper.selectOne(sysUser);
        if (userInfo == null) {
            log.info("shiro Realm获取用户信息失败！");
            throw new UnauthorizedException();
        }
        if(!sysUserLoginParam.getPassword().equals(userInfo.getPassword())){
            throw new UnauthorizedException();
        }
        JSONObject jo = new JSONObject();
        jo.put("userId", userInfo.getId());
        jo.put("account", userInfo.getAccount());

        String token = jwtUtil.createJWT(jwtUtil.generalKey().toString(), jo.toJSONString(), JwtConstant.JWT_TTL);
        return token;
    }
}
