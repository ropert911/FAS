package com.xq.secser.ssbser.tt.provider;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xq.secser.ssbser.model.FundTypeEnum;
import com.xq.secser.ssbser.model.PageInfo;
import com.xq.secser.ssbser.pojo.po.FoundPo;
import com.xq.secser.ssbser.service.FundProvider;
import com.xq.secser.ssbser.tt.pojo.ITtFund;
import com.xq.secser.ssbser.tt.pojo.TtFundPo;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author sk-qianxiao
 * @date 2019/12/30
 */
@Service
public class TTFundProvider implements FundProvider {
    private static Logger logger = LoggerFactory.getLogger(TTFundProvider.class);
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

    @Override
    public List<FoundPo> parseFund() {
        List<FoundPo> foundPoList = new ArrayList<>();

        try (SqlSession session = sqlSessionFactory.openSession(true)) {
            java.util.Date now = new Date();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

            ITtFund iTtFund = session.getMapper(ITtFund.class);
            List<TtFundPo> ttFundPoList = iTtFund.getAll();
            ttFundPoList.forEach(data -> {
                String[] items = data.getInfo().split(",");
                String code = items[0];
                String name = items[1];
                String date = items[3];

                Double l1y = items[11].length() == 0 ? null : Double.valueOf(items[11]);
                Double l2y = items[12].length() == 0 ? null : Double.valueOf(items[12]);
                Double l3y = items[13].length() == 0 ? null : Double.valueOf(items[13]);
                Double ty = items[14].length() == 0 ? null : Double.valueOf(items[14]);
                Double cy = items[15].length() == 0 ? null : Double.valueOf(items[15]);
                FoundPo foundPo = FoundPo.builder().code(code).name(name).ft(data.getFt()).l1y(l1y).l2y(l2y).l3y(l3y).ty(ty).cy(cy).build();
                try {
                    java.util.Date d = now;
                    if (date.length() > 0) {
                        d = format.parse(date);
                    }

                    foundPo.setDate(new java.sql.Date(d.getTime()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                foundPoList.add(foundPo);
            });
        } finally {
        }

        return foundPoList;
    }

    private List<String> getAllFund(FundTypeEnum ft) {
        List<String> allData = new ArrayList<>();

        String data = funderRequest(ft, 500, 1);
        PageInfo pageInfo = parseReqData(data, allData);
        for (long i = pageInfo.getPageIndex() + 1; i <= pageInfo.getAllPages(); ++i) {
            String data2 = funderRequest(ft, pageInfo.getPageNum(), i);
            parseReqData(data2, allData);
        }

        return allData;
    }

    @Override
    public void initFoundData() {
        for (FundTypeEnum fte : FundTypeEnum.values()) {
            List<String> allData1 = getAllFund(fte);
            batchInsertDb(fte, allData1);
        }
    }

    private void batchInsertDb(FundTypeEnum fundTypeEnum, List<String> allData) {
        List<TtFundPo> ttFundPoList = new ArrayList<>();
        allData.forEach(data -> {
            String[] items = data.split(",", 2);
            TtFundPo ttFundPo = TtFundPo.builder().code(items[0]).ft(fundTypeEnum.getUrlParam()).info(data).build();
            ttFundPoList.add(ttFundPo);
        });

        SqlSession session = sqlSessionFactory.openSession(true);
        try {
            ITtFund iTtFund = session.getMapper(ITtFund.class);
            iTtFund.insertFundBatch(ttFundPoList);
        } finally {
            session.close();
        }
    }
}
