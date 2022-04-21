package com.github.pinyinsearch.config;

import com.github.pinyinsearch.entity.PinYinSugResp;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.util.Assert;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Objects;
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

    private final static Gson gson = new GsonBuilder().create();

    private static OkHttpClient okHttpClient;

    private final URI addUpdateUri;
    private final URI deleteUri;
    private final URI suggestionUri;

    public PinYinSearchService(PinYinSearchProperties props) {
        this.props = props;
        this.addUpdateUri = URI.create(props.getEndpoint() + (props.getEndpoint().endsWith("/")?"":"/") + "addUpdate");
        this.deleteUri = URI.create(props.getEndpoint() + (props.getEndpoint().endsWith("/")?"":"/") + "delete");
        this.suggestionUri = URI.create(props.getEndpoint() + (props.getEndpoint().endsWith("/")?"":"/") + "suggestion");
    }

    /**
     * 获取OkHttpClient
     * @return OkHttpClient
     */
    private synchronized OkHttpClient getHttpClient() {
        if (null == okHttpClient) {
            okHttpClient = new OkHttpClient.Builder()
                    .retryOnConnectionFailure(true)
                    .connectionPool(new ConnectionPool(100, 30, TimeUnit.SECONDS))
                    .build();
        }
        return okHttpClient;
    }

    /**
     * 添加索引
     * @param indexName index name
     * @param data 数据
     */
    public void addUpdateIndex(String indexName, String dataId, String data) {
        if (!props.isEnabled()) {
            return;
        }
        Call call = getHttpClient().newCall(getRequest(addUpdateUri, indexName, dataId, data));

        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if (response.code() == 200) {
                    try {
                        assert response.body() != null;
                        log.debug("添加拼音搜索成功! indexName:{}, 返回值: {}", indexName, response.body().string());
                    } catch (Exception e) {
                        log.warn("添加拼音搜索失败! indexName:{}, Err: {}", indexName, e.getMessage());
                        e.printStackTrace();
                    }
                    return;
                }
                log.warn("添加拼音搜索失败: {}", response.body());
            }

            @Override
            public void onFailure(Call call, IOException e) {
                log.error("Request pinyin-search with error. {}", e.getMessage());
                e.printStackTrace();
            }
        });
    }


    /**
     * 添加索引
     * @param indexName index name
     * @param dataId dataId
     * @param data 数据
     */
    public void deleteIndex(String indexName, String dataId, String data) {
        if (!props.isEnabled()) {
            return;
        }
        Call call = getHttpClient().newCall(getRequest(addUpdateUri, indexName, dataId, data));

        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if (response.code() == 200) {
                    try {
                        assert response.body() != null;
                        log.debug("添加拼音搜索成功! indexName:{}, 返回值: {}", indexName, response.body().string());
                    } catch (Exception e) {
                        log.warn("添加拼音搜索失败! indexName:{}, Err: {}", indexName, e.getMessage());
                        e.printStackTrace();
                    }
                    return;
                }
                log.warn("添加拼音搜索失败: {}", response.body());
            }

            @Override
            public void onFailure(Call call, IOException e) {
                log.error("Request pinyin-search with error. {}", e.getMessage());
                e.printStackTrace();
            }
        });
    }

    /**
     * 拼音搜索建议
     * @param indexName index name
     * @param data data
     * @return 结果
     */
    public PinYinSugResp suggestion(String indexName, String data) {
        if (!props.isEnabled()) {
            return null;
        }
        Call call = getHttpClient().newCall(getRequest(suggestionUri, indexName, null, data));
        try (Response response = call.execute()) {
            return gson.fromJson(Objects.requireNonNull(response.body()).charStream(), PinYinSugResp.class);
        } catch (Exception e) {
            log.warn("获取拼音搜索建议失败: {}", e.getMessage());
            e.printStackTrace();
            return PinYinSugResp.builder().success(false).msg(e.getMessage()).data(new ArrayList<>()).build();
        }
    }

    /**
     * request
     * @param uri uri
     * @param indexName indexName
     * @param dataId dataId
     * @param data 数据
     * @return okhttp request
     */
    private Request getRequest(URI uri, String indexName, String dataId, String data) {
        Assert.notNull(indexName, "The IndexName can't be null");

        HttpUrl.Builder httpBuilder = Objects.requireNonNull(HttpUrl.get(uri)).newBuilder();
        httpBuilder.addQueryParameter("tenant", props.getTenant());
        httpBuilder.addQueryParameter("indexName", indexName);
        if (null != dataId) {
            httpBuilder.addQueryParameter("dataId", dataId);
        }
        httpBuilder.addQueryParameter("data", data);

        return new Request.Builder().url(httpBuilder.build()).get()
                .addHeader("Authorization", props.getAuthorization())
                .build();
    }

}
