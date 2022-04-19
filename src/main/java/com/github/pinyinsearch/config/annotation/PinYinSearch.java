package com.github.pinyinsearch.config.annotation;

import java.lang.annotation.*;

/**
 * <p>
 * 拼音搜索 实体注解
 * 放入方法中
 * </p>
 *
 * @author jeessy
 * @since 2022-04-19
 */
@Target({ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PinYinSearch {

    /**
     * index name 前缀
     */
    String indexNamePrefix();

    /**
     * 实体名
     */
    String entityFieldName() default "";

    /**
     * 是否为删除索引
     */
    boolean deleteIndex() default false;

}
