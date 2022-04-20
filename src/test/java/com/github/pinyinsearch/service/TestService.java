package com.github.pinyinsearch.service;

import com.github.pinyinsearch.config.annotation.PinYinSearch;
import com.github.pinyinsearch.entity.TestEntity;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 实际业务的service/controller
 *
 * @author jeessy
 * @since 2022-04-20
 */
@Component
public class TestService {

    @PinYinSearch(indexName = "method", extractFieldName = "name")
    public void testMethod(TestEntity para) {
    }

    @PinYinSearch(indexName = "method", extractFieldName = "name")
    public void testMethod(Map<String, String> para) {
    }

    @PinYinSearch(indexName = "method", extractFieldName = "name")
    public void testString(String para) {
    }

}

