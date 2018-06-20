package com.zoudong.permission.config;

import com.zoudong.permission.config.shiro.filter.AccessTokenFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zd
 * @description class
 * @date 2018/6/20 14:52
 */
@Configuration
public class RegFilterConfig {
/*    @Bean
    public FilterRegistrationBean myFilter() {
        FilterRegistrationBean myFilter = new FilterRegistrationBean();
        myFilter.addUrlPatterns("/*");
        myFilter.setFilter(new AccessTokenFilter());
        return myFilter;
    }*/
}
