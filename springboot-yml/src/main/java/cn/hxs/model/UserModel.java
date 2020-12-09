package cn.hxs.model;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;


import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.*;

/**
 * @Author hxs
 * @Date 2020/12/4 17:05
 * @Description
 * @Version 1.0
 */
@Data
@ConfigurationProperties(prefix ="yml")
@Validated
public class UserModel {
    @NotEmpty
    private String name;
    @Max(50)
    private Integer age;
    private Date birth;
    private List<String> hobby = new ArrayList<>();
    private List<String> cars = new ArrayList<>();
    private Map<String,String> map = new HashMap<>();
    @Valid
    private List<ChildModel> children = new ArrayList<>();

    public UserModel() {

    }
}
