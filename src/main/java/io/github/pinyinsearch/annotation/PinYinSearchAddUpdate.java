package io.github.pinyinsearch.annotation;

import java.lang.annotation.*;

/**
 * PinYinSearch 添加或更新 注解
 * 添加到需要拦截的方法中
 *
 * @author jeessy
 * @since 2022-04-19
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PinYinSearchAddUpdate {

}
