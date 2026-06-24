package com.ecommerce.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Aspect
@Component
@Order(1)
public class LoggingAspect {

    @Pointcut("within(com.ecommerce.service..*)")
    public void serviceLayer() {
    }

    @Pointcut("within(com.ecommerce.controller..*)")
    public void controllerLayer() {
    }

    @Before("serviceLayer() || controllerLayer()")
    public void logBefore(JoinPoint joinPoint) {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        log.info("==> Entering {}.{}() with arguments={}", className, methodName, Arrays.toString(args));
    }

    @AfterReturning(pointcut = "serviceLayer() || controllerLayer()", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        log.info("<== Exiting {}.{}() with result={}", className, methodName, result);
    }

    @AfterThrowing(pointcut = "serviceLayer() || controllerLayer()", throwing = "ex")
    public void logAfterThrowing(JoinPoint joinPoint, Exception ex) {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        log.error("<== Exception in {}.{}(): {}", className, methodName, ex.getMessage());
    }
}