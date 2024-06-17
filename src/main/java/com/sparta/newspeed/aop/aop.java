package com.sparta.newspeed.aop;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Aspect
@Component
public class aop {

    @Pointcut("execution(* com.sparta.newspeed.controller..*.*(..) )")
    private void forAllController() {}

    @Before("forAllController()")
    public void doLogging(JoinPoint joinpoint) throws Throwable{
        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        log.info(request.getMethod()+" : "+request.getRequestURI()); // API 요청을 할때마다 해당 메서드와
    }
}
