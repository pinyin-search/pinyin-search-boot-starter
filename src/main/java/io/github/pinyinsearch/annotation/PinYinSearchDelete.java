package io.github.pinyinsearch.annotation;

import java.lang.annotation.*;

/**
 * 删除拼音搜索 注解
 * 添加到需要拦截的方法中
 *
 * @author jeessy
 * @since 2022-04-22
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PinYinSearchDelete {

    /**
     * 关连的实体类
     *
     * @return Class
     */
    Class<?> value();

}
