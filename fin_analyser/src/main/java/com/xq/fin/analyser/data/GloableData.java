package com.xq.fin.analyser.data;

import com.xq.fin.analyser.pojo.ZcfzPo;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class GloableData {
    public Map<String, SingleData> allStockList = new HashMap<>();
}
