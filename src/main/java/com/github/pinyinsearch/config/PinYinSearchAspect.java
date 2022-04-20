package com.github.pinyinsearch.config;

import com.github.pinyinsearch.config.annotation.PinYinSearch;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

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

    public PinYinSearchAspect(PinYinSearchService pinYinSearchService) {
        this.pinYinSearchService = pinYinSearchService;
    }

    @Pointcut(value = "@annotation(com.github.pinyinsearch.config.annotation.PinYinSearch)")
    public void pointCut(){
    }

    @After("pointCut()")
    public void after(JoinPoint joinPoint){
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Object[] args = joinPoint.getArgs();

        PinYinSearch pinYinSearch = method.getAnnotation(PinYinSearch.class);
        for (String fieldName : pinYinSearch.extractFieldName()) {
            boolean findFieldName = false;
            for (Object arg : args) {
                if (arg instanceof Map) {
                    Object value = ((Map<?,?>)arg).get(fieldName);
                    if (value instanceof String) {
                        pinYinSearchService.addIndex(pinYinSearch.indexName() + "_" + fieldName, (String)value);
                        findFieldName = true;
                        break;  // next field
                    }
                    continue;
                }

                Field field = ReflectionUtils.findField(arg.getClass(), fieldName);
                if (null != field) {
                    field.setAccessible(true);
                    Object value = ReflectionUtils.getField(field, arg);
                    if (value instanceof String) {
                        pinYinSearchService.addIndex(pinYinSearch.indexName() + "_" + fieldName, (String)value);
                        findFieldName = true;
                        break;  // next field
                    }
                }
            }

            if (!findFieldName) {
                log.warn("{}#{} 的所有参数没找到属性 {}, 只支持Map/实体对象, 将不会添加到拼音搜索", method.getDeclaringClass().getName(), method.getName(), fieldName);
            }

        }
    }

}
