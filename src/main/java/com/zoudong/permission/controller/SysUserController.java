package com.zoudong.permission.controller;

import com.github.pagehelper.PageInfo;
import com.zoudong.permission.config.shiro.JwtAuthenticationToken;
import com.zoudong.permission.exception.BusinessException;
import com.zoudong.permission.model.SysUser;
import com.zoudong.permission.param.user.login.SysUserLoginParam;
import com.zoudong.permission.param.user.query.QuerySysUserParam;
import com.zoudong.permission.result.ResultUtil;
import com.zoudong.permission.service.api.SysUserService;
import com.zoudong.permission.utils.jwt.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.mgt.SubjectFactory;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Map;

/**
 * @author zd
 * @description class
 * @date 2018/6/4 17:47
 */
@Slf4j
@RestController
public class SysUserController {
    @Autowired
    private SysUserService sysUserService;

    /**
     * 未登录，shiro应重定向到登录界面，此处返回未登录状态信息由前端控制跳转页面
     * @return
     */
    @RequestMapping(value = "/permission/unAuth")
    @ResponseBody
    public Object unauth() {
        throw new BusinessException("unAuth","token认证失败,请重新登录。");
    }

    /*@RequiresRoles("1")*/
    @RequestMapping(value = "/permission/querySysUserByPage", method = RequestMethod.POST)
    public Object test(@Valid @RequestBody QuerySysUserParam querySysUserParam, HttpServletRequest request, HttpServletResponse response)throws Exception {
       /* try {*/
        log.info("开始分页查询全部用户:{}", querySysUserParam);
        PageInfo<SysUser> pageInfo = sysUserService.queryAllSysUser(querySysUserParam);
        log.info("结束分页查询全部用户:{}", pageInfo);
        return ResultUtil.fillSuccesData(pageInfo);
       /* } catch (BusinessException e) {
            log.info("业务异常test:{}", e.getMessage());
            e.printStackTrace();
            return ResultUtil.fillErrorMsg(e.getErrorCode(), e.getMessage());
        } catch (Exception e) {
            log.info("系统异常test:{}", e.getMessage());
            e.printStackTrace();
            return ResultUtil.error();
        }*/
    }
    @RequestMapping(value = "/permission/apiLogin", method = RequestMethod.POST)
    public Object apiLogin(@Valid @RequestBody SysUserLoginParam sysUserLoginParam)throws Exception {
        log.info("开始用户API接口登录:{}", sysUserLoginParam);
        String jwtToken = sysUserService.apiLogin(sysUserLoginParam);
        log.info("结束用户API接口登录:{}", jwtToken);
        return ResultUtil.fillSuccesData(jwtToken);

    }


    @RequestMapping(value = "/permission/proxyCheck", method = RequestMethod.POST)
    public Object proxyCheck(HttpServletRequest request,HttpServletResponse response)throws Exception {
        log.info("开始代理检查第3方认证请求");
        ServletRequest servletRequest=(ServletRequest)request;
        ServletResponse servletResponse=(ServletResponse) response;
        JwtAuthenticationToken token = JwtUtil.createToken(servletRequest, servletResponse);
        try {
            if (null != SecurityUtils.getSubject()
                    && SecurityUtils.getSubject().isAuthenticated()) {
                return ResultUtil.succes();
            }
            Subject subject = SecurityUtils.getSubject();
            subject.login(token);//认证
        } catch (AuthenticationException e) {//认证失败，发送401状态并附带异常信息
            log.error(e.getMessage(),e);
            ResultUtil.fillErrorMsg(String.valueOf(HttpServletResponse.SC_UNAUTHORIZED),e.getMessage());
        }
        log.info("结束代理检查第3方认证请求");
        return ResultUtil.succes();//认证成功，过滤器链继续
    }


}
