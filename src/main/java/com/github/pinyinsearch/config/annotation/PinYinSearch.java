package com.github.pinyinsearch.config.annotation;

import java.lang.annotation.*;

/**
 * <p>
 * 拼音搜索 实体注解 <br/>
 * 提取的字段名称: 方法中的每一个参数都将会迭代 <br/>
 * 只支持实体对象或Map
 * </p>
 *
 * @author jeessy
 * @since 2022-04-19
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PinYinSearch {

    /**
     * <p>
     *     index name 前缀 <br/>
     *     实际的index name为 indexName + "_" + 每一个fieldName
     * </p>
     */
    String indexName();

    /**
     * 提取的字段名称
     * 支持多个
     *
     */
    String[] extractFieldName();

    /**
     * 是否为删除索引
     */
    boolean deleteIndex() default false;

}
