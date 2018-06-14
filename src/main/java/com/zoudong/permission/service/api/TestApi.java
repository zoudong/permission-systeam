package com.zoudong.permission.service.api;

import com.github.pagehelper.PageInfo;
import com.zoudong.permission.model.Test;

/**
 * @author zd
 * @description class
 * @date 2018/6/4 17:47
 */
public interface TestApi {
     PageInfo<Test> test()throws Exception;
}
