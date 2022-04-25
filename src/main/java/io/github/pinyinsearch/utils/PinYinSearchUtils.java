package io.github.pinyinsearch.utils;

import io.github.pinyinsearch.annotation.PinYinSearchEntity;
import io.github.pinyinsearch.annotation.PinYinSearchField;
import io.github.pinyinsearch.annotation.PinYinSearchId;
import io.github.pinyinsearch.entity.PinYinRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
     * 获取反射结果
     *
     * @param arg arg
     * @param clazz clazz
     * @param tenant tenant
     * @return {@link List<PinYinRequest>}
     */
    public static List<PinYinRequest> getReflectResults(Object arg, Class<?> clazz, String tenant) {
        List<PinYinRequest> pinYinRequests = new ArrayList<>();

        String indexNamePrefix = PinYinSearchUtils.getIndexNamePrefix(clazz);
        if (null != indexNamePrefix) {
            Field[] fields = clazz.getDeclaredFields();
            Field fieldId = PinYinSearchUtils.getFieldId(fields);

            // field
            if (fieldId == null) {
                return pinYinRequests;
            }

            fieldId.setAccessible(true);
            Object dataId = ReflectionUtils.getField(fieldId, arg);

            if (dataId instanceof String) {
                Map<String, Field> fieldsMap = PinYinSearchUtils.getFields(fields);
                for (Map.Entry<String, Field> entry : fieldsMap.entrySet()) {
                    entry.getValue().setAccessible(true);
                    Object value = ReflectionUtils.getField(entry.getValue(), arg);
                    if (value instanceof String) {
                        pinYinRequests.add(
                                PinYinRequest.builder()
                                        .tenant(tenant)
                                        .indexName(indexNamePrefix + "_" + entry.getKey())
                                        .dataId((String)dataId)
                                        .data((String)value)
                                        .build()
                        );
                    } else {
                        log.warn("{}#{} 获取的值类型不是字符串", arg.getClass().getSimpleName(), entry.getValue().getName());
                    }
                }
            } else {
                log.warn("{}#{} 字段中不能获取字符串", arg.getClass().getSimpleName(), fieldId.getName());
            }
        }

        return pinYinRequests;
    }

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
