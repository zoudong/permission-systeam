package com.zoudong.permission.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zoudong.permission.mapper.SysUserMapper;
import com.zoudong.permission.model.SysUser;
import com.zoudong.permission.param.user.query.QuerySysUserParam;
import com.zoudong.permission.service.api.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zd
 * @description class
 * @date 2018/6/15 17:41
 */
@Service
public class SysUserServiceImpl implements SysUserService {
    @Autowired
    private SysUserMapper sysUserMapper;
    @Override
    public PageInfo<SysUser> queryAllSysUser(QuerySysUserParam querySysUserParam) throws Exception {
        PageHelper.startPage(querySysUserParam.getPageNum(),querySysUserParam.getPageSize());
        List<SysUser> sysUserList=sysUserMapper.selectAll();
        PageInfo<SysUser> pageInfo=new PageInfo<>(sysUserList);
        return pageInfo;
    }
}
