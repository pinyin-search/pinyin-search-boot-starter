package com.github.pinyinsearch.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

/**
 * aspect
 *
 * @author jeessy
 * @since 2022-04-19
 */
@Aspect
@Slf4j
public class PinYinSearchAspect {

    private final PinYinSearchService pinYinSearchService;

    /**
     * constructor
     * @param pinYinSearchService pinYinSearchService
     */
    public PinYinSearchAspect(PinYinSearchService pinYinSearchService) {
        this.pinYinSearchService = pinYinSearchService;
    }

    /**
     * PointCut 拦截 {@link com.github.pinyinsearch.annotation.PinYinSearch}
     */
    @Pointcut(value = "@annotation(com.github.pinyinsearch.annotation.PinYinSearch)")
    public void pointCut(){
    }

    /**
     * after
     * @param joinPoint joinPoint
     */
    @After("pointCut()")
    public void after(JoinPoint joinPoint){
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        if (!pinYinSearchService.requestByArgs(joinPoint.getArgs())) {
            log.warn("{}#{} 的所有参数中没有找到注解 @PinYinSearchEntity @PinYinSearchId @PinYinSearchField", method.getDeclaringClass().getSimpleName(), method.getName());
        }
    }

}
