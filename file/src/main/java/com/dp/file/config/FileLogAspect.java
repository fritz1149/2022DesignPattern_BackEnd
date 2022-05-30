package com.dp.file.config;


import com.google.gson.Gson;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
public class FileLogAspect {
    private final static Logger logger = LoggerFactory.getLogger(FileLogAspect.class);

    @Pointcut("execution(public * com.dp.file.controller..*.*(..))")
    public void fileLog() {
    }

    @Before("fileLog()")
    public void doBefore(JoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        logger.info("{}  {}  {}  {}", request.getMethod(), request.getRequestURL().toString(), new Gson().toJson(joinPoint.getArgs()), request.getRemoteAddr());
    }


    @Around("fileLog()")
    public Object doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Object result = proceedingJoinPoint.proceed();
        logger.info("Response Args  : {}", new Gson().toJson(result));
        return result;
    }
}