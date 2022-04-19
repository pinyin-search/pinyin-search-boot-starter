package com.github.pinyinsearch.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * 搜索服务 自动配置
 *
 * @author jeessy
 * @since 2022-04-19
 */
@Configuration
@EnableConfigurationProperties(PinYinSearchProperties.class)
@ConditionalOnClass(PinYinSearchService.class)
@ConditionalOnProperty(prefix = "pinyin.search", value = "enabled", matchIfMissing = true)
public class PinYinSearchAutoConfiguration {

    @Resource
    private PinYinSearchProperties properties;

    @Bean
    @ConditionalOnMissingBean(PinYinSearchService.class)
    public PinYinSearchService searchService(){
        return new PinYinSearchService(properties);
    }

}
