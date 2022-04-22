package io.github.pinyinsearch.aop;

import io.github.pinyinsearch.annotation.PinYinSearchAddUpdate;
import io.github.pinyinsearch.config.PinYinSearchService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

/**
 * 添加或更新拼音搜索索引 aspect
 *
 * @author jeessy
 * @since 2022-04-19
 */
@Aspect
@Slf4j
public class PinYinSearchAddUpdateAspect {

    private final PinYinSearchService pinYinSearchService;

    /**
     * constructor
     * @param pinYinSearchService pinYinSearchService
     */
    public PinYinSearchAddUpdateAspect(PinYinSearchService pinYinSearchService) {
        this.pinYinSearchService = pinYinSearchService;
    }

    /**
     * PointCut 拦截 {@link PinYinSearchAddUpdate}
     */
    @Pointcut(value = "@annotation(io.github.pinyinsearch.annotation.PinYinSearchAddUpdate)")
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
        // 添加或更新索引
        if (!pinYinSearchService.addUpdateIndex(joinPoint.getArgs())) {
            log.warn("{}#{} 的所有参数中没有找到注解 @PinYinSearchEntity @PinYinSearchId @PinYinSearchField", method.getDeclaringClass().getSimpleName(), method.getName());
        }
    }

}
