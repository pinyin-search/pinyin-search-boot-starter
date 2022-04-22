package io.github.pinyinsearch.entity;

import io.github.pinyinsearch.annotation.PinYinSearchEntity;
import io.github.pinyinsearch.annotation.PinYinSearchField;
import io.github.pinyinsearch.annotation.PinYinSearchId;

import java.io.Serializable;

/**
 * MyEntity 实际业务的实体类
 *
 * @author jeessy
 * @since 2022-04-20
 */
@PinYinSearchEntity
public class TestEntity implements Serializable {
    @PinYinSearchId
    private String guid;
    @PinYinSearchField
    private String name;
    private Integer age;

    public TestEntity(String guid, String name, Integer age) {
        this.guid = guid;
        this.name = name;
        this.age = age;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}
