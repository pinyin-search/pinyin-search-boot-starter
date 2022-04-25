package io.github.pinyinsearch.entity;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * 请求更新拼音搜索的 实体
 *
 * @author jeessy
 * @since 2022-04-25
 */
@Data
@Builder
public class PinYinRequest implements Serializable {

    /**
     * 租户
     */
    private String tenant;

    /**
     * 索引名
     */
    private String indexName;

    /**
     * 数据ID
     */
    private String dataId;

    /**
     * 数据
     */
    private String data;

}
