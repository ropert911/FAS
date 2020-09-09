package com.xq.fin.analyser.data;

import com.xq.fin.analyser.model.po.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SingleData {
    private GpBaseInfoPo gpBaseInfoPo;
    private List<ZcfzPo> zcfzPoList;
    private List<XjllPo> xjllPoList;
    private List<LrbPo> lrbPoList;
    private List<ZyzbPo> zyzbPoList;
    private List<BfbPo> bfbPoList;

    public SingleData() {
        gpBaseInfoPo = new GpBaseInfoPo();
        zcfzPoList = new ArrayList<>();
        xjllPoList = new ArrayList<>();
        lrbPoList = new ArrayList<>();
        zyzbPoList = new ArrayList<>();
        bfbPoList = new ArrayList<>();
    }
}
