package com.xq.fin.analyser.data;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xq.fin.analyser.pojo.ZyzbVo;
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
public class ZyzbUtil {
    private static Logger logger = LoggerFactory.getLogger(ZyzbUtil.class);

    @Autowired
    private GloableData gloableData;
    @Autowired
    private RestTemplate restTemplate;

    public void getData(String code) {
        List<ZyzbVo> zyzbVoList = new ArrayList<>(10);
        String url = "http://f10.eastmoney.com/NewFinanceAnalysis/MainTargetAjax?type=0&code=" + StringUtil.getSCByCode(code);
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
        String data = responseEntity.getBody();
//        String data = "[{\"date\":\"2020-06-30\",\"jbmgsy\":\"1.8800\",\"kfmgsy\":\"1.8800\",\"xsmgsy\":\"1.8800\",\"mgjzc\":\"19.7459\",\"mggjj\":\"14.3293\",\"mgwfply\":\"3.7688\",\"mgjyxjl\":\"-2.9943\",\"yyzsr\":\"11.5亿\",\"mlr\":\"1.82亿\",\"gsjlr\":\"1.03亿\",\"kfjlr\":\"1.03亿\",\"yyzsrtbzz\":\"61.23\",\"gsjlrtbzz\":\"127.76\",\"kfjlrtbzz\":\"155.72\",\"yyzsrgdhbzz\":\"18.04\",\"gsjlrgdhbzz\":\"14.85\",\"kfjlrgdhbzz\":\"18.02\",\"jqjzcsyl\":\"17.34\",\"tbjzcsyl\":\"7.53\",\"tbzzcsyl\":\"6.68\",\"mll\":\"16.12\",\"jll\":\"8.94\",\"sjsl\":\"14.19\",\"yskyysr\":\"--\",\"xsxjlyysr\":\"0.56\",\"jyxjlyysr\":\"-0.18\",\"zzczzy\":\"0.75\",\"yszkzzts\":\"111.44\",\"chzzts\":\"1.46\",\"zcfzl\":\"38.73\",\"ldzczfz\":\"98.84\",\"ldbl\":\"2.57\",\"sdbl\":\"2.56\"},{\"date\":\"2020-03-31\",\"jbmgsy\":\"0.7800\",\"kfmgsy\":\"0.7800\",\"xsmgsy\":\"--\",\"mgjzc\":\"8.0500\",\"mggjj\":\"0.3605\",\"mgwfply\":\"5.8224\",\"mgjyxjl\":\"-5.0109\",\"yyzsr\":\"4.45亿\",\"mlr\":\"7799万\",\"gsjlr\":\"4042万\",\"kfjlr\":\"4032万\",\"yyzsrtbzz\":\"37.16\",\"gsjlrtbzz\":\"164.54\",\"kfjlrtbzz\":\"--\",\"yyzsrgdhbzz\":\"7.39\",\"gsjlrgdhbzz\":\"12.99\",\"kfjlrgdhbzz\":\"13.82\",\"jqjzcsyl\":\"10.19\",\"tbjzcsyl\":\"9.70\",\"tbzzcsyl\":\"4.77\",\"mll\":\"17.80\",\"jll\":\"9.09\",\"sjsl\":\"15.11\",\"yskyysr\":\"0.04\",\"xsxjlyysr\":\"0.48\",\"jyxjlyysr\":\"-0.58\",\"zzczzy\":\"0.52\",\"yszkzzts\":\"112.73\",\"chzzts\":\"2.19\",\"zcfzl\":\"50.79\",\"ldzczfz\":\"97.68\",\"ldbl\":\"1.99\",\"sdbl\":\"1.97\"},{\"date\":\"2019-12-31\",\"jbmgsy\":\"3.7400\",\"kfmgsy\":\"3.5200\",\"xsmgsy\":\"3.7400\",\"mgjzc\":\"7.2700\",\"mggjj\":\"0.3605\",\"mgwfply\":\"5.0420\",\"mgjyxjl\":\"2.7124\",\"yyzsr\":\"16.3亿\",\"mlr\":\"3.59亿\",\"gsjlr\":\"1.94亿\",\"kfjlr\":\"1.82亿\",\"yyzsrtbzz\":\"48.38\",\"gsjlrtbzz\":\"56.11\",\"kfjlrtbzz\":\"50.98\",\"yyzsrgdhbzz\":\"--\",\"gsjlrgdhbzz\":\"56.11\",\"kfjlrgdhbzz\":\"--\",\"jqjzcsyl\":\"60.80\",\"tbjzcsyl\":\"51.44\",\"tbzzcsyl\":\"25.73\",\"mll\":\"22.36\",\"jll\":\"11.87\",\"sjsl\":\"15.22\",\"yskyysr\":\"0.02\",\"xsxjlyysr\":\"0.99\",\"jyxjlyysr\":\"0.09\",\"zzczzy\":\"2.17\",\"yszkzzts\":\"86.78\",\"chzzts\":\"1.66\",\"zcfzl\":\"55.69\",\"ldzczfz\":\"97.04\",\"ldbl\":\"1.82\",\"sdbl\":\"1.80\"},{\"date\":\"2019-06-30\",\"jbmgsy\":\"--\",\"kfmgsy\":\"0.7800\",\"xsmgsy\":\"--\",\"mgjzc\":\"4.4000\",\"mggjj\":\"0.3605\",\"mgwfply\":\"2.5400\",\"mgjyxjl\":\"-2.3797\",\"yyzsr\":\"7.13亿\",\"mlr\":\"1.17亿\",\"gsjlr\":\"4510万\",\"kfjlr\":\"4015万\",\"yyzsrtbzz\":\"--\",\"gsjlrtbzz\":\"--\",\"kfjlrtbzz\":\"--\",\"yyzsrgdhbzz\":\"--\",\"gsjlrgdhbzz\":\"0.00\",\"kfjlrgdhbzz\":\"--\",\"jqjzcsyl\":\"15.94\",\"tbjzcsyl\":\"19.79\",\"tbzzcsyl\":\"6.13\",\"mll\":\"16.80\",\"jll\":\"6.33\",\"sjsl\":\"16.00\",\"yskyysr\":\"0.01\",\"xsxjlyysr\":\"0.65\",\"jyxjlyysr\":\"-0.17\",\"zzczzy\":\"0.97\",\"yszkzzts\":\"119.64\",\"chzzts\":\"1.37\",\"zcfzl\":\"72.04\",\"ldzczfz\":\"97.62\",\"ldbl\":\"1.40\",\"sdbl\":\"1.38\"},{\"date\":\"2019-03-31\",\"jbmgsy\":\"0.2900\",\"kfmgsy\":\"0.2900\",\"xsmgsy\":\"--\",\"mgjzc\":\"--\",\"mggjj\":\"--\",\"mgwfply\":\"--\",\"mgjyxjl\":\"-3.6900\",\"yyzsr\":\"3.24亿\",\"mlr\":\"5231万\",\"gsjlr\":\"1528万\",\"kfjlr\":\"1515万\",\"yyzsrtbzz\":\"--\",\"gsjlrtbzz\":\"--\",\"kfjlrtbzz\":\"--\",\"yyzsrgdhbzz\":\"--\",\"gsjlrgdhbzz\":\"0.00\",\"kfjlrgdhbzz\":\"--\",\"jqjzcsyl\":\"5.70\",\"tbjzcsyl\":\"--\",\"tbzzcsyl\":\"4.66\",\"mll\":\"16.58\",\"jll\":\"4.71\",\"sjsl\":\"16.30\",\"yskyysr\":\"--\",\"xsxjlyysr\":\"0.56\",\"jyxjlyysr\":\"-0.59\",\"zzczzy\":\"0.99\",\"yszkzzts\":\"48.47\",\"chzzts\":\"0.31\",\"zcfzl\":\"--\",\"ldzczfz\":\"--\",\"ldbl\":\"--\",\"sdbl\":\"--\"},{\"date\":\"2018-12-31\",\"jbmgsy\":\"2.3900\",\"kfmgsy\":\"2.3300\",\"xsmgsy\":\"2.3900\",\"mgjzc\":\"5.0300\",\"mggjj\":\"0.3605\",\"mgwfply\":\"3.1693\",\"mgjyxjl\":\"1.5048\",\"yyzsr\":\"11.0亿\",\"mlr\":\"2.71亿\",\"gsjlr\":\"1.24亿\",\"kfjlr\":\"1.21亿\",\"yyzsrtbzz\":\"44.97\",\"gsjlrtbzz\":\"26.87\",\"kfjlrtbzz\":\"29.33\",\"yyzsrgdhbzz\":\"--\",\"gsjlrgdhbzz\":\"26.87\",\"kfjlrgdhbzz\":\"--\",\"jqjzcsyl\":\"56.37\",\"tbjzcsyl\":\"47.62\",\"tbzzcsyl\":\"21.06\",\"mll\":\"25.34\",\"jll\":\"11.28\",\"sjsl\":\"15.88\",\"yskyysr\":\"0.04\",\"xsxjlyysr\":\"0.93\",\"jyxjlyysr\":\"0.07\",\"zzczzy\":\"1.87\",\"yszkzzts\":\"101.28\",\"chzzts\":\"0.81\",\"zcfzl\":\"60.27\",\"ldzczfz\":\"98.99\",\"ldbl\":\"1.64\",\"sdbl\":\"1.64\"},{\"date\":\"2017-12-31\",\"jbmgsy\":\"1.8900\",\"kfmgsy\":\"1.8000\",\"xsmgsy\":\"1.8900\",\"mgjzc\":\"3.6300\",\"mggjj\":\"0.3605\",\"mgwfply\":\"2.0028\",\"mgjyxjl\":\"2.5423\",\"yyzsr\":\"7.58亿\",\"mlr\":\"1.96亿\",\"gsjlr\":\"9776万\",\"kfjlr\":\"9326万\",\"yyzsrtbzz\":\"105.61\",\"gsjlrtbzz\":\"34.75\",\"kfjlrtbzz\":\"32.88\",\"yyzsrgdhbzz\":\"--\",\"gsjlrgdhbzz\":\"34.75\",\"kfjlrgdhbzz\":\"--\",\"jqjzcsyl\":\"58.01\",\"tbjzcsyl\":\"51.93\",\"tbzzcsyl\":\"23.69\",\"mll\":\"27.24\",\"jll\":\"12.89\",\"sjsl\":\"17.24\",\"yskyysr\":\"0.04\",\"xsxjlyysr\":\"0.97\",\"jyxjlyysr\":\"0.17\",\"zzczzy\":\"1.84\",\"yszkzzts\":\"104.65\",\"chzzts\":\"1.72\",\"zcfzl\":\"63.94\",\"ldzczfz\":\"95.81\",\"ldbl\":\"1.62\",\"sdbl\":\"1.61\"},{\"date\":\"2016-12-31\",\"jbmgsy\":\"--\",\"kfmgsy\":\"1.3500\",\"xsmgsy\":\"--\",\"mgjzc\":\"2.7100\",\"mggjj\":\"0.3605\",\"mgwfply\":\"1.2304\",\"mgjyxjl\":\"1.2723\",\"yyzsr\":\"3.69亿\",\"mlr\":\"1.39亿\",\"gsjlr\":\"7255万\",\"kfjlr\":\"7018万\",\"yyzsrtbzz\":\"222.33\",\"gsjlrtbzz\":\"196.36\",\"kfjlrtbzz\":\"216.64\",\"yyzsrgdhbzz\":\"--\",\"gsjlrgdhbzz\":\"68.67\",\"kfjlrgdhbzz\":\"--\",\"jqjzcsyl\":\"64.48\",\"tbjzcsyl\":\"51.65\",\"tbzzcsyl\":\"31.94\",\"mll\":\"38.99\",\"jll\":\"19.67\",\"sjsl\":\"23.40\",\"yskyysr\":\"0.02\",\"xsxjlyysr\":\"0.89\",\"jyxjlyysr\":\"0.18\",\"zzczzy\":\"1.62\",\"yszkzzts\":\"104.58\",\"chzzts\":\"5.51\",\"zcfzl\":\"53.69\",\"ldzczfz\":\"97.54\",\"ldbl\":\"1.86\",\"sdbl\":\"1.84\"},{\"date\":\"2016-06-30\",\"jbmgsy\":\"0.4100\",\"kfmgsy\":\"0.3900\",\"xsmgsy\":\"0.4100\",\"mgjzc\":\"1.9300\",\"mggjj\":\"0.2792\",\"mgwfply\":\"0.6152\",\"mgjyxjl\":\"0.0465\",\"yyzsr\":\"1.38亿\",\"mlr\":\"4351万\",\"gsjlr\":\"2138万\",\"kfjlr\":\"2025万\",\"yyzsrtbzz\":\"219.28\",\"gsjlrtbzz\":\"652.22\",\"kfjlrtbzz\":\"--\",\"yyzsrgdhbzz\":\"--\",\"gsjlrgdhbzz\":\"75.71\",\"kfjlrgdhbzz\":\"--\",\"jqjzcsyl\":\"21.33\",\"tbjzcsyl\":\"21.33\",\"tbzzcsyl\":\"12.24\",\"mll\":\"32.49\",\"jll\":\"15.53\",\"sjsl\":\"24.05\",\"yskyysr\":\"0.11\",\"xsxjlyysr\":\"0.79\",\"jyxjlyysr\":\"0.02\",\"zzczzy\":\"0.79\",\"yszkzzts\":\"89.38\",\"chzzts\":\"6.72\",\"zcfzl\":\"49.42\",\"ldzczfz\":\"95.91\",\"ldbl\":\"1.89\",\"sdbl\":\"1.85\"}]";
//        logger.info("get data = {}", data);
        data = StringEscapeUtils.unescapeJava(data);
//        logger.info("get data unescapeJava = {}", data);
        if (data.startsWith("\"")) {
            data = data.substring(1, data.length() - 1);
//        logger.info("get data unescapeJava2 = {}", data);
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
                zyzbVo.setDate(String.format("%s%02d", date.get(0), Long.valueOf(date.get(1))));
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

        zyzbVoList = zyzbVoList.stream().sorted(Comparator.comparing(ZyzbVo::getDate).reversed()).collect(Collectors.toList());
        gloableData.allStockList.get(code).setZyzbVoList(zyzbVoList);
    }
}
