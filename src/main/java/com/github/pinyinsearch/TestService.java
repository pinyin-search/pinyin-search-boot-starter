package com.github.pinyinsearch;

import com.github.pinyinsearch.config.annotation.PinYinSearch;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
public class TestService {

    @PinYinSearch(indexNamePrefix = "method", entityFieldName = "name")
    public void testMethod(MyEntity para) {
    }

    @PinYinSearch(indexNamePrefix = "method")
    public void testMethod(String para) {
    }

    public void testParameter(@PinYinSearch(indexNamePrefix = "para") String para) {
    }

}

@Data
@AllArgsConstructor
class MyEntity implements Serializable {
    private String name;
    private Integer age;
}
