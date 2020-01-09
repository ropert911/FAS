package com.xq.secser.provider.tt.provider;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xq.secser.constant.FundTypeEnum;
import com.xq.secser.constant.PageInfo;
import com.xq.secser.constant.ZQSubTypeEnum;
import com.xq.secser.ssbser.pojo.po.FoundPo;
import com.xq.secser.provider.FundProvider;
import com.xq.secser.provider.tt.pojo.ITtFund;
import com.xq.secser.provider.tt.pojo.TtFundPo;
import com.xq.secser.ssbser.pojo.vo.RedeemRate;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
        logger.debug("rmUnuseData data=={}", data);
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

                Double l1m = items[8].length() == 0 ? null : Double.valueOf(items[8]);
                Double l3m = items[9].length() == 0 ? null : Double.valueOf(items[9]);
                Double l6m = items[10].length() == 0 ? null : Double.valueOf(items[10]);
                Double l1y = items[11].length() == 0 ? null : Double.valueOf(items[11]);
                Double l2y = items[12].length() == 0 ? null : Double.valueOf(items[12]);
                Double l3y = items[13].length() == 0 ? null : Double.valueOf(items[13]);
                Double ty = items[14].length() == 0 ? null : Double.valueOf(items[14]);
                Double cy = items[15].length() == 0 ? null : Double.valueOf(items[15]);
                FoundPo foundPo = FoundPo.builder().code(code).name(name).ft(data.getFt()).l1m(l1m).l3m(l3m).l6m(l6m).l1y(l1y).l2y(l2y).l3y(l3y).ty(ty).cy(cy).build();
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

    @Override
    public RedeemRate getflinfo(String fundCode) {
        String patten = "http://fundf10.eastmoney.com/jjfl_%s.html";
        String url = String.format(patten, fundCode);
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
        String data = responseEntity.getBody();


        Double purchfl = null, managefl = null, tgfl = null;
        int index = 0, index2 = 0;
        String sub;
        {
            index = data.indexOf("申购费率（前端）");
            index = data.indexOf("</strike>", index);
            if (-1 != index) {
                index2 = data.indexOf("</td>", index);
                sub = data.substring(index + 9, index2);
                sub = sub.replaceAll("&nbsp;", "");
                sub = sub.substring(1);
                List<String> mutir = getmutiString("([0-9,\\.]+)%\\|([0-9,\\.]+)%", sub);
                purchfl = Double.valueOf(mutir.get(1));
            }
        }

        {
            index = data.indexOf("管理费率");
            sub = data.substring(index, index + 300);
            index = sub.indexOf("\">");
            sub = sub.substring(index + 2, index + 10);
            String r = getString("([0-9,\\.]+)%\\（每年", sub);
            managefl = Double.valueOf(r);
        }


        {
            index = data.indexOf("托管费率");
            sub = data.substring(index, index + 300);
            index = sub.indexOf("\">");
            sub = sub.substring(index + 2, index + 20);
            String r = getString("([0-9,\\.]+)%\\（每年", sub);
            tgfl = Double.valueOf(r);
        }

        index = data.indexOf("赎回费率");
        index = data.indexOf("赎回费率", index + 1);
        sub = data.substring(index, index + 500);
        index = sub.indexOf("<tr>");
        index2 = sub.indexOf("</table>");
        sub = sub.substring(index, index2);

        RedeemRate redeemRate = RedeemRate.builder().build();
        boolean bend = false;
        int shflindex = 1;
        while (true) {
            index = sub.indexOf("<tr>");
            index2 = sub.indexOf("<tr>", index + 3);
            String row = "";
            if (-1 == index2) {
                index2 = sub.indexOf("</tr>", index + 3);
                bend = true;
            }

            row = sub.substring(index, index2);
            sub = sub.substring(index2);


            List<String> mutir = getmutiString("<td>(.+)</td><td>([0-9,\\.]+)%", row);
            String item = mutir.get(0);
            Double value = Double.valueOf(mutir.get(1));
            switch (shflindex) {
                case 1:
                    redeemRate.setC1(item);
                    redeemRate.setFl(value);
                    break;
                case 2:
                    redeemRate.setC2(item);
                    redeemRate.setF2(value);
                    break;
                case 3:
                    redeemRate.setC3(item);
                    redeemRate.setF3(value);
                    break;
                case 4:
                    redeemRate.setC4(item);
                    redeemRate.setF4(value);
                    break;
                default:
                    break;
            }

            if (bend) {
                break;
            }
            shflindex++;
        }

        redeemRate.setSgfl(purchfl);
        redeemRate.setYzfl(managefl + tgfl);
        redeemRate.setManagefl(managefl);
        redeemRate.setTgfl(tgfl);

        return redeemRate;
    }

    public static String getString(String pstr, String text) {
        final Pattern pattern = Pattern.compile(pstr);
        Matcher m = pattern.matcher(text);
        String str = "";
        if (m.find()) {
            str = m.group(1);
        }
        return str;
    }

    public static List<String> getmutiString(String pstr, String text) {
        final Pattern pattern = Pattern.compile(pstr);
        Matcher m = pattern.matcher(text);
        List<String> r = new ArrayList<>();
        if (m.find()) {
            int n = m.groupCount();
            String str = "";
            for (int i = 1; i <= n; ++i) {
                str = m.group(i);
                r.add(str);
            }
        }
        return r;
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
