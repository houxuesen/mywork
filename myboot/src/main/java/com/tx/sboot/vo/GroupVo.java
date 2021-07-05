package com.tx.sboot.vo;

/**
 * @Author hxs
 * @Date 2021/7/5 14:19
 * @Description
 * @Version 1.0
 */
public class GroupVo {
    private String  id;
    private Double  inDegree;
    private Double  outDegree;
    private Double  degree;
    private Double  betweenessCentrality;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Double getInDegree() {
        return inDegree;
    }

    public void setInDegree(Double inDegree) {
        this.inDegree = inDegree;
    }

    public Double getOutDegree() {
        return outDegree;
    }

    public void setOutDegree(Double outDegree) {
        this.outDegree = outDegree;
    }

    public Double getDegree() {
        return degree;
    }

    public void setDegree(Double degree) {
        this.degree = degree;
    }

    public Double getBetweenessCentrality() {
        return betweenessCentrality;
    }

    public void setBetweenessCentrality(Double betweenessCentrality) {
        this.betweenessCentrality = betweenessCentrality;
    }
}
