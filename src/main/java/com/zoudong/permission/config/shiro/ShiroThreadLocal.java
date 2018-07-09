package com.zoudong.permission.config.shiro;

import lombok.Data;

/**
 * @author zd
 * @description class
 * @date 2018/7/5 17:19
 */
@Data
public class ShiroThreadLocal {
    public final static ThreadLocal<String> AUTHORIZATIONKEY = new ThreadLocal<String>();
}
