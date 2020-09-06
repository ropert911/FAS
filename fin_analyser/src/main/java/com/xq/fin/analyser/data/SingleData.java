package com.xq.fin.analyser.data;

import com.xq.fin.analyser.pojo.LrbPo;
import com.xq.fin.analyser.pojo.XjllPo;
import com.xq.fin.analyser.pojo.ZcfzPo;
import com.xq.fin.analyser.pojo.ZyzbPo;

import java.util.ArrayList;
import java.util.List;

public class SingleData {
    private List<ZcfzPo> zcfzPoList;
    private List<XjllPo> xjllPoList;
    private List<LrbPo> lrbPoList;
    private List<ZyzbPo> zyzbPoList;

    public List<ZcfzPo> getZcfzPoList() {
        return zcfzPoList;
    }

    public void setZcfzPoList(List<ZcfzPo> zcfzPoList) {
        this.zcfzPoList = zcfzPoList;
    }

    public List<XjllPo> getXjllPoList() {
        return xjllPoList;
    }

    public void setXjllPoList(List<XjllPo> xjllPoList) {
        this.xjllPoList = xjllPoList;
    }

    public List<LrbPo> getLrbPoList() {
        return lrbPoList;
    }

    public void setLrbPoList(List<LrbPo> lrbPoList) {
        this.lrbPoList = lrbPoList;
    }

    public List<ZyzbPo> getZyzbPoList() {
        return zyzbPoList;
    }

    public void setZyzbPoList(List<ZyzbPo> zyzbPoList) {
        this.zyzbPoList = zyzbPoList;
    }
}
