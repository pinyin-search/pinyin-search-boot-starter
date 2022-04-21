package com.github.pinyinsearch.annotation;

import java.lang.annotation.*;

/**
 * 拼音搜索 字段注解
 *
 * @author jeessy
 * @since 2022-04-21
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PinYinSearchField {

    /**
     * <p>
     *     index name 后缀 <br/>
     *     留空默认为字段名称 <br/>
     *     实际的index name为 indexNamePrefix + "_" + indexNameSuffix
     * </p>
     *
     * @return ""
     */
    String indexNameSuffix() default "";

}
