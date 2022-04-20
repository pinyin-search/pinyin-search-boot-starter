package com.github.pinyinsearch.entity;

import java.io.Serializable;

/**
 * MyEntity 实际业务的实体类
 *
 * @author jeessy
 * @since 2022-04-20
 */
public class TestEntity implements Serializable {
    private String name;
    private Integer age;

    public TestEntity(String name, Integer age) {
        this.name = name;
        this.age = age;
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
