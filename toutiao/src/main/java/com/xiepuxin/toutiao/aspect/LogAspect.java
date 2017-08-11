package com.xiepuxin.toutiao.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Aspect
@Component
public class LogAspect {
    private static final Logger logger = LoggerFactory.getLogger(LogAspect.class);

    @Before("execution(* com.xiepuxin.toutiao.controller.*Controller.*(..))")
    public void beforeMethod(JoinPoint joinPoint){
        StringBuilder sb = new StringBuilder();
        for(Object arg : joinPoint.getArgs()){
            sb.append("ars:" + arg.toString()+"\n");
        }
        logger.info("before method\n" + sb.toString());
    }

    @After("execution(* com.xiepuxin.toutiao.controller.*Controller.*(..))")
    public void afterMethod(){
        logger.info("after method");
    }
}
