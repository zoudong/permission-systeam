package com.zoudong.permission.controller.base;

import com.github.pagehelper.PageInfo;
import com.zoudong.permission.exception.BusinessException;
import com.zoudong.permission.result.ResultUtil;
import com.zoudong.permission.service.api.TestApi;
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
public class Test {
    @Autowired
    private TestApi testApi;

    @RequestMapping(value = "/permission/test", method = RequestMethod.POST)
    public Object test(@Valid @RequestBody com.zoudong.permission.model.Test test)throws Exception {
       /* try {*/
            log.info("开始test:{}", test);
            PageInfo<com.zoudong.permission.model.Test> pageInfo = testApi.test();
            log.info("结束test:{}", pageInfo);
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
