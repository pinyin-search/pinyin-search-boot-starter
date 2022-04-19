package com.github.pinyinsearch;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public class TestAnnotation {

    @Resource
    TestService testService;

    @Test
    public void testMethodMap() {
        testService.testMethod(new MyEntity("今天天气真好啊", 20));
    }

    @Test
    public void testMethod() {
        testService.testMethod("今天天气真好啊");
    }


    @Test
    public void testParameter() {
        testService.testParameter("今天天气真好啊");
    }

}
