package com.github.pinyinsearch.config;


import lombok.extern.slf4j.Slf4j;

/**
 * 搜索 服务类
 *
 * @author jeessy
 * @since 2022-04-19
 */
@Slf4j
public class PinYinSearchService {

    private final PinYinSearchProperties props;

    public PinYinSearchService(PinYinSearchProperties props) {
        this.props = props;
    }

}
