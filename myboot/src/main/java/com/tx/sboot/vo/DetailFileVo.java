package com.tx.sboot.vo;

/**
 * @Author hxs
 * @Date 2021/4/25 16:08
 * @Description
 * @Version 1.0
 */
public class DetailFileVo {
    private String year;
    private String tradeFlow;
    private String reporter;
    private String partner;
    private String code;
    private Double TradeValue;
    private Double netWeight;
    private String unit;
    private Double tradeQuantity;



    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getTradeFlow() {
        return tradeFlow;
    }

    public void setTradeFlow(String tradeFlow) {
        this.tradeFlow = tradeFlow;
    }

    public String getReporter() {
        return reporter;
    }

    public void setReporter(String reporter) {
        this.reporter = reporter;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPartner() {
        return partner;
    }

    public void setPartner(String partner) {
        this.partner = partner;
    }

    public Double getNetWeight() {
        return netWeight;
    }

    public void setNetWeight(Double netWeight) {
        this.netWeight = netWeight;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Double getTradeQuantity() {
        return tradeQuantity;
    }

    public void setTradeQuantity(Double tradeQuantity) {
        this.tradeQuantity = tradeQuantity;
    }

    public Double getTradeValue() {
        return TradeValue;
    }

    public void setTradeValue(Double tradeValue) {
        TradeValue = tradeValue;
    }
}
