package io.github.pinyinsearch.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.pinyinsearch.entity.PinYinRequest;
import io.github.pinyinsearch.entity.PinYinSuggestResp;
import io.github.pinyinsearch.utils.PinYinSearchUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okio.BufferedSink;
import org.springframework.util.Assert;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
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

    private final static int BATCH_SIZE = 10000;

    private static OkHttpClient okHttpClient;

    private final String updateUrl;
    private final String updateBatchUrl;
    private final String deleteUrl;
    private final String suggestUrl;

    /**
     * constructor
     * @param props props
     */
    public PinYinSearchService(PinYinSearchProperties props) {
        this.props = props;
        String endpointPrefix = props.getEndpoint() + (props.getEndpoint().endsWith("/")?"":"/");
        this.updateUrl = endpointPrefix + "update";
        this.updateBatchUrl = endpointPrefix + "updateBatch";
        this.deleteUrl = endpointPrefix + "delete";
        this.suggestUrl = endpointPrefix + "suggest";
    }

    /**
     * 获取OkHttpClient
     * @return OkHttpClient
     */
    private synchronized OkHttpClient getHttpClient() {
        if (null == okHttpClient) {
            okHttpClient = new OkHttpClient.Builder()
                    .retryOnConnectionFailure(true)
                    .connectionPool(new ConnectionPool(5, 120, TimeUnit.SECONDS))
                    .writeTimeout(Duration.ofSeconds(180))
                    .readTimeout(Duration.ofSeconds(180))
                    .build();
        }
        return okHttpClient;
    }

    /**
     * 更新(添加)索引, 通过反射
     *
     * @param args args
     * @return 成功的条数
     */
    public int updateIndex(Object[] args) {
        if (!props.isEnabled()) {
            return 0;
        }

        List<PinYinRequest> pinYinRequests = new ArrayList<>();

        // 通过反射解析参数
        for (Object arg : args) {
            if (arg instanceof Collection) {
                // 集合迭代所有
                for (Object obj : (Collection<?>) arg) {
                    if (null != obj) {
                        pinYinRequests.addAll(PinYinSearchUtils.getReflectResults(obj, obj.getClass(), props.getTenant()));
                    }
                }
            } else {
                // 非集合, 只迭代当前arg
                pinYinRequests.addAll(PinYinSearchUtils.getReflectResults(arg, arg.getClass(), props.getTenant()));
            }
        }

        if (!pinYinRequests.isEmpty()) {
            if (pinYinRequests.size() > BATCH_SIZE) {
                // 批量处理
                int remainder = pinYinRequests.size() % BATCH_SIZE;
                int number = pinYinRequests.size() / BATCH_SIZE;
                for (int i=0; i<number; i++) {
                    this.updateIndex(pinYinRequests.subList(i*BATCH_SIZE, (i+1) * BATCH_SIZE));
                }
                if (remainder > 0) {
                    this.updateIndex(pinYinRequests.subList(number*BATCH_SIZE, pinYinRequests.size()));
                }
            } else {
                this.updateIndex(pinYinRequests);
            }
        }

        return pinYinRequests.size();
    }

    /**
     * 批量更新(不存在会新增)索引
     * @param pinYinRequests pinYinRequests
     */
    private void updateIndex(List<PinYinRequest> pinYinRequests) {
        if (!props.isEnabled()) {
            return;
        }

        Call call = getHttpClient().newCall(
                new Request.Builder().url(updateBatchUrl).get()
                        .addHeader("Authorization", props.getAuthorization())
                        .post(new RequestBody() {
                            @Override
                            public MediaType contentType() {
                                return MediaType.get("application/json");
                            }

                            @Override
                            public void writeTo(BufferedSink sink) throws IOException {
                                sink.write(gson.toJson(pinYinRequests).getBytes(StandardCharsets.UTF_8));
                            }

                        })
                        .build()
        );

        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                try {
                    assert response.body() != null;
                    if (response.code() == 200) {
                        log.debug("批量更新(新增)成功! 返回值: {}", response.body().string());
                        return;
                    }
                    log.warn("批量更新(新增)失败！返回值: {}", response.body().string());
                }catch (Exception e) {
                    log.warn("批量更新(新增)失败! Err: {}", e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                log.error("批量更新发生异常! Err: {}", e.getMessage());
                e.printStackTrace();
            }
        });

    }

    /**
     * 更新(添加)索引 单次
     * @param indexName index name
     * @param dataId 数据Id(通过数据Id更新索引)
     * @param data 数据
     */
    public void updateIndex(String indexName, String dataId, String data) {
        if (!props.isEnabled()) {
            return;
        }
        Call call = getHttpClient().newCall(getRequest(updateUrl, indexName, dataId, data));

        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                try {
                    assert response.body() != null;
                    if (response.code() == 200) {
                        log.debug("更新(新增)拼音搜索成功! indexName:{}, 返回值: {}", indexName, response.body().string());
                        return;
                    }
                    log.warn("更新(新增)拼音搜索失败！返回值: {}", response.body().string());
                }catch (Exception e) {
                    log.warn("更新(新增)拼音搜索失败! Err: {}", e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                log.error("更新(新增)拼音发生异常! Err: {}", e.getMessage());
                e.printStackTrace();
            }
        });
    }

    /**
     * 添加索引
     * @param indexName index name
     * @param dataId dataId
     */
    public void deleteIndex(String indexName, String dataId) {
        if (!props.isEnabled()) {
            return;
        }
        Call call = getHttpClient().newCall(getRequest(deleteUrl, indexName, dataId, null));

        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                try {
                    assert response.body() != null;
                    if (response.code() == 200) {
                        log.debug("删除拼音搜索索引成功! indexName:{}, 返回值: {}", indexName, response.body().string());
                        return;
                    }
                    log.warn("删除拼音索引失败！返回值: {}", response.body().string());
                }catch (Exception e) {
                    log.warn("删除拼音索引失败! Err: {}", e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                log.error("删除拼音索引发生异常! Err: {}", e.getMessage());
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
    public PinYinSuggestResp suggest(String indexName, String data) {
        if (!props.isEnabled()) {
            return null;
        }
        Call call = getHttpClient().newCall(getRequest(suggestUrl, indexName, null, data));
        try (Response response = call.execute()) {
            if (response.code() == 200) {
                assert response.body() != null;
                return gson.fromJson(Objects.requireNonNull(response.body()).charStream(), PinYinSuggestResp.class);
            }

            assert response.body() != null;
            log.warn("获取拼音搜索建议失败: {}", response.body().string());
            return PinYinSuggestResp.builder().success(false).msg(response.body().string()).data(new ArrayList<>()).build();
        } catch (Exception e) {
            log.warn("获取拼音搜索建议失败发生异常: {}", e.getMessage());
            e.printStackTrace();
            return PinYinSuggestResp.builder().success(false).msg(e.getMessage()).data(new ArrayList<>()).build();
        }
    }

    /**
     * request
     * @param url url
     * @param indexName indexName
     * @param dataId dataId
     * @param data 数据
     * @return okhttp request
     */
    private Request getRequest(String url, String indexName, String dataId, String data) {
        Assert.notNull(indexName, "The IndexName can't be null");

        HttpUrl.Builder httpBuilder = Objects.requireNonNull(HttpUrl.get(url)).newBuilder();
        httpBuilder.addQueryParameter("tenant", props.getTenant());
        httpBuilder.addQueryParameter("indexName", indexName);
        if (null != dataId) {
            httpBuilder.addQueryParameter("dataId", dataId);
        }
        if (null != data) {
            httpBuilder.addQueryParameter("data", data);
        }

        return new Request.Builder().url(httpBuilder.build()).get()
                .addHeader("Authorization", props.getAuthorization())
                .build();
    }

    /**
     * 删除索引, 通过反射
     *
     * @param entityClass 关联的实体
     * @param args args
     */
    public void deleteIndex(Class<?> entityClass, Object[] args) {
        for (Object arg : args) {
            if (arg instanceof String) {
                deleteIndex(entityClass, new String[]{(String)arg});
            } else if (arg instanceof String[]) {
                deleteIndex(entityClass, (String[])arg);
            } else if (arg instanceof List) {
                List<?> list = (List<?>) arg;
                String[] myArray = new String[list.size()];
                int i = 0;
                for (Object id : list) {
                    if (id instanceof String) {
                        myArray[i] = (String) id;
                        i++;
                    }
                }
                deleteIndex(entityClass, Arrays.copyOfRange(myArray, 0, i));
            }
        }
    }

    /**
     * 删除索引, 通过反射
     *
     * @param entityClass 关联的实体
     * @param dataIds dataId集合
     */
    public void deleteIndex(Class<?> entityClass, String[] dataIds) {
        for (String dataId : dataIds) {
            String indexNamePrefix = PinYinSearchUtils.getIndexNamePrefix(entityClass);
            if (null != indexNamePrefix) {
                Map<String, Field> fieldsMap = PinYinSearchUtils.getFields(entityClass.getDeclaredFields());
                for (Map.Entry<String, Field> entry : fieldsMap.entrySet()) {
                    deleteIndex(indexNamePrefix + "_" + entry.getKey(), dataId);
                }
            }
        }
    }


}
