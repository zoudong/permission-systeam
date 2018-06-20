package com.zoudong.permission.param.user.login;

import com.zoudong.permission.param.PageParam;
import lombok.Data;

import java.util.Date;

@Data
public class SysUserLoginParam {
    /**
     * 账号
     */
    private String account;

    /**
     * 密码
     */
    private String password;

}