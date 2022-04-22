package io.github.pinyinsearch.utils;

import io.github.pinyinsearch.annotation.PinYinSearchEntity;
import io.github.pinyinsearch.annotation.PinYinSearchField;
import io.github.pinyinsearch.annotation.PinYinSearchId;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * 拼音搜索 工具类
 *
 * @author jeessy
 * @since 2022-04-22
 */
@Slf4j
public class PinYinSearchUtils {

    /**
     * 获取IndexNamePrefix
     *
     * @param clazz clazz
     * @return "IndexNamePrefix"/null(未找到)
     */
    public static String getIndexNamePrefix(Class<?> clazz) {
        PinYinSearchEntity entityAnnotation = clazz.getAnnotation(PinYinSearchEntity.class);
        if (entityAnnotation != null) {
            String indexNamePrefix = entityAnnotation.indexNamePrefix();
            // 默认为参数的 class name
            if ("".equals(indexNamePrefix)) {
                indexNamePrefix = clazz.getSimpleName();
            }
            return indexNamePrefix;
        }
        return null;
    }



    /**
     * 获取 {@link PinYinSearchId}
     *
     * @param fields fields
     * @return {@link PinYinSearchId} Field
     */
    public static Field getFieldId(Field[] fields) {
        for (Field field : fields) {
            if (field.getAnnotation(PinYinSearchId.class) != null) {
                return field;
            }
        }
        return null;
    }

    /**
     * 获取 {@link PinYinSearchField}
     *
     * @param fields fields
     * @return Map key: indexNameSuffix value: 当前Field
     */
    public static Map<String, Field> getFields(Field[] fields) {
        Map<String, Field> fieldsMap = new HashMap<>();
        for (Field field : fields) {
            PinYinSearchField fieldAnnotation = field.getAnnotation(PinYinSearchField.class);
            if (fieldAnnotation != null) {
                String indexNameSuffix = fieldAnnotation.indexNameSuffix();
                if ("".equals(indexNameSuffix)) {
                    // 默认当前字段名
                    indexNameSuffix = field.getName();
                }
                fieldsMap.put(indexNameSuffix, field);
            }
        }
        return fieldsMap;
    }
}
