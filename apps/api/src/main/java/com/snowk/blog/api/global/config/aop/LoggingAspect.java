package com.snowk.blog.api.global.config.aop;

import java.time.LocalDateTime;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Around("execution(* com.snowk.blog.api..application.service..*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        LocalDateTime startTime = LocalDateTime.now();
        long startMillis = System.currentTimeMillis();

        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();

        log.debug("[{}.{}] event={}, startTime={}", className, methodName, "START", startTime);

        try {
            return joinPoint.proceed();
        } finally {
            long executionTime = System.currentTimeMillis() - startMillis;
            LocalDateTime endTime = LocalDateTime.now();
            log.debug("[{}.{}] event={}, endTime={}, durationMs={}",
                className, methodName, "END", endTime, executionTime);
        }
    }
}
