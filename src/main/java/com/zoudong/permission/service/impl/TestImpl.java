package com.zoudong.permission.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zoudong.permission.exception.BusinessException;
import com.zoudong.permission.mapper.TestMapper;
import com.zoudong.permission.service.api.TestApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zd
 * @description class
 * @date 2018/6/4 17:47
 */
@Slf4j
@Service
public class TestImpl implements TestApi {
    @Autowired
    private TestMapper testMapper;

    public PageInfo<com.zoudong.permission.model.Test> test() throws Exception {
        throw new BusinessException("business_error","业务异常主动测试");
        //throw new Exception("业务异常主动测试");
        /*PageHelper.startPage(1,2,true,true,false);
        List<com.zoudong.permission.model.Test> testList = testMapper.selectAll();
        PageInfo<com.zoudong.permission.model.Test> pageInfo = new PageInfo<com.zoudong.permission.model.Test>(testList);
        return pageInfo;*/
    }
}
