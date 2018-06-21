package com.zoudong.permission.config.shiro;

import com.zoudong.permission.advice.ExceptionHandlerAdvice;
import com.zoudong.permission.config.shiro.filter.AccessTokenFilter;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.session.mgt.DefaultSessionManager;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.crazycake.shiro.RedisCacheManager;
import org.crazycake.shiro.RedisManager;
import org.crazycake.shiro.RedisSessionDAO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.apache.shiro.mgt.SecurityManager;

import javax.servlet.Filter;
import java.util.LinkedHashMap;
import java.util.Map;
@Slf4j
@Configuration
public class ShiroConfig {
    @Value("${spring.redis.host}")
    private String host;
    @Value("${spring.redis.port}")
    private int port;
    @Value("${spring.redis.timeout}")
    private int timeout;
    @Value("${spring.redis.password}")
    private String password;
    @Bean  
    public ShiroFilterFactoryBean shirFilter(SecurityManager securityManager) {
        log.info("开始进入shiro Filter");
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();  
        shiroFilterFactoryBean.setSecurityManager(securityManager);


        // 自定义过滤器
        Map<String, Filter> filterMap = shiroFilterFactoryBean.getFilters();
        filterMap.put("accessTokenFilter", new AccessTokenFilter());
        shiroFilterFactoryBean.setFilters(filterMap);


        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<String, String>();
        filterChainDefinitionMap.put("/permission/querySysUserByPage", "accessTokenFilter");
        //accessTokenFilter代替默认的authc不然后DisabledSessionException
        filterChainDefinitionMap.put("/permission/logout", "logout");
        filterChainDefinitionMap.put("/permission/apiLogin", "anon");
        filterChainDefinitionMap.put("/static/**", "anon");
        filterChainDefinitionMap.put("/**", "accessTokenFilter");

        shiroFilterFactoryBean.setLoginUrl("/unAuth");

        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);  
        return shiroFilterFactoryBean;  
    }  
  
    /**  
     * 凭证匹配器  
     * （由于我们的密码校验交给Shiro的SimpleAuthenticationInfo进行处理了  
     * ）  
     *  
     * @return  
     */  
    @Bean
    public HashedCredentialsMatcher hashedCredentialsMatcher() {  
        HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();
        hashedCredentialsMatcher.setHashAlgorithmName("md5");//散列算法:这里使用MD5算法;  
        hashedCredentialsMatcher.setHashIterations(2);//散列的次数，比如散列两次，相当于 md5(md5(""));  
        return hashedCredentialsMatcher;  
    }  
  
    @Bean  
    public MyShiroRealm myShiroRealm() {  
        MyShiroRealm myShiroRealm = new MyShiroRealm();
        //暂时不加密
        /*myShiroRealm.setCredentialsMatcher(hashedCredentialsMatcher());  */
        return myShiroRealm;  
    }
    /**
     * cacheManager 缓存 redis实现
     * <p>
     * 使用的是shiro-redis开源插件
     *
     * @return
     */
    @Bean
    public RedisCacheManager redisCacheManager() {
        RedisCacheManager redisCacheManager = new RedisCacheManager();
        redisCacheManager.setRedisManager(redisManager());
        return redisCacheManager;
    }

    /**
     * RedisSessionDAO shiro sessionDao层的实现 通过redis
     * <p>
     * 使用的是shiro-redis开源插件
     */
    @Bean
    public RedisSessionDAO redisSessionDAO() {
        RedisSessionDAO redisSessionDAO = new RedisSessionDAO();
        redisSessionDAO.setRedisManager(redisManager());
        return redisSessionDAO;
    }
    //自定义sessionManager
    @Bean
    public SessionManager sessionManager() {
        MySessionManager mySessionManager = new MySessionManager();
        //shiro无状态设置
        mySessionManager.setSessionValidationSchedulerEnabled(false);
        mySessionManager.setSessionDAO(redisSessionDAO());
        return mySessionManager;
    }

    @Bean  
    public SecurityManager securityManager() {  
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(myShiroRealm());  
        // 自定义session管理 使用redis  
        securityManager.setSessionManager(sessionManager());  
        // 自定义缓存实现 使用redis  
        securityManager.setCacheManager(redisCacheManager());//这儿注意不要被重名

        securityManager.setSubjectFactory(new TokenSubjectFactory());
        //应对 无状态currentUser.getSession()==null;
        ((DefaultSessionStorageEvaluator)((DefaultSubjectDAO)securityManager.getSubjectDAO()).getSessionStorageEvaluator()).setSessionStorageEnabled(false);

        return securityManager;
    }

  
    /** 
     * 配置shiro redisManager 
     * <p> 
     * 使用的是shiro-redis开源插件 
     * 
     * @return 
     */  
    public RedisManager redisManager() {  
        RedisManager redisManager = new RedisManager();
        redisManager.setHost(host);  
        redisManager.setPort(port);  
        redisManager.setTimeout(timeout);
        //redisManager.setPassword(password);
        return redisManager;  
    }  
  

  
    /** 
     * 开启shiro aop注解支持. 
     * 使用代理方式;所以需要开启代码支持; 
     * 
     * @param securityManager 
     * @return 
     */  
    @Bean  
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();  
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);  
        return authorizationAttributeSourceAdvisor;  
    }  
  
    /** 
     * 注册全局异常处理 
     * @return 
     */  
    @Bean(name = "exceptionHandler")  
    public ExceptionHandlerAdvice handlerExceptionResolver() {
        return new ExceptionHandlerAdvice();
    }  
}  