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
    private GloableData gloableData;
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
            {
                String pStr = jsonObject.getString("date");
                List<String> date = StringUtil.getmutiString("([0-9]{4})-([0-9]{1,2})", pStr);
                bfbVo.setTime(String.format("%s%02d", date.get(0), Long.valueOf(date.get(1))));
            }
            {
                String pStr = jsonObject.getString("yysr");
                if (pStr.contains("亿")) {
                    pStr = pStr.replace("亿", "");
                    bfbVo.setYysr(Double.valueOf(pStr) * 100000000);
                } else if (pStr.contains("万")) {
                    pStr = pStr.replace("万", "");
                    bfbVo.setYysr(Double.valueOf(pStr) * 10000);
                } else {
                    bfbVo.setYysr(pStr.equals("--") ? 0 : Double.valueOf(pStr));
                }
            }
            {
                String pStr = jsonObject.getString("yysjjfj");
                if (pStr.contains("亿")) {
                    pStr = pStr.replace("亿", "");
                    bfbVo.setYysjjfj(Double.valueOf(pStr) * 100000000);
                } else if (pStr.contains("万")) {
                    pStr = pStr.replace("万", "");
                    bfbVo.setYysjjfj(Double.valueOf(pStr) * 10000);
                } else {
                    bfbVo.setYysjjfj(pStr.equals("--") ? 0 : Double.valueOf(pStr));
                }
            }
            {
                String pStr = jsonObject.getString("qjfy");
                if (pStr.contains("亿")) {
                    pStr = pStr.replace("亿", "");
                    bfbVo.setQjfy(Double.valueOf(pStr) * 100000000);
                } else if (pStr.contains("万")) {
                    pStr = pStr.replace("万", "");
                    bfbVo.setQjfy(Double.valueOf(pStr) * 10000);
                } else {
                    bfbVo.setQjfy(pStr.equals("--") ? 0 : Double.valueOf(pStr));
                }
            }
            {
                String pStr = jsonObject.getString("xsfy");
                if (pStr.contains("亿")) {
                    pStr = pStr.replace("亿", "");
                    bfbVo.setXsfy(Double.valueOf(pStr) * 100000000);
                } else if (pStr.contains("万")) {
                    pStr = pStr.replace("万", "");
                    bfbVo.setXsfy(Double.valueOf(pStr) * 10000);
                } else {
                    bfbVo.setXsfy(pStr.equals("--") ? 0 : Double.valueOf(pStr));
                }
            }
            {
                String pStr = jsonObject.getString("glfy");
                if (pStr.contains("亿")) {
                    pStr = pStr.replace("亿", "");
                    bfbVo.setGlfy(Double.valueOf(pStr) * 100000000);
                } else if (pStr.contains("万")) {
                    pStr = pStr.replace("万", "");
                    bfbVo.setGlfy(Double.valueOf(pStr) * 10000);
                } else {
                    bfbVo.setGlfy(pStr.equals("--") ? 0 : Double.valueOf(pStr));
                }
            }
            {
                String pStr = jsonObject.getString("cwfy");
                if (pStr.contains("亿")) {
                    pStr = pStr.replace("亿", "");
                    bfbVo.setCwfy(Double.valueOf(pStr) * 100000000);
                } else if (pStr.contains("万")) {
                    pStr = pStr.replace("万", "");
                    bfbVo.setCwfy(Double.valueOf(pStr) * 10000);
                } else {
                    bfbVo.setCwfy(pStr.equals("--") ? 0 : Double.valueOf(pStr));
                }
            }
            {
                String pStr = jsonObject.getString("zcjzss");
                if (pStr.contains("亿")) {
                    pStr = pStr.replace("亿", "");
                    bfbVo.setZcjzss(Double.valueOf(pStr) * 100000000);
                } else if (pStr.contains("万")) {
                    pStr = pStr.replace("万", "");
                    bfbVo.setZcjzss(Double.valueOf(pStr) * 10000);
                } else {
                    bfbVo.setZcjzss(pStr.equals("--") ? 0 : Double.valueOf(pStr));
                }
            }
            {
                String pStr = jsonObject.getString("qtjysy");
                if (pStr.contains("亿")) {
                    pStr = pStr.replace("亿", "");
                    bfbVo.setQtjysy(Double.valueOf(pStr) * 100000000);
                } else if (pStr.contains("万")) {
                    pStr = pStr.replace("万", "");
                    bfbVo.setQtjysy(Double.valueOf(pStr) * 10000);
                } else {
                    bfbVo.setQtjysy(pStr.equals("--") ? 0 : Double.valueOf(pStr));
                }
            }
            {
                String pStr = jsonObject.getString("gyjzbdsy");
                if (pStr.contains("亿")) {
                    pStr = pStr.replace("亿", "");
                    bfbVo.setGyjzbdsy(Double.valueOf(pStr) * 100000000);
                } else if (pStr.contains("万")) {
                    pStr = pStr.replace("万", "");
                    bfbVo.setGyjzbdsy(Double.valueOf(pStr) * 10000);
                } else {
                    bfbVo.setGyjzbdsy(pStr.equals("--") ? 0 : Double.valueOf(pStr));
                }
            }
            {
                String pStr = jsonObject.getString("tzsy");
                if (pStr.contains("亿")) {
                    pStr = pStr.replace("亿", "");
                    bfbVo.setTzsy(Double.valueOf(pStr) * 100000000);
                } else if (pStr.contains("万")) {
                    pStr = pStr.replace("万", "");
                    bfbVo.setTzsy(Double.valueOf(pStr) * 10000);
                } else {
                    bfbVo.setTzsy(pStr.equals("--") ? 0 : Double.valueOf(pStr));
                }
            }
            {
                String pStr = jsonObject.getString("yylr");
                if (pStr.contains("亿")) {
                    pStr = pStr.replace("亿", "");
                    bfbVo.setYylr(Double.valueOf(pStr) * 100000000);
                } else if (pStr.contains("万")) {
                    pStr = pStr.replace("万", "");
                    bfbVo.setYylr(Double.valueOf(pStr) * 10000);
                } else {
                    bfbVo.setYylr(pStr.equals("--") ? 0 : Double.valueOf(pStr));
                }
            }
            {
                String pStr = jsonObject.getString("yywsr");
                if (pStr.contains("亿")) {
                    pStr = pStr.replace("亿", "");
                    bfbVo.setYywsr(Double.valueOf(pStr) * 100000000);
                } else if (pStr.contains("万")) {
                    pStr = pStr.replace("万", "");
                    bfbVo.setYywsr(Double.valueOf(pStr) * 10000);
                } else {
                    bfbVo.setYywsr(pStr.equals("--") ? 0 : Double.valueOf(pStr));
                }
            }
            {
                String pStr = jsonObject.getString("btsr");
                if (pStr.contains("亿")) {
                    pStr = pStr.replace("亿", "");
                    bfbVo.setBtsr(Double.valueOf(pStr) * 100000000);
                } else if (pStr.contains("万")) {
                    pStr = pStr.replace("万", "");
                    bfbVo.setBtsr(Double.valueOf(pStr) * 10000);
                } else {
                    bfbVo.setBtsr(pStr.equals("--") ? 0 : Double.valueOf(pStr));
                }
            }
            {
                String pStr = jsonObject.getString("yywzc");
                if (pStr.contains("亿")) {
                    pStr = pStr.replace("亿", "");
                    bfbVo.setYywzc(Double.valueOf(pStr) * 100000000);
                } else if (pStr.contains("万")) {
                    pStr = pStr.replace("万", "");
                    bfbVo.setYywzc(Double.valueOf(pStr) * 10000);
                } else {
                    bfbVo.setYywzc(pStr.equals("--") ? 0 : Double.valueOf(pStr));
                }
            }
            {
                String pStr = jsonObject.getString("lrze");
                if (pStr.contains("亿")) {
                    pStr = pStr.replace("亿", "");
                    bfbVo.setLrze(Double.valueOf(pStr) * 100000000);
                } else if (pStr.contains("万")) {
                    pStr = pStr.replace("万", "");
                    bfbVo.setLrze(Double.valueOf(pStr) * 10000);
                } else {
                    bfbVo.setLrze(pStr.equals("--") ? 0 : Double.valueOf(pStr));
                }
            }
            {
                String pStr = jsonObject.getString("sds");
                if (pStr.contains("亿")) {
                    pStr = pStr.replace("亿", "");
                    bfbVo.setSds(Double.valueOf(pStr) * 100000000);
                } else if (pStr.contains("万")) {
                    pStr = pStr.replace("万", "");
                    bfbVo.setSds(Double.valueOf(pStr) * 10000);
                } else {
                    bfbVo.setSds(pStr.equals("--") ? 0 : Double.valueOf(pStr));
                }
            }
            {
                String pStr = jsonObject.getString("jlr");
                if (pStr.contains("亿")) {
                    pStr = pStr.replace("亿", "");
                    bfbVo.setJlr(Double.valueOf(pStr) * 100000000);
                } else if (pStr.contains("万")) {
                    pStr = pStr.replace("万", "");
                    bfbVo.setJlr(Double.valueOf(pStr) * 10000);
                } else {
                    bfbVo.setJlr(pStr.equals("--") ? 0 : Double.valueOf(pStr));
                }
            }


            bfbVoList.add(bfbVo);

        }

        return bfbVoList;
    }
}
