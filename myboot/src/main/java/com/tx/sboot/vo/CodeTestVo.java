package com.tx.sboot.vo;

import java.util.List;

/**
 * @Author hxs
 * @Date 2021/4/25 17:04
 * @Description
 * @Version 1.0
 */
public class CodeTestVo {
    private List<CodeFileVo> codeFileVoList;
    private List<CountryVo> countryVoList;

    public List<CodeFileVo> getCodeFileVoList() {
        return codeFileVoList;
    }

    public void setCodeFileVoList(List<CodeFileVo> codeFileVoList) {
        this.codeFileVoList = codeFileVoList;
    }

    public List<CountryVo> getCountryVoList() {
        return countryVoList;
    }

    public void setCountryVoList(List<CountryVo> countryVoList) {
        this.countryVoList = countryVoList;
    }
}
