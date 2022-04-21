package com.github.pinyinsearch.annotation;

import java.lang.annotation.*;

/**
 * 拼音搜索 Id字段 注解
 *
 * @author jeessy
 * @since 2022-04-21
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PinYinSearchId {

}
