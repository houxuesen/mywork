package cn.hxs.model;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @Author hxs
 * @Date 2020/12/4 17:09
 * @Description
 * @Version 1.0
 */
@Data
public class ChildModel {
    @NotEmpty
    private String name;
    @Max(20)
    private Integer age;

    public ChildModel() {
    }

    public ChildModel(String name, Integer age) {
        this.name = name;
        this.age = age;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
