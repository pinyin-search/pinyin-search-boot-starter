package com.github.pinyinsearch.config;


import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;

import java.net.URI;
import java.util.concurrent.TimeUnit;

/**
 * 搜索 服务类
 *
 * @author jeessy
 * @since 2022-04-19
 */
@Slf4j
public class PinYinSearchService {

    @Getter
    private final PinYinSearchProperties props;

    private static OkHttpClient okHttpClient;

    @Getter
    private final URI addUrl;
    @Getter
    private final URI suggestionUrl;

    public PinYinSearchService(PinYinSearchProperties props) {
        this.props = props;
        this.addUrl = URI.create(props.getEndpoint() + (props.getEndpoint().endsWith("/")?"":"/") + "add");
        this.suggestionUrl = URI.create(props.getEndpoint() + (props.getEndpoint().endsWith("/")?"":"/") + "suggestion");
    }

    public synchronized OkHttpClient getHttpClient() {
        if (null == okHttpClient) {
            okHttpClient = new OkHttpClient.Builder()
                    .retryOnConnectionFailure(true)
                    .connectionPool(new ConnectionPool(100, 30, TimeUnit.SECONDS))
                    .build();
        }
        return okHttpClient;
    }

}
