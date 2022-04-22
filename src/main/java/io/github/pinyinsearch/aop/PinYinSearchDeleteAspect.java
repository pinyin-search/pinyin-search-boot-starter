package io.github.pinyinsearch.aop;

import io.github.pinyinsearch.annotation.PinYinSearchDelete;
import io.github.pinyinsearch.config.PinYinSearchService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

/**
 * 删除拼音搜索索引 aspect
 *
 * @author jeessy
 * @since 2022-04-22
 */
@Aspect
@Slf4j
public class PinYinSearchDeleteAspect {

    private final PinYinSearchService pinYinSearchService;

    /**
     * constructor
     * @param pinYinSearchService pinYinSearchService
     */
    public PinYinSearchDeleteAspect(PinYinSearchService pinYinSearchService) {
        this.pinYinSearchService = pinYinSearchService;
    }

    /**
     * PointCut 拦截 {@link PinYinSearchDelete}
     */
    @Pointcut(value = "@annotation(io.github.pinyinsearch.annotation.PinYinSearchDelete)")
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
        PinYinSearchDelete annotation = method.getAnnotation(PinYinSearchDelete.class);
        pinYinSearchService.deleteIndex(annotation.value(), joinPoint.getArgs());
    }

}
