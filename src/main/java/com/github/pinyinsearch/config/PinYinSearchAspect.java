package com.github.pinyinsearch.config;

import com.github.pinyinsearch.annotation.PinYinSearchEntity;
import com.github.pinyinsearch.annotation.PinYinSearchField;
import com.github.pinyinsearch.annotation.PinYinSearchId;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
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

    public PinYinSearchAspect(PinYinSearchService pinYinSearchService) {
        this.pinYinSearchService = pinYinSearchService;
    }

    @Pointcut(value = "@annotation(com.github.pinyinsearch.annotation.PinYinSearch)")
    public void pointCut(){
    }

    @After("pointCut()")
    public void after(JoinPoint joinPoint){
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Object[] args = joinPoint.getArgs();

        boolean find = false;

        for (Object arg : args) {
            PinYinSearchEntity entityAnnotation = arg.getClass().getAnnotation(PinYinSearchEntity.class);
            if (entityAnnotation != null) {
                String indexNamePrefix = entityAnnotation.indexNamePrefix();
                // 默认为参数的 class name
                if ("".equals(indexNamePrefix)) {
                    indexNamePrefix = arg.getClass().getSimpleName();
                }
                Field[] fields = arg.getClass().getDeclaredFields();

                // 先查找 PinYinSearchId
                Field fieldId = null;
                for (Field field : fields) {
                    if (field.getAnnotation(PinYinSearchId.class) != null) {
                        fieldId = field;
                        break;
                    }
                }

                // field
                if (fieldId == null) {
                    continue;
                }

                fieldId.setAccessible(true);
                Object dataId = ReflectionUtils.getField(fieldId, arg);
                if (dataId instanceof String) {
                    for (Field field : fields) {
                        PinYinSearchField fieldAnnotation = field.getAnnotation(PinYinSearchField.class);
                        if (fieldAnnotation != null) {
                            String indexNameSuffix = fieldAnnotation.indexNameSuffix();
                            if ("".equals(indexNameSuffix)) {
                                // 默认当前字段名
                                indexNameSuffix = field.getName();
                            }
                            field.setAccessible(true);
                            Object value = ReflectionUtils.getField(field, arg);
                            if (value instanceof String) {
                                pinYinSearchService.addUpdateIndex(indexNamePrefix + "_" + indexNameSuffix, (String) dataId, (String)value);
                                find = true;
                            }
                        }
                    }
                } else {
                    log.warn("{}#{} 字段中不能获取字符串", arg.getClass().getSimpleName(), fieldId.getName());
                }
            }
        }

        if (!find) {
            log.warn("{}#{} 的所有参数中没有找到注解 @PinYinSearchEntity @PinYinSearchId @PinYinSearchField", method.getDeclaringClass().getSimpleName(), method.getName());
        }
    }
}
