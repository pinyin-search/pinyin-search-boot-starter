package io.github.pinyinsearch.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 拼音suggest返回数据 实体
 *
 * @author jeessy
 * @since 2022-04-20
 */
@Data
public class PinYinSuggestRespData implements Serializable {
    /**
     * index id
     */
    private String id;

    /**
     * key
     */
    private String key;

    /**
     * value
     */
    private String value;

}
