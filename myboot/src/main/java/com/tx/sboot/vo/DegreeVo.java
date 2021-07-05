package com.tx.sboot.vo;

import java.util.List;

/**
 * @Author hxs
 * @Date 2021/7/5 17:54
 * @Description
 * @Version 1.0
 */
public class DegreeVo {
    private List<GroupVo>   inDegrees;
    private List<GroupVo>   outDegrees;
    private List<GroupVo>   degrees;
    private List<GroupVo>   betweenessCentralitys;

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
}
