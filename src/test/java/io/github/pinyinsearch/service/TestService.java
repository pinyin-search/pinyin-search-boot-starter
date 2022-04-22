package io.github.pinyinsearch.service;

import io.github.pinyinsearch.annotation.PinYinSearchAddUpdate;
import io.github.pinyinsearch.annotation.PinYinSearchDelete;
import io.github.pinyinsearch.entity.TestEntity;
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

    @PinYinSearchAddUpdate
    public void testAddUpdate(TestEntity para) {
    }

    @PinYinSearchDelete(value = TestEntity.class)
    public void testDelete(String guid) {
    }

    @PinYinSearchAddUpdate
    public void testMethod(Map<String, String> para) {
    }

    @PinYinSearchAddUpdate
    public void testString(String para) {
    }

}

