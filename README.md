# pinyin-search-boot-starter

spring boot 项目使用注解的方式快速搭建拼音分词搜索。需搭配 [pinyin-search](https://github.com/pinyin-search/pinyin-search) 



## 依赖
```
<dependency>
  <groupId>io.github.pinyin-search</groupId>
  <artifactId>pinyin-search-boot-starter</artifactId>
  <version>0.1.2</version>
</dependency>
```

## 基本使用
```
@RestController
@RequestMapping(value = "/demo" )
public class DemoController {
    @PostMapping("/add")
    @PinYinSearch
    public Result<String> add(@Validated @RequestBody Demo demo) {
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

### 配置项目中的yaml
endpoint为 [pinyin-search](https://github.com/pinyin-search/pinyin-search) 服务
```yaml
pinyin:
  search:
    enabled: true
    tenant: ${spring.application.name}
    endpoint: http://127.0.0.1:7701
    authorization:
```