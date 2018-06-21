package com.zoudong.permission.config.shiro;

import com.zoudong.permission.config.shiro.filter.AccessTokenFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zd
 * @description class
 * @date 2018/6/20 17:19
 */
@Slf4j
@Configuration
public class FilterManager {
    /**
     * 配置过滤器
     * @return
     */
    /*@Bean
    public FilterRegistrationBean someFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
       registration.setFilter(new AccessTokenFilter());
        registration.addUrlPatterns("/*");
        registration.setName("accessTokenFilter");
        return registration;
    }*/

}
