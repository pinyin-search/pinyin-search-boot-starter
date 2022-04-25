package io.github.pinyinsearch;

import io.github.pinyinsearch.entity.TestEntity;
import io.github.pinyinsearch.service.TestService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public void testUpdate() {
        testService.testUpdate(new TestEntity("123456789", "今天天气真好啊", 20));
    }

    @Test
    public void testUpdateBatch() throws InterruptedException {
        List<TestEntity> list = new ArrayList<>();
        list.add(new TestEntity("1", "我是帅哥", 20));
        list.add(new TestEntity("2", "我是美女", 20));
        testService.testUpdateBatch(list);
    }

    @Test
    public void testDelete() {
        testService.testDelete("123456789");
    }

    @Test
    public void testMethodMap() {
        Map<String, String> map = new HashMap<>();
        map.put("guid", "123456789");
        map.put("name", "今天天气真好啊");
        testService.testMethod(map);
    }

    @Test
    public void testString() {
        testService.testString("今天天气真好啊");
    }

}
