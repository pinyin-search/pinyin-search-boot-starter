package com.github.pinyinsearch.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 拼音suggestion 实体
 *
 * @author jeessy
 * @since 2022-04-20
 */
@Data
public class PinYinSugRespData implements Serializable {
    /**
     * index id
     */
    private long id;

    /**
     * key
     */
    private String key;

    /**
     * value
     */
    private String value;

}
