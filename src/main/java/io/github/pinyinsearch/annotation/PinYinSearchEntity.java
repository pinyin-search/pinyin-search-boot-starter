package io.github.pinyinsearch.annotation;

import java.lang.annotation.*;

/**
 * 拼音搜索 实体注解
 *
 * @author jeessy
 * @since 2022-04-21
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PinYinSearchEntity {

    /**
     * <p>
     *     index name 前缀 <br/>
     *     留空默认为类名称 <br/>
     *     实际的index name为 indexNamePrefix + "_" + indexNameSuffix
     * </p>
     *
     * @return ""
     */
    String indexNamePrefix() default "";

    /**
     * 是否索引当前类的所有字段
     *
     * @return false
     */
    boolean useAllField() default false;

}
