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

    private Double g;
    private Double h;
    private Double i;
    private Double k;
    private Double  betweenessCentrality;

    private Double o;
    private Double q;

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

    public Double getG() {
        return g;
    }

    public void setG(Double g) {
        this.g = g;
    }

    public Double getH() {
        return h;
    }

    public void setH(Double h) {
        this.h = h;
    }

    public Double getI() {
        return i;
    }

    public void setI(Double i) {
        this.i = i;
    }

    public Double getK() {
        return k;
    }

    public void setK(Double k) {
        this.k = k;
    }

    public Double getO() {
        return o;
    }

    public void setO(Double o) {
        this.o = o;
    }

    public Double getQ() {
        return q;
    }

    public void setQ(Double q) {
        this.q = q;
    }
}
