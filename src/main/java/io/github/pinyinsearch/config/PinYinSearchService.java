package io.github.pinyinsearch.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.pinyinsearch.entity.PinYinSuggestResp;
import io.github.pinyinsearch.utils.PinYinSearchUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URI;
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

    private static OkHttpClient okHttpClient;

    private final URI addUpdateUri;
    private final URI deleteUri;
    private final URI suggestUri;

    /**
     * constructor
     * @param props props
     */
    public PinYinSearchService(PinYinSearchProperties props) {
        this.props = props;
        this.addUpdateUri = URI.create(props.getEndpoint() + (props.getEndpoint().endsWith("/")?"":"/") + "addUpdate");
        this.deleteUri = URI.create(props.getEndpoint() + (props.getEndpoint().endsWith("/")?"":"/") + "delete");
        this.suggestUri = URI.create(props.getEndpoint() + (props.getEndpoint().endsWith("/")?"":"/") + "suggest");
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
     * @param dataId 数据Id(通过数据Id更新索引)
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
                try {
                    assert response.body() != null;
                    log.warn("添加拼音搜索失败: {}", response.body().string());
                }catch (Exception e) {
                    // ignore
                }
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
     */
    public void deleteIndex(String indexName, String dataId) {
        if (!props.isEnabled()) {
            return;
        }
        Call call = getHttpClient().newCall(getRequest(deleteUri, indexName, dataId, null));

        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if (response.code() == 200) {
                    try {
                        assert response.body() != null;
                        log.debug("删除拼音搜索索引成功! indexName:{}, 返回值: {}", indexName, response.body().string());
                    } catch (Exception e) {
                        log.warn("删除拼音搜索索引失败! indexName:{}, Err: {}", indexName, e.getMessage());
                        e.printStackTrace();
                    }
                    return;
                }
                try {
                    assert response.body() != null;
                    log.warn("删除拼音索引失败: {}", response.body().string());
                }catch (Exception e) {
                    // ignore
                }
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
    public PinYinSuggestResp suggest(String indexName, String data) {
        if (!props.isEnabled()) {
            return null;
        }
        Call call = getHttpClient().newCall(getRequest(suggestUri, indexName, null, data));
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
        if (null != data) {
            httpBuilder.addQueryParameter("data", data);
        }

        return new Request.Builder().url(httpBuilder.build()).get()
                .addHeader("Authorization", props.getAuthorization())
                .build();
    }

    /**
     * 添加或更新索引, 通过反射
     * @param args args
     * @return 是否有一次成功的请求
     */
    public boolean addUpdateIndex(Object[] args) {
        boolean find = false;
        for (Object arg : args) {
            String indexNamePrefix = PinYinSearchUtils.getIndexNamePrefix(arg.getClass());
            if (null != indexNamePrefix) {
                Field[] fields = arg.getClass().getDeclaredFields();
                Field fieldId = PinYinSearchUtils.getFieldId(fields);

                // field
                if (fieldId == null) {
                    continue;
                }

                fieldId.setAccessible(true);
                Object dataId = ReflectionUtils.getField(fieldId, arg);

                if (dataId instanceof String) {
                    Map<String, Field> fieldsMap = PinYinSearchUtils.getFields(fields);
                    for (Map.Entry<String, Field> entry : fieldsMap.entrySet()) {
                        entry.getValue().setAccessible(true);
                        Object value = ReflectionUtils.getField(entry.getValue(), arg);
                        if (value instanceof String) {
                            addUpdateIndex(indexNamePrefix + "_" + entry.getKey(), (String) dataId, (String)value);
                            find = true;
                        } else {
                            log.warn("{}#{} 获取的值类型不是字符串", arg.getClass().getSimpleName(), entry.getValue().getName());
                        }
                    }
                } else {
                    log.warn("{}#{} 字段中不能获取字符串", arg.getClass().getSimpleName(), fieldId.getName());
                }
            }
        }
        return find;
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
