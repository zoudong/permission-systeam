package com.zoudong.permission.utils.shiro;

import com.zoudong.permission.advice.ExceptionHandlerAdvice;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
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
import java.util.LinkedHashMap;
import java.util.Map;

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
        System.out.println("ShiroConfiguration.shirFilter()");  
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();  
        shiroFilterFactoryBean.setSecurityManager(securityManager);

        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<String, String>();
        filterChainDefinitionMap.put("/logout", "logout");

        filterChainDefinitionMap.put("/static/**", "anon");
        filterChainDefinitionMap.put("/ajaxLogin", "anon");  
        filterChainDefinitionMap.put("/login", "anon");  
        filterChainDefinitionMap.put("/**", "authc");

        shiroFilterFactoryBean.setLoginUrl("/unauth");  

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
        myShiroRealm.setCredentialsMatcher(hashedCredentialsMatcher());  
        return myShiroRealm;  
    }  
  
  
    @Bean  
    public SecurityManager securityManager() {  
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(myShiroRealm());  
        // 自定义session管理 使用redis  
        securityManager.setSessionManager(sessionManager());  
        // 自定义缓存实现 使用redis  
        securityManager.setCacheManager(cacheManager());  
        return securityManager;  
    }  
  
    //自定义sessionManager  
    @Bean  
    public SessionManager sessionManager() {
        MySessionManager mySessionManager = new MySessionManager();  
        mySessionManager.setSessionDAO(redisSessionDAO());  
        return mySessionManager;  
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
        redisManager.setPassword(password);  
        return redisManager;  
    }  
  
    /** 
     * cacheManager 缓存 redis实现 
     * <p> 
     * 使用的是shiro-redis开源插件 
     * 
     * @return 
     */  
    @Bean  
    public RedisCacheManager cacheManager() {
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