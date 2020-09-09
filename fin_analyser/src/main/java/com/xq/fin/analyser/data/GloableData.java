package com.xq.fin.analyser.data;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class GloableData {
    public Map<String, SingleData> allStockList = new HashMap<>();
    public void clean(){
        allStockList = new HashMap<>();
    }
}
