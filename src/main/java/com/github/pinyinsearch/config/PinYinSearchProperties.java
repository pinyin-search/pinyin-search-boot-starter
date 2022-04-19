package com.github.pinyinsearch.config;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;

/**
 * 搜索服务 配置对象
 *
 * @author jeessy
 * @since 2022-04-19
 */
@Data
@ConfigurationProperties(prefix = "pinyin.search")
public class PinYinSearchProperties implements Serializable {
    /**
     * 是否启动
     */
    private boolean enabled = true;

    /**
     * endpoint
     */
    private String endpoint = "http://127.0.0.1:7701";

    /**
     * authorization (token)
     */
    private String authorization;

    /**
     * tenant can be set to project name
     */
    private String tenant = "";

}
