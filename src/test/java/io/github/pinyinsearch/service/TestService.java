package io.github.pinyinsearch.service;

import io.github.pinyinsearch.annotation.PinYinSearchUpdate;
import io.github.pinyinsearch.annotation.PinYinSearchDelete;
import io.github.pinyinsearch.entity.TestEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 实际业务的service/controller
 *
 * @author jeessy
 * @since 2022-04-20
 */
@Component
public class TestService {

    @PinYinSearchUpdate
    public void testUpdate(TestEntity para) {
    }

    @PinYinSearchUpdate
    public void testUpdateBatch(List<TestEntity> paras) {
    }

    @PinYinSearchDelete(value = TestEntity.class)
    public void testDelete(String guid) {
    }

    @PinYinSearchUpdate
    public void testMethod(Map<String, String> para) {
    }

    @PinYinSearchUpdate
    public void testString(String para) {
    }

}

