package com.xq.secser.provider.tt.provider;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xq.secser.ssbser.model.FundTypeEnum;
import com.xq.secser.ssbser.model.PageInfo;
import com.xq.secser.ssbser.model.ZQSubTypeEnum;
import com.xq.secser.ssbser.pojo.po.FoundPo;
import com.xq.secser.provider.FundProvider;
import com.xq.secser.provider.tt.pojo.ITtFund;
import com.xq.secser.provider.tt.pojo.TtFundPo;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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


    private String getUrl(FundTypeEnum fundTypeEnum, String subType, long pageNum, long pageIndex) {
        String urlPattern = "http://fund.eastmoney.com/data/rankhandler.aspx?op=ph&dt=kf&ft=%s&rs=&gs=0&sc=zzf&st=desc&sd=%s&ed=%s&qdii=%s|&tabSubtype=%s,,,,,&pi=%d&pn=%d&dx=1&v=0.5797826098327001";
        if (fundTypeEnum == FundTypeEnum.QDII) {
            urlPattern = "http://fund.eastmoney.com/data/rankhandler.aspx?op=ph&dt=kf&ft=%s&rs=&gs=0&sc=zzf&st=desc&sd=%s&ed=%s&qdii=%s&tabSubtype=%s,,,,,&pi=%d&pn=%d&dx=1&v=0.5797826098327001";
        }

        LocalDate localDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String date = localDate.format(formatter);
        String url = String.format(urlPattern, fundTypeEnum.getUrlParam(), date, date, subType, subType, pageIndex, pageNum);
        return url;
    }

    private String rmUnuseData(String data) {
        logger.info("rmUnuseData data=={}", data);
        String outData = data.replace("var rankData =", "");
        outData = outData.replace(";", "");
        return outData;
    }

    private String funderRequest(FundTypeEnum ft, String subType, long pageNum, long pageIndex) {
        String url = getUrl(ft, subType, pageNum, pageIndex);
        logger.info("funderRequest ft={} subType={} url={}", ft, subType, url);
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
                foundPo.setSubt(data.getSubt());

                foundPoList.add(foundPo);
            });
        } finally {
        }


        //包含基金的公司和基金的评级
        List<FoundPo> cCodeLevel = parseFundLevelData();
        Map<String, FoundPo> levMap = cCodeLevel.stream().collect(Collectors.toMap(FoundPo::getCode, a -> a, (key1, key2) -> key2));
        foundPoList.forEach(item -> {
            FoundPo t = levMap.get(item.getCode());
            if (t != null) {
                item.setComcode(t.getComcode());
                item.setLevel(t.getLevel());
            }
        });


        return foundPoList;
    }


    private List<FoundPo> parseFundLevelData() {
        List<FoundPo> foundLevelList = new ArrayList<>();
        try {
            Resource res2 = new ClassPathResource("评级.xlsx");
            XSSFWorkbook workbook = new XSSFWorkbook(res2.getInputStream());
            XSSFSheet dataSheet = workbook.getSheet("data");
            XSSFRow dataheader = dataSheet == null ? null : dataSheet.getRow(0);
            int itfCellNum = dataheader == null ? 0 : dataheader.getPhysicalNumberOfCells();
            int fcodei = 0;
            int ccodei = 0;
            int flevelsh = -1, flevelzs = -1, flevelja = -1;
            for (int i = 0; i < itfCellNum; i++) {
                String content = getCellString(dataheader, i);
                switch (content) {
                    case "代码":
                        fcodei = i;
                        break;
                    case "简称":
                        break;
                    case "相关链接":
                        break;
                    case "基金经理":
                        break;
                    case "基金公司":
                        ccodei = i;
                        break;
                    case "5星评级家数":
                        break;
                    case "上海证券":
                        flevelsh = i;
                        break;
                    case "招商证券":
                        flevelzs = i;
                        break;
                    case "济安金信":
                        flevelja = i;
                        break;
                    default:
                        break;
                }
            }

            int itfRowsNum = dataSheet.getPhysicalNumberOfRows();
            for (int i = 1; i < itfRowsNum; i++) {
                FoundPo foundLevel = FoundPo.builder().build();
                //从第二列获取
                XSSFRow xssfRow = dataSheet.getRow(i);
                String code = getCellString(xssfRow, fcodei);
                while (code.length() < 6) {
                    code = "0" + code;
                }
                foundLevel.setCode(code);

                String ccode = getLinkString(xssfRow, ccodei);
                ccode = ccode.replaceAll("[^\\d]+", "");
                foundLevel.setComcode(ccode);

                String lsh = getCellString(xssfRow, flevelsh);
                lsh = lsh.replaceAll("[^★]+", "");

                String lzs = getCellString(xssfRow, flevelzs);
                lzs = lzs.replaceAll("[^★]+", "");

                String lja = getCellString(xssfRow, flevelja);
                lja = lja.replaceAll("[^★]+", "");
                double level = getLevel(lsh.length(), lzs.length(), lja.length());
                foundLevel.setLevel(level);
                foundLevelList.add(foundLevel);
                logger.debug("{}-{}-{}-{}-{}==={}", code, ccode, lsh, lzs, lja, level);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return foundLevelList;
    }

    private List<String> getAllFund(FundTypeEnum ft, String subType) {
        List<String> allData = new ArrayList<>();

        String data = funderRequest(ft, subType, 500, 1);
        PageInfo pageInfo = parseReqData(data, allData);
        for (long i = pageInfo.getPageIndex() + 1; i <= pageInfo.getAllPages(); ++i) {
            String data2 = funderRequest(ft, subType, pageInfo.getPageNum(), i);
            parseReqData(data2, allData);
        }

        return allData;
    }

    @Override
    public void initFoundData() {
        for (FundTypeEnum fte : FundTypeEnum.values()) {
            if (FundTypeEnum.ZQ == fte) {
                for (ZQSubTypeEnum st : ZQSubTypeEnum.values()) {
                    List<String> allData1 = getAllFund(fte, st.getCode());
                    batchInsertDb(fte, st.getSubt(), allData1);
                }
            } else {
                List<String> allData1 = getAllFund(fte, "");
                batchInsertDb(fte, "", allData1);
            }
        }

        //评组表格从 http://fund.eastmoney.com/data/fundrating.html 基金评级得到
    }

    private void batchInsertDb(FundTypeEnum fundTypeEnum, String subType, List<String> allData) {
        List<TtFundPo> ttFundPoList = new ArrayList<>();
        allData.forEach(data -> {
            String[] items = data.split(",", 2);
            TtFundPo ttFundPo = TtFundPo.builder().code(items[0]).ft(fundTypeEnum.getUrlParam()).info(data).subt(subType).build();
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

    private String getCellString(XSSFRow xssfRow, int index) {
        XSSFCell cell = xssfRow.getCell(index);
        if (cell == null) {
            return "";
        }
        cell.setCellType(CellType.STRING);
        return cell.toString();
    }

    private String getLinkString(XSSFRow xssfRow, int index) {
        XSSFCell cell = xssfRow.getCell(index);
        if (cell == null) {
            return "";
        }
        XSSFHyperlink link = cell.getHyperlink();
        if (link == null) {
            return "";
        }
        return link.getAddress();
    }

    private double getLevel(double i, double j, double k) {
        double sum = 0;
        int num = 0;
        if (i > 0) {
            sum += i;
            num++;
        }
        if (j > 0) {
            sum += j;
            num++;
        }
        if (k > 0) {
            sum += k;
            num++;
        }

        return num > 0 ? (sum / num) : 0d;
    }
}
