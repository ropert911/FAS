package com.xq.fin.analyser.service;

import com.xq.fin.analyser.model.SingleData;
import com.xq.fin.analyser.model.po.BfbPo;
import com.xq.fin.analyser.model.po.LrbPo;
import com.xq.fin.analyser.model.po.XjllPo;
import com.xq.fin.analyser.model.po.ZyzbPo;
import com.xq.fin.analyser.model.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * @author sk-qianxiao
 * @date 2020/9/10
 */
@Service
public class DataService {
    @Autowired
    private BaseInfoRepository baseInfoRepository;
    @Autowired
    private ZcfzRepository zcfzRepository;
    @Autowired
    private XjllRepository xjllRepository;
    @Autowired
    private LrbRepository lrbRepository;
    @Autowired
    private ZyzbRepository zyzbRepository;
    @Autowired
    private BfbRepository bfbRepository;

    public void save(SingleData singleData) {
        baseInfoRepository.save(singleData.getBaseInfoPo());
        zcfzRepository.saveAll(singleData.getZcfzPoList());
        lrbRepository.saveAll(singleData.getLrbPoList());
        xjllRepository.saveAll(singleData.getXjllPoList());
        zyzbRepository.saveAll(singleData.getZyzbPoList());
        bfbRepository.saveAll(singleData.getBfbPoList());
    }

    public SingleData loadData(String code) {
        SingleData singleData = new SingleData();
        singleData.setBaseInfoPo(baseInfoRepository.findByCode(code).get(0));
        singleData.getZcfzPoList().addAll(zcfzRepository.findByCode(code).stream().sorted(Comparator.comparing(com.xq.fin.analyser.model.po.ZcfzPo::getTime).reversed()).collect(Collectors.toList()));
        singleData.getLrbPoList().addAll(lrbRepository.findByCode(code).stream().sorted(Comparator.comparing(LrbPo::getTime).reversed()).collect(Collectors.toList()));
        singleData.getXjllPoList().addAll(xjllRepository.findByCode(code).stream().sorted(Comparator.comparing(XjllPo::getTime).reversed()).collect(Collectors.toList()));
        singleData.getZyzbPoList().addAll(zyzbRepository.findByCode(code).stream().sorted(Comparator.comparing(ZyzbPo::getTime).reversed()).collect(Collectors.toList()));
        singleData.getBfbPoList().addAll(bfbRepository.findByCode(code).stream().sorted(Comparator.comparing(BfbPo::getTime).reversed()).collect(Collectors.toList()));
        return singleData;
    }
}
