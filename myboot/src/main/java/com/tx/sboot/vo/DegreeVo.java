package com.tx.sboot.vo;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author hxs
 * @Date 2021/7/5 17:54
 * @Description
 * @Version 1.0
 */
public class DegreeVo {
    private List<GroupVo> inDegrees;
    private List<GroupVo> outDegrees;
    private List<GroupVo> degrees;
    private List<GroupVo> betweenessCentralitys;

    private List<GroupVo> gs;
    private List<GroupVo> hs;
    private List<GroupVo> is;
    private List<GroupVo> ks;
    private List<GroupVo> os;
    private List<GroupVo> qs;

    public List<GroupVo> getInDegrees() {
        return inDegrees;
    }

    public void setInDegrees(List<GroupVo> inDegrees) {
        this.inDegrees = inDegrees;
    }

    public List<GroupVo> getOutDegrees() {
        return outDegrees;
    }

    public void setOutDegrees(List<GroupVo> outDegrees) {
        this.outDegrees = outDegrees;
    }

    public List<GroupVo> getDegrees() {
        return degrees;
    }

    public void setDegrees(List<GroupVo> degrees) {
        this.degrees = degrees;
    }

    public List<GroupVo> getBetweenessCentralitys() {
        return betweenessCentralitys;
    }

    public void setBetweenessCentralitys(List<GroupVo> betweenessCentralitys) {
        this.betweenessCentralitys = betweenessCentralitys;
    }

    public List<GroupVo> getGs() {
        return gs;
    }

    public void setGs(List<GroupVo> gs) {
        this.gs = gs;
    }

    public List<GroupVo> getHs() {
        return hs;
    }

    public void setHs(List<GroupVo> hs) {
        this.hs = hs;
    }

    public List<GroupVo> getIs() {
        return is;
    }

    public void setIs(List<GroupVo> is) {
        this.is = is;
    }

    public List<GroupVo> getKs() {
        return ks;
    }

    public void setKs(List<GroupVo> ks) {
        this.ks = ks;
    }

    public List<GroupVo> getOs() {
        return os;
    }

    public void setOs(List<GroupVo> os) {
        this.os = os;
    }

    public List<GroupVo> getQs() {
        return qs;
    }

    public void setQs(List<GroupVo> qs) {
        this.qs = qs;
    }
}
