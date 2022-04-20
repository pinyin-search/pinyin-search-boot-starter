package com.github.pinyinsearch.entity;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 拼音suggestion 实体
 *
 * @author jeessy
 * @since 2022-04-20
 */
@Data
@Builder
public class PinYinSugResp implements Serializable {
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
    private List<PinYinSugRespData> data;

}
