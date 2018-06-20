package com.zoudong.permission.config.shiro;

import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.SubjectContext;
import org.apache.shiro.web.mgt.DefaultWebSubjectFactory;

public class TokenSubjectFactory extends DefaultWebSubjectFactory {

    public Subject createSubject(SubjectContext context) {
        //不创建 session
        context.setSessionCreationEnabled(false);
        return super.createSubject(context);
    }
}
