package io.github.pinyinsearch.entity;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 拼音suggest 实体
 *
 * @author jeessy
 * @since 2022-04-20
 */
@Data
@Builder
public class PinYinSuggestResp implements Serializable {
    /**
     * 是否成功
     */
    private boolean success;

    /**
     * msg
     */
    private String msg;

    /**
     * 数据
     */
    private List<PinYinSuggestRespData> data;

}
