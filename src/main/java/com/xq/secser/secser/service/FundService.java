package com.xq.secser.secser.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xq.secser.secser.model.FundTypeEnum;
import com.xq.secser.secser.model.PageInfo;
import com.xq.secser.secser.pojo.po.FoundPo;
import com.xq.secser.secser.pojo.po.FundDownPo;
import com.xq.secser.secser.pojo.po.IFund;
import com.xq.secser.secser.pojo.po.IFundDown;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author sk-qianxiao
 * @date 2019/12/30
 */
@Service
public class FundService {
    private static Logger logger = LoggerFactory.getLogger(FundService.class);
    private static RestTemplate restTemplate = new RestTemplate();
    @Autowired
    SqlSessionFactory sqlSessionFactory;


    private String getUrl(FundTypeEnum fundTypeEnum, long pageNum, long pageIndex) {
        String urlPattern = "http://fund.eastmoney.com/data/rankhandler.aspx?op=ph&dt=kf&ft=%s&rs=&gs=0&sc=zzf&st=desc&sd=%s&ed=%s&qdii=&tabSubtype=,,,,,&pi=%d&pn=%d&dx=1&v=0.5797826098327001";

        LocalDate localDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String date = localDate.format(formatter);
        String url = String.format(urlPattern, fundTypeEnum.getUrlParam(), date, date, pageIndex, pageNum);
        return url;
    }

    private String rmUnuseData(String data) {
        String outData = data.replace("var rankData =", "");
        outData = outData.replace(";", "");
        return outData;
    }

    private String funderRequest(FundTypeEnum ft, long pageNum, long pageIndex) {
        String url = getUrl(ft, pageNum, pageIndex);
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
        String data = rmUnuseData(responseEntity.getBody());
        return data;
    }

    private PageInfo parseReqData(String data, List<String> allData) {
        PageInfo pageInfo = PageInfo.builder().build();
        JSONObject configJson = JSON.parseObject(data);
        pageInfo.setPageNum(configJson.getLong("pageNum"));
        pageInfo.setPageIndex(configJson.getLong("pageIndex"));
        pageInfo.setAllPages(configJson.getLong("allPages"));
        pageInfo.setAllRecords(configJson.getLong("allRecords"));
        pageInfo.setAllNum(configJson.getLong("allNum"));
        pageInfo.setGpNum(configJson.getLong("gpNum"));
        pageInfo.setHhNum(configJson.getLong("hhNum"));
        pageInfo.setZqNum(configJson.getLong("zqNum"));
        pageInfo.setZsNum(configJson.getLong("zsNum"));
        pageInfo.setBbNum(configJson.getLong("bbNum"));
        pageInfo.setQdiiNum(configJson.getLong("qdiiNum"));
        pageInfo.setEtfNum(configJson.getLong("etfNum"));
        pageInfo.setLofNum(configJson.getLong("lofNum"));
        pageInfo.setFofNum(configJson.getLong("fofNum"));

        JSONArray jsonArray = configJson.getJSONArray("datas");
        jsonArray.forEach(item -> allData.add(item.toString()));

        return pageInfo;
    }

    public List<FoundPo> parseFund() {
        List<FoundPo> foundPoList = new ArrayList<>();

        try (SqlSession session = sqlSessionFactory.openSession(true)){
            LocalDate localDate = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            IFundDown iFundDown = session.getMapper(IFundDown.class);
            List<FundDownPo> fundDownPoList = iFundDown.getAll();
            fundDownPoList.forEach(data -> {
                String[] items = data.getInfo().split(",");
                String code = items[0];
                String name = items[1];
                String date = items[3];
                if (date.length() == 0) {
                    date = localDate.format(formatter);
                }
                Double l1y = items[11].length() == 0 ? null : Double.valueOf(items[11]);
                Double l2y = items[12].length() == 0 ? null : Double.valueOf(items[12]);
                Double l3y = items[13].length() == 0 ? null : Double.valueOf(items[13]);
                Double ty = items[14].length() == 0 ? null : Double.valueOf(items[14]);
                Double cy = items[15].length() == 0 ? null : Double.valueOf(items[15]);
                FoundPo foundPo = FoundPo.builder().code(code).name(name).ft(data.getFt()).date(date).l1y(l1y).l2y(l2y).l3y(l3y).ty(ty).cy(cy).build();
                foundPoList.add(foundPo);
            });
        } finally {
        }

        return foundPoList;
    }

    public List<String> getAllFund(FundTypeEnum ft) {
        List<String> allData = new ArrayList<>();

        String data = funderRequest(ft, 500, 1);
        PageInfo pageInfo = parseReqData(data, allData);
        for (long i = pageInfo.getPageIndex() + 1; i <= pageInfo.getAllPages(); ++i) {
            String data2 = funderRequest(ft, pageInfo.getPageNum(), i);
            parseReqData(data2, allData);
        }

        return allData;
    }
}
