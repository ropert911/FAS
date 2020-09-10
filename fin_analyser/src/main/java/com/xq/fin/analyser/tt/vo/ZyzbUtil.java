package com.xq.fin.analyser.tt.vo;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xq.fin.analyser.util.StringUtil;
import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Component
public class ZyzbUtil {
    private static Logger logger = LoggerFactory.getLogger(ZyzbUtil.class);

    @Autowired
    private RestTemplate restTemplate;

    public List<ZyzbVo> getData(String code) {
        List<ZyzbVo> zyzbVoList = new ArrayList<>(10);
        String url = "http://f10.eastmoney.com/NewFinanceAnalysis/MainTargetAjax?type=0&code=" + StringUtil.getSCByCode(code);
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
        String data = responseEntity.getBody();
        data = StringEscapeUtils.unescapeJava(data);
        if (data.startsWith("\"")) {
            data = data.substring(1, data.length() - 1);
        }

        JSONArray jsonArray = JSONObject.parseArray(data);
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            ZyzbVo zyzbVo = new ZyzbVo();

            zyzbVo.setCode(code);
            zyzbVo.setTime(StringUtil.getTime(jsonObject.getString("date")));
            zyzbVo.setJbmgsy(StringUtil.getDouble(jsonObject.getString("jbmgsy")));
            zyzbVo.setKfmgsy(StringUtil.getDouble(jsonObject.getString("kfmgsy")));
            zyzbVo.setXsmgsy(StringUtil.getDouble(jsonObject.getString("xsmgsy")));
            zyzbVo.setMgjzc(StringUtil.getDouble(jsonObject.getString("mgjzc")));
            zyzbVo.setMggjj(StringUtil.getDouble(jsonObject.getString("mggjj")));
            zyzbVo.setMgwfply(StringUtil.getDouble(jsonObject.getString("mgwfply")));
            zyzbVo.setMgjyxjl(StringUtil.getDouble(jsonObject.getString("mgjyxjl")));
            zyzbVo.setYyzsr(StringUtil.getDouble(jsonObject.getString("yyzsr")));
            zyzbVo.setMlr(StringUtil.getDouble(jsonObject.getString("mlr")));
            zyzbVo.setGsjlr(StringUtil.getDouble(jsonObject.getString("gsjlr")));
            zyzbVo.setKfjlr(StringUtil.getDouble(jsonObject.getString("kfjlr")));
            zyzbVo.setYyzsrtbzz(StringUtil.getDouble(jsonObject.getString("yyzsrtbzz")));
            zyzbVo.setGsjlrtbzz(StringUtil.getDouble(jsonObject.getString("gsjlrtbzz")));
            zyzbVo.setKfjlrtbzz(StringUtil.getDouble(jsonObject.getString("kfjlrtbzz")));
            zyzbVo.setYyzsrgdhbzz(StringUtil.getDouble(jsonObject.getString("yyzsrgdhbzz")));
            zyzbVo.setGsjlrgdhbzz(StringUtil.getDouble(jsonObject.getString("gsjlrgdhbzz")));
            zyzbVo.setKfjlrgdhbzz(StringUtil.getDouble(jsonObject.getString("kfjlrgdhbzz")));
            zyzbVo.setJqjzcsyl(StringUtil.getDouble(jsonObject.getString("jqjzcsyl")));
            zyzbVo.setTbjzcsyl(StringUtil.getDouble(jsonObject.getString("tbjzcsyl")));
            zyzbVo.setTbzzcsyl(StringUtil.getDouble(jsonObject.getString("tbzzcsyl")));
            zyzbVo.setMll(StringUtil.getDouble(jsonObject.getString("mll")));
            zyzbVo.setJll(StringUtil.getDouble(jsonObject.getString("jll")));
            zyzbVo.setSjsl(StringUtil.getDouble(jsonObject.getString("sjsl")));
            zyzbVo.setYskyysr(StringUtil.getDouble(jsonObject.getString("yskyysr")));
            zyzbVo.setXsxjlyysr(StringUtil.getDouble(jsonObject.getString("xsxjlyysr")));
            zyzbVo.setJyxjlyysr(StringUtil.getDouble(jsonObject.getString("jyxjlyysr")));
            zyzbVo.setZzczzy(StringUtil.getDouble(jsonObject.getString("zzczzy")));
            zyzbVo.setYszkzzts(StringUtil.getDouble(jsonObject.getString("yszkzzts")));
            zyzbVo.setChzzts(StringUtil.getDouble(jsonObject.getString("chzzts")));
            zyzbVo.setZcfzl(StringUtil.getDouble(jsonObject.getString("zcfzl")));
            zyzbVo.setLdzczfz(StringUtil.getDouble(jsonObject.getString("ldzczfz")));
            zyzbVo.setLdbl(StringUtil.getDouble(jsonObject.getString("ldbl")));
            zyzbVo.setSdbl(StringUtil.getDouble(jsonObject.getString("sdbl")));

            zyzbVoList.add(zyzbVo);

        }

        return zyzbVoList;
    }
}
