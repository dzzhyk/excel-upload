package com.yankaizhang.excel.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Slf4j
public class LogAspect {

    @Pointcut("execution(* com.yankaizhang.excel.service.*.*(..))")
    public void pt(){}

    @Around("pt()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable{

        long start = System.currentTimeMillis();
        Object result = "";
        try{
            result = joinPoint.proceed();

        } catch(Exception e) {

            log.error("执行错误, errorMessage: {}", e.getMessage());

        } finally {
            long cost = System.currentTimeMillis() - start;

            String methodName = joinPoint.getSignature().getName();

            log.info(methodName + " 方法执行时间: {} ms", cost);
        }
        return result;
    }

}
