package com.xq.fin.analyser.data;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xq.fin.analyser.tt.vo.BfbVo;
import com.xq.fin.analyser.util.StringUtil;
import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class BfbUtil {
    private static Logger logger = LoggerFactory.getLogger(BfbUtil.class);

    @Autowired
    private RestTemplate restTemplate;

    public List<BfbVo> getData(String code) {
        List<BfbVo> bfbVoList = new ArrayList<>(10);
        String url = "http://f10.eastmoney.com/NewFinanceAnalysis/PercentAjax?code=" + StringUtil.getSCByCode(code) + "&ctype=4&type=0";
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
        String data = responseEntity.getBody();
        data = StringEscapeUtils.unescapeJava(data);
        if (data.startsWith("\"")) {
            data = data.substring(1, data.length() - 1);
        }

        JSONObject origJsonObject = JSONObject.parseObject(data);
        JSONArray jsonArray = origJsonObject.getJSONArray("lr0");
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            BfbVo bfbVo = new BfbVo();

            bfbVo.setCode(code);
            bfbVo.setTime(StringUtil.getTime(jsonObject.getString("date")));
            bfbVo.setYysr(StringUtil.getDouble(jsonObject.getString("yysr")));
            bfbVo.setYysjjfj(StringUtil.getDouble(jsonObject.getString("yysjjfj")));
            bfbVo.setQjfy(StringUtil.getDouble(jsonObject.getString("qjfy")));
            bfbVo.setXsfy(StringUtil.getDouble(jsonObject.getString("xsfy")));
            bfbVo.setGlfy(StringUtil.getDouble(jsonObject.getString("glfy")));
            bfbVo.setCwfy(StringUtil.getDouble(jsonObject.getString("cwfy")));
            bfbVo.setZcjzss(StringUtil.getDouble(jsonObject.getString("zcjzss")));
            bfbVo.setQtjysy(StringUtil.getDouble(jsonObject.getString("qtjysy")));
            bfbVo.setGyjzbdsy(StringUtil.getDouble(jsonObject.getString("gyjzbdsy")));
            bfbVo.setTzsy(StringUtil.getDouble(jsonObject.getString("tzsy")));
            bfbVo.setYylr(StringUtil.getDouble(jsonObject.getString("yylr")));
            bfbVo.setYywsr(StringUtil.getDouble(jsonObject.getString("yywsr")));
            bfbVo.setBtsr(StringUtil.getDouble(jsonObject.getString("btsr")));
            bfbVo.setYywzc(StringUtil.getDouble(jsonObject.getString("yywzc")));
            bfbVo.setLrze(StringUtil.getDouble(jsonObject.getString("lrze")));
            bfbVo.setSds(StringUtil.getDouble(jsonObject.getString("sds")));
            bfbVo.setJlr(StringUtil.getDouble(jsonObject.getString("jlr")));

            bfbVoList.add(bfbVo);
        }

        return bfbVoList;
    }
}
