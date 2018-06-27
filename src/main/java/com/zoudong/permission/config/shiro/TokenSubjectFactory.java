package com.zoudong.permission.config.shiro;

import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.SubjectContext;
import org.apache.shiro.web.mgt.DefaultWebSubjectFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;


public class TokenSubjectFactory extends DefaultWebSubjectFactory {
    public Subject createSubject(SubjectContext context) {
        AuthenticationToken token = context.getAuthenticationToken();
        if((token instanceof JwtAuthenticationToken)){
            // 当token为HmacToken时， 不创建 session
            context.setSessionCreationEnabled(false);
        }
        return super.createSubject(context);
    }
}
