package com.tx.sboot.vo;

/**
 * @Author hxs
 * @Date 2021/4/25 16:08
 * @Description
 * @Version 1.0
 */
public class CodeFileVo {
    private String code; //唯一值
    private Double coefficient;//系数

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Double getCoefficient() {
        return coefficient;
    }

    public void setCoefficient(Double coefficient) {
        this.coefficient = coefficient;
    }
}
