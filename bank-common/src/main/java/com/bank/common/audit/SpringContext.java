package com.bank.common.audit;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class SpringContext {
    private static ApplicationContext context;

    public SpringContext(ApplicationContext applicationContext){
        context = applicationContext;
    }

    public static ApplicationEventPublisher getPublisher(){
        return context.getBean(ApplicationEventPublisher.class);
    }

}
