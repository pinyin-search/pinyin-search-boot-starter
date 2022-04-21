package com.github.pinyinsearch;

import com.github.pinyinsearch.entity.TestEntity;
import com.github.pinyinsearch.service.TestService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Test
 *
 * @author jeessy
 * @since 2022-04-20
 */
@SpringBootTest
public class TestAnnotation {

    @Resource
    TestService testService;

    @Test
    public void testMethodEntity() {
        testService.testMethod(new TestEntity(UUID.randomUUID().toString(), "今天天气真好啊", 20));
    }

    @Test
    public void testMethodMap() {
        Map<String, String> map = new HashMap<>();
        map.put("guid", UUID.randomUUID().toString());
        map.put("name", "今天天气真好啊");
        testService.testMethod(map);
    }

    @Test
    public void testString() {
        testService.testString("今天天气真好啊");
    }

}
