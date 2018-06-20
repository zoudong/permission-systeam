package com.zoudong.permission.controller;

import com.github.pagehelper.PageInfo;
import com.zoudong.permission.exception.BusinessException;
import com.zoudong.permission.model.SysUser;
import com.zoudong.permission.param.user.query.QuerySysUserParam;
import com.zoudong.permission.result.ResultUtil;
import com.zoudong.permission.service.api.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

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

    @RequestMapping(value = "/permission/querySysUserByPage", method = RequestMethod.POST)
    public Object test(@Valid @RequestBody QuerySysUserParam querySysUserParam)throws Exception {
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
}