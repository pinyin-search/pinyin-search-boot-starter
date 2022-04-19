package com.github.pinyinsearch;

import com.github.pinyinsearch.config.PinYinSearchService;
import com.github.pinyinsearch.config.annotation.PinYinSearch;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * aspect
 *
 * @author jeessy
 * @since 2022-04-19
 */
@Aspect
@Component
@Slf4j
public class PinYinSearchAspect {

    @Resource
    private PinYinSearchService pinYinSearchService;

    @Pointcut("@annotation(com.github.pinyinsearch.config.annotation.PinYinSearch)")
    public void annotationPointCut(){}

    @After("annotationPointCut()")
    public void after(JoinPoint joinPoint){
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Object[] args = joinPoint.getArgs();

        PinYinSearch pinYinSearch = method.getAnnotation(PinYinSearch.class);

        for (Object arg : args) {
            Field field = ReflectionUtils.findField(arg.getClass(), pinYinSearch.entityFieldName());
            if (null != field) {
                field.setAccessible(true);
                Object value = ReflectionUtils.getField(field, arg);
            } else {
                // todo
            }
        }

        HttpUrl.Builder httpBuilder = Objects.requireNonNull(HttpUrl.get(pinYinSearchService.getAddUrl())).newBuilder();
        httpBuilder.addQueryParameter("tenant", pinYinSearchService.getProps().getTenant());
        httpBuilder.addQueryParameter("data", pinYinSearchService.getProps().getTenant());
        httpBuilder.addQueryParameter("indexName", pinYinSearch.indexNamePrefix());

        Request request = new Request.Builder().url(httpBuilder.build()).get()
                .addHeader("Authorization", pinYinSearchService.getProps().getAuthorization())
                .build();
        Call call = pinYinSearchService.getHttpClient().newCall(request);
        call.enqueue(new Callback() {

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                log.debug("成功 {}", response.code());
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                log.error("Request pinyin-search with error. {}", e.getMessage());
                e.printStackTrace();
            }
        });
    }

}
