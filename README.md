# pinyin-search-boot-starter

spring boot 项目使用注解的方式快速搭建拼音分词搜索。需搭配 [pinyin-search](https://github.com/pinyin-search/pinyin-search) 



## 依赖
```
<dependency>
  <groupId>io.github.pinyin-search</groupId>
  <artifactId>pinyin-search-boot-starter</artifactId>
  <version>0.3.1</version>
</dependency>
```

## 基本使用
```
@Service
public class DemoServiceImpl extends DemoService {

    @PinYinSearchUpdate
    public boolean add(Demo demo) {
        // 具体业务
    }

    @PinYinSearchUpdate
    public boolean edit(Demo demo) {
        // 具体业务
    }

    @PinYinSearchDelete(Demo.class)
    public boolean del(@PathVariable String guid) {
        // 具体业务
    }

}
```

### 定义实体
```
@PinYinSearchEntity
public class Demo implements Serializable {
    // 根据此id更新索引
    @PinYinSearchId
    private String id;

    // 需要添加拼音搜索的字段
    @PinYinSearchField
    private String name;

}
```

### 拼音搜索-使用参考
```
@RestController
@RequestMapping(value = "/search" )
public class SearchController {

    @Resource
    private PinYinSearchService pinYinSearchService;

    /**
     * 搜索建议
     *
     * @param search 实体
     */
    @PostMapping("/suggest")
    @ResponseBody
    public Result suggest(@Validated @RequestBody Search search){
        PinYinSuggestResp resp = pinYinSearchService.suggest(search.getIndex() + "_" + search.getField(), search.getKeyword());
        if (null != resp && null != resp.getData()) {
            List<String> results = new ArrayList<>();
            for (PinYinSuggestRespData d : resp.getData()) {
                results.add(d.getValue());
            }
            return Result.success(results);
        }
        return Result.success(new ArrayList<>());
    }

}
```

### 配置项目中的yaml
endpoint为 [pinyin-search](https://github.com/pinyin-search/pinyin-search) 服务
```yaml
pinyin-search:
  enabled: true
  endpoint: https://pinyin-search.xxxxxxx.com
  authorization: xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
  tenant: ${spring.application.name}
```