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

            {
                zyzbVo.setCode(code);
            }
            {
                String pStr = jsonObject.getString("date");
                List<String> date = StringUtil.getmutiString("([0-9]{4})-([0-9]{1,2})", pStr);
                zyzbVo.setTime(String.format("%s%02d", date.get(0), Long.valueOf(date.get(1))));
            }
            {
                String pStr = jsonObject.getString("jbmgsy");
                zyzbVo.setJbmgsy(pStr.equals("--") ? 0 : Double.valueOf(pStr));
            }
            {
                String pStr = jsonObject.getString("kfmgsy");
                zyzbVo.setKfmgsy(pStr.equals("--") ? 0 : Double.valueOf(pStr));
            }
            {
                String pStr = jsonObject.getString("xsmgsy");
                zyzbVo.setXsmgsy(pStr.equals("--") ? 0 : Double.valueOf(pStr));
            }
            {
                String pStr = jsonObject.getString("mgjzc");
                zyzbVo.setMgjzc(pStr.equals("--") ? 0 : Double.valueOf(pStr));
            }
            {
                String pStr = jsonObject.getString("mggjj");
                zyzbVo.setMggjj(pStr.equals("--") ? 0 : Double.valueOf(pStr));
            }
            {
                String pStr = jsonObject.getString("mgwfply");
                zyzbVo.setMgwfply(pStr.equals("--") ? 0 : Double.valueOf(pStr));
            }
            {
                String pStr = jsonObject.getString("mgjyxjl");
                zyzbVo.setMgjyxjl(pStr.equals("--") ? 0 : Double.valueOf(pStr));
            }
            {
                String pStr = jsonObject.getString("yyzsr");
                if (pStr.contains("亿")) {
                    pStr = pStr.replace("亿", "");
                    zyzbVo.setYyzsr(pStr.equals("--") ? 0 : Double.valueOf(pStr) * 100000000);
                } else if (pStr.contains("万")) {
                    pStr = pStr.replace("万", "");
                    zyzbVo.setYyzsr(pStr.equals("--") ? 0 : Double.valueOf(pStr) * 10000);
                } else {
                    zyzbVo.setYyzsr(pStr.equals("--") ? 0 : Double.valueOf(pStr));
                }
            }
            {
                String pStr = jsonObject.getString("mlr");
                if (pStr.contains("亿")) {
                    pStr = pStr.replace("亿", "");
                    zyzbVo.setMlr(pStr.isEmpty() ? 0 : Double.valueOf(pStr) * 100000000);
                } else if (pStr.contains("万")) {
                    pStr = pStr.replace("万", "");
                    zyzbVo.setMlr(pStr.isEmpty() ? 0 : Double.valueOf(pStr) * 10000);
                } else {
                    zyzbVo.setMlr(pStr.equals("--") ? 0 : Double.valueOf(pStr));
                }
            }
            {
                String pStr = jsonObject.getString("gsjlr");
                if (pStr.contains("亿")) {
                    pStr = pStr.replace("亿", "");
                    zyzbVo.setGsjlr(pStr.isEmpty() ? 0 : Double.valueOf(pStr) * 100000000);
                } else if (pStr.contains("万")) {
                    pStr = pStr.replace("万", "");
                    zyzbVo.setGsjlr(pStr.isEmpty() ? 0 : Double.valueOf(pStr) * 10000);
                } else {
                    zyzbVo.setGsjlr(pStr.equals("--") ? 0 : Double.valueOf(pStr));
                }
            }
            {
                String pStr = jsonObject.getString("kfjlr");
                if (pStr.contains("亿")) {
                    pStr = pStr.replace("亿", "");
                    zyzbVo.setKfjlr(pStr.isEmpty() ? 0 : Double.valueOf(pStr) * 100000000);
                } else if (pStr.contains("万")) {
                    pStr = pStr.replace("万", "");
                    zyzbVo.setKfjlr(pStr.isEmpty() ? 0 : Double.valueOf(pStr) * 100000000);
                } else {
                    zyzbVo.setKfjlr(pStr.equals("--") ? 0 : Double.valueOf(pStr));
                }
            }
            {
                String pStr = jsonObject.getString("yyzsrtbzz");
                zyzbVo.setYyzsrtbzz(pStr.equals("--") ? 0 : Double.valueOf(pStr));
            }
            {
                String pStr = jsonObject.getString("gsjlrtbzz");
                zyzbVo.setGsjlrtbzz(pStr.equals("--") ? 0 : Double.valueOf(pStr));
            }
            {
                String pStr = jsonObject.getString("kfjlrtbzz");
                zyzbVo.setKfjlrtbzz(pStr.equals("--") ? 0 : Double.valueOf(pStr));
            }
            {
                String pStr = jsonObject.getString("yyzsrgdhbzz");
                zyzbVo.setYyzsrgdhbzz(pStr.equals("--") ? 0 : Double.valueOf(pStr));
            }
            {
                String pStr = jsonObject.getString("gsjlrgdhbzz");
                zyzbVo.setGsjlrgdhbzz(pStr.equals("--") ? 0 : Double.valueOf(pStr));
            }
            {
                String pStr = jsonObject.getString("kfjlrgdhbzz");
                zyzbVo.setKfjlrgdhbzz(pStr.equals("--") ? 0 : Double.valueOf(pStr));
            }
            {
                String pStr = jsonObject.getString("jqjzcsyl");
                zyzbVo.setJqjzcsyl(pStr.equals("--") ? 0 : Double.valueOf(pStr));
            }
            {
                String pStr = jsonObject.getString("tbjzcsyl");
                zyzbVo.setTbjzcsyl(pStr.equals("--") ? 0 : Double.valueOf(pStr));
            }
            {
                String pStr = jsonObject.getString("tbzzcsyl");
                zyzbVo.setTbzzcsyl(pStr.equals("--") ? 0 : Double.valueOf(pStr));
            }
            {
                String pStr = jsonObject.getString("mll");
                zyzbVo.setMll(pStr.equals("--") ? 0 : Double.valueOf(pStr));
            }
            {
                String pStr = jsonObject.getString("jll");
                zyzbVo.setJll(pStr.equals("--") ? 0 : Double.valueOf(pStr));
            }
            {
                String pStr = jsonObject.getString("sjsl");
                zyzbVo.setSjsl(pStr.equals("--") ? 0 : Double.valueOf(pStr));
            }
            {
                String pStr = jsonObject.getString("yskyysr");
                zyzbVo.setYskyysr(pStr.equals("--") ? 0 : Double.valueOf(pStr));
            }
            {
                String pStr = jsonObject.getString("xsxjlyysr");
                zyzbVo.setXsxjlyysr(pStr.equals("--") ? 0 : Double.valueOf(pStr));
            }
            {
                String pStr = jsonObject.getString("jyxjlyysr");
                zyzbVo.setJyxjlyysr(pStr.equals("--") ? 0 : Double.valueOf(pStr));
            }
            {
                String pStr = jsonObject.getString("zzczzy");
                zyzbVo.setZzczzy(pStr.equals("--") ? 0 : Double.valueOf(pStr));
            }
            {
                String pStr = jsonObject.getString("yszkzzts");
                zyzbVo.setYszkzzts(pStr.equals("--") ? 0 : Double.valueOf(pStr));
            }
            {
                String pStr = jsonObject.getString("chzzts");
                zyzbVo.setChzzts(pStr.equals("--") ? 0 : Double.valueOf(pStr));
            }
            {
                String pStr = jsonObject.getString("zcfzl");
                zyzbVo.setZcfzl(pStr.equals("--") ? 0 : Double.valueOf(pStr));
            }
            {
                String pStr = jsonObject.getString("ldzczfz");
                zyzbVo.setLdzczfz(pStr.equals("--") ? 0 : Double.valueOf(pStr));
            }
            {
                String pStr = jsonObject.getString("ldbl");
                zyzbVo.setLdbl(pStr.equals("--") ? 0 : Double.valueOf(pStr));
            }
            {
                String pStr = jsonObject.getString("sdbl");
                zyzbVo.setSdbl(pStr.equals("--") ? 0 : Double.valueOf(pStr));
            }

            zyzbVoList.add(zyzbVo);

        }

        return zyzbVoList;
    }
}
