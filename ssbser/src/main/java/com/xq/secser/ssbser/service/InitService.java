package com.xq.secser.ssbser.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xq.secser.provider.CompanyProvider;
import com.xq.secser.provider.FundProvider;
import com.xq.secser.ssbser.pojo.po.*;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author sk-qianxiao
 * @date 2019/12/31
 */
@Service
public class InitService implements ApplicationRunner {
    private static Logger logger = LoggerFactory.getLogger(InitService.class);

    @Autowired
    FundProvider fundProvider;
    @Autowired
    CompanyProvider companyProvider;
    @Autowired
    SqlSessionFactory sqlSessionFactory;

    @Value("${com.xq.secser.download.fund}")
    private boolean bDowloadFound;

    @Value("${com.xq.secser.download.company}")
    private boolean bDowloadCompany;


    @Override
    public void run(ApplicationArguments args) {
        //TODO:添加时间统计
        //下载found数据
        if (bDowloadFound) {
            fundProvider.initFoundData();
        }

        //下载com数据
        if (bDowloadCompany) {
            companyProvider.initCompany();
        }

        //把foud原始数据解析到数据结构表里
//        parseFund();

        //解析公司数据
//        parseCompany();

        searchHighBenlif();
    }


    private void parseFund() {
        List<FoundPo> foundPoList = fundProvider.parseFund();

        try (SqlSession session = sqlSessionFactory.openSession(true)) {
            IFund iFund = session.getMapper(IFund.class);
            iFund.insertFundBatch(foundPoList);
        } finally {
        }
    }

    private void parseCompany() {
        List<CompPo> compPoList = companyProvider.parseCompany();

        try (SqlSession session = sqlSessionFactory.openSession(true)) {
            IComp iComp = session.getMapper(IComp.class);
            iComp.insertCompBatch(compPoList);
        } finally {
        }
    }

    private void searchHighBenlif() {
        try (SqlSession session = sqlSessionFactory.openSession(true)) {
            IFund iFund = session.getMapper(IFund.class);
            IComp iComp = session.getMapper(IComp.class);

            /**哪些类基金放在一起来找*/
            String[] ft = {"gp", "hh"};
            /**级别要大于这个级别*/
            double foudlevel = 3.5;
            /**近一年利率大于等待*/
            double l1y = 30.0;
            /**近三年利率大于等于*/
            double l3y = 50.0;

            /**季度历史排名平均在前30%*/
            double qhisrank = 0.3;


            /**公司模型排名*/
            int topN = 30;
            /**公司成立要在这个时间点前，这样公司至少穿越了牛熊*/
            String esTime = "2014-06-06";
            Map<String, CompPo> comMap = new HashMap<>();

            //公司筛选
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date dateEs = format.parse(esTime);
            for (String t : ft) {
                List<CompPo> compPoList = iComp.getTopN(t, topN);
                compPoList.stream().filter(item -> item.getEstime().compareTo(dateEs) < 0).forEach(item -> comMap.put(item.getComcode(), item));
            }


            //基金筛选:类型，级别，公司
            final List<FoundPo> foundPoResult = new ArrayList<>();
            for (String t : ft) {
                List<FoundPo> foundPoList = iFund.getByLevel(t, foudlevel);
                foundPoList.forEach(item -> {
                    if (null != comMap.get(item.getComcode())) {
                        foundPoResult.add(item);
                    }
                });
            }


            //基金筛选:利润过滤
            List<FoundPo> rFoundPoResult = foundPoResult.stream().filter(s -> s.getL1y() >= l1y).filter(s -> s.getL3y() >= l3y).collect(Collectors.toList());
            //基金筛选:去重
            rFoundPoResult = rFoundPoResult.stream().collect(
                    Collectors.collectingAndThen(
                            Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(FoundPo::getCode))), ArrayList::new)
            );

            /**对近2年季度排名进行处理*/
            List<String> ftcodelist = rFoundPoResult.stream().map(FoundPo::getCode).collect(Collectors.toList());
            {
                //判断哪些code没有季度数据：然后进行下载下载季度数据
                Set<String> existCodeSet = getExistCodes(ftcodelist).stream().collect(Collectors.toSet());
                List<String> notexistCodeList = ftcodelist.stream().filter(code -> !existCodeSet.contains(code)).collect(Collectors.toList());
                downLoadFoudQuartData(notexistCodeList);
            }
            {
                Set<String> qokcodelist = new HashSet<>();
                //获取季度数据
                List<FundQuarterPo> fqList = getQuarterDataByCode(ftcodelist);
                //找到平均季度排名在0.3以下的
                Map<String, List<FundQuarterPo>> fqListMap = fqList.stream().collect(Collectors.groupingBy(FundQuarterPo::getCode));
                fqListMap.forEach((key, value) -> {
                    if (!value.isEmpty()) {
                        Double rank = value.stream().filter(item -> item.getRank() != null).collect(Collectors.averagingDouble(FundQuarterPo::getRank));
                        if (rank <= qhisrank) {
                            qokcodelist.add(key);
                        }
                    }
                });

                rFoundPoResult = rFoundPoResult.stream().filter(item -> qokcodelist.contains(item.getCode())).collect(Collectors.toList());
            }


            //基金筛选:排序
            rFoundPoResult.sort(Comparator.comparing(FoundPo::getL3y));
            Collections.reverse(rFoundPoResult);

            logger.info("size={}", rFoundPoResult.size());
            for (FoundPo item : rFoundPoResult) {
                logger.info("code={} name={}, levle={}, ly={}% l3y={}% ccode={},cname={}",
                        item.getCode(), item.getName(), item.getLevel(),
                        item.getL1y(), item.getL3y(),
                        item.getComcode(), comMap.get(item.getComcode()).getName());
            }
            /*
            select * from fund  left outer join comp on fund.comcode = comp.comcode
            where
                fund.ft in("gp","hh") 			#类型为股票和混合
                and comp.ft in("gp","hh")
                and fund.level>=3.5            #机构为4星级以上
                and fund.l1y>=30             #近一年30%
                and fund.l3y>=50             #近3年50%
                and comp.estime<='2014-06-06'  #2014年前的基金公司，穿越牛熊周期
                and comp.ordernum<= 30      #基金公司规模在前20名
            group by code  order by l3y desc
             */


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }

    private static RestTemplate restTemplate = new RestTemplate();

    private List<String> getExistCodes(List<String> codes) {
        List<String> existCodes = null;
        try (SqlSession session = sqlSessionFactory.openSession(true)) {
            IFundQuarter iFundQuarter = session.getMapper(IFundQuarter.class);
            existCodes = iFundQuarter.getExistCodes(codes);
        } finally {
        }

        return existCodes;
    }

    private String getQuarterData(String code) {
        String pattern = "http://fundf10.eastmoney.com/FundArchivesDatas.aspx?type=quarterzf&code=%s&rt=0.13838623850422516";
        String url = String.format(pattern, code);
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
        String data = responseEntity.getBody();
        data = data.replaceAll("var qapidata=", "");
        data = data.replaceAll(";$", "");
        data = data.replaceAll("<img.+gif' >", "");
        return data;
    }

    private List<FundQuarterPo> parseQuarterData(String code, String data) {
        FundQuarterPo fq2 = FundQuarterPo.builder().code(code).build();
        FundQuarterPo fq3 = FundQuarterPo.builder().code(code).build();
        FundQuarterPo fq4 = FundQuarterPo.builder().code(code).build();
        FundQuarterPo fq5 = FundQuarterPo.builder().code(code).build();
        FundQuarterPo fq6 = FundQuarterPo.builder().code(code).build();
        FundQuarterPo fq7 = FundQuarterPo.builder().code(code).build();
        FundQuarterPo fq8 = FundQuarterPo.builder().code(code).build();
        FundQuarterPo fq9 = FundQuarterPo.builder().code(code).build();
        JSONObject configJson = JSON.parseObject(data);
        String content = configJson.getString("content");
        List<FundQuarterPo> fundQuarterPoList = new ArrayList<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            ByteArrayInputStream is = new ByteArrayInputStream(content.getBytes());
            Document document = builder.parse(is);
            NodeList nodelist = document.getElementsByTagName("table");
            Node table = nodelist.item(0);

            //季度
            Node n1 = table.getFirstChild().getFirstChild().getFirstChild();

            Node node = n1.getNextSibling();
            String quarterTime = node.getTextContent().substring(0, 2) + node.getTextContent().substring(3, 4);
            fq2.setQuarter(quarterTime);

            node = node.getNextSibling();
            quarterTime = node.getTextContent().substring(0, 2) + node.getTextContent().substring(3, 4);
            fq3.setQuarter(quarterTime);

            node = node.getNextSibling();
            quarterTime = node.getTextContent().substring(0, 2) + node.getTextContent().substring(3, 4);
            fq4.setQuarter(quarterTime);

            node = node.getNextSibling();
            quarterTime = node.getTextContent().substring(0, 2) + node.getTextContent().substring(3, 4);
            fq5.setQuarter(quarterTime);

            node = node.getNextSibling();
            quarterTime = node.getTextContent().substring(0, 2) + node.getTextContent().substring(3, 4);
            fq6.setQuarter(quarterTime);

            node = node.getNextSibling();
            quarterTime = node.getTextContent().substring(0, 2) + node.getTextContent().substring(3, 4);
            fq7.setQuarter(quarterTime);

            node = node.getNextSibling();
            quarterTime = node.getTextContent().substring(0, 2) + node.getTextContent().substring(3, 4);
            fq8.setQuarter(quarterTime);

            node = node.getNextSibling();
            quarterTime = node.getTextContent().substring(0, 2) + node.getTextContent().substring(3, 4);
            fq9.setQuarter(quarterTime);
//            logger.info("{}--{}--{}--{}--{}--{}--{}--{}--{}", "", date2, date3, date4, date5, date6, date7, date8, date9);

            Node tbodyNode = table.getLastChild();
            //阶段涨幅
            Node tr1Node = tbodyNode.getFirstChild();
            n1 = tr1Node.getFirstChild();
            node = n1.getNextSibling();
            content = node.getTextContent();
            if (!content.equals("---")) {
                Double r = Double.valueOf(content.replace("%", ""));
                fq2.setRise(r);
            }

            node = node.getNextSibling();
            content = node.getTextContent();
            if (!content.equals("---")) {
                Double r = Double.valueOf(content.replace("%", ""));
                fq3.setRise(r);
            }

            node = node.getNextSibling();
            content = node.getTextContent();
            if (!content.equals("---")) {
                Double r = Double.valueOf(content.replace("%", ""));
                fq4.setRise(r);
            }

            node = node.getNextSibling();
            content = node.getTextContent();
            if (!content.equals("---")) {
                Double r5 = Double.valueOf(content.replace("%", ""));
                fq5.setRise(r5);
            }

            node = node.getNextSibling();
            content = node.getTextContent();
            if (!content.equals("---")) {
                Double r = Double.valueOf(content.replace("%", ""));
                fq6.setRise(r);
            }

            node = node.getNextSibling();
            content = node.getTextContent();
            if (!content.equals("---")) {
                Double r = Double.valueOf(content.replace("%", ""));
                fq7.setRise(r);
            }

            node = node.getNextSibling();
            content = node.getTextContent();
            if (!content.equals("---")) {
                Double r = Double.valueOf(content.replace("%", ""));
                fq8.setRise(r);
            }

            node = node.getNextSibling();
            content = node.getTextContent();
            if (!content.equals("---")) {
                Double r = Double.valueOf(content.replace("%", ""));
                fq9.setRise(r);
            }

            //沪深300
            Node tr3Node = tr1Node.getNextSibling().getNextSibling();
            n1 = tr3Node.getFirstChild();
            node = n1.getNextSibling();
            content = node.getTextContent();
            if (!content.equals("---")) {
                Double.valueOf(content.replace("%", ""));
            }

            node = node.getNextSibling();
            content = node.getTextContent();
            if (!content.equals("---")) {
                Double.valueOf(content.replace("%", ""));
            }

            node = node.getNextSibling();
            content = node.getTextContent();
            if (!content.equals("---")) {
                Double.valueOf(content.replace("%", ""));
            }

            node = node.getNextSibling();
            content = node.getTextContent();
            if (!content.equals("---")) {
                Double.valueOf(content.replace("%", ""));
            }

            node = node.getNextSibling();
            content = node.getTextContent();
            if (!content.equals("---")) {
                Double.valueOf(content.replace("%", ""));
            }

            node = node.getNextSibling();
            content = node.getTextContent();
            if (!content.equals("---")) {
                Double.valueOf(content.replace("%", ""));
            }

            node = node.getNextSibling();
            content = node.getTextContent();
            if (!content.equals("---")) {
                Double.valueOf(content.replace("%", ""));
            }

            node = node.getNextSibling();
            content = node.getTextContent();
            if (!content.equals("---")) {
                Double.valueOf(content.replace("%", ""));
            }

            //同类排名
            Node tr4Node = tr3Node.getNextSibling();
            n1 = tr4Node.getFirstChild();
            node = n1.getNextSibling();
            content = node.getTextContent();
            if (!content.equals("---")) {
                String[] items = content.split("\\|");
                fq2.setRank(Double.valueOf(items[0]) / Double.valueOf(items[1]));
            }

            node = node.getNextSibling();
            content = node.getTextContent();
            if (!content.equals("---")) {
                String[] items = content.split("\\|");
                fq3.setRank(Double.valueOf(items[0]) / Double.valueOf(items[1]));
            }

            node = node.getNextSibling();
            content = node.getTextContent();
            if (!content.equals("---")) {
                String[] items = content.split("\\|");
                fq4.setRank(Double.valueOf(items[0]) / Double.valueOf(items[1]));
            }

            node = node.getNextSibling();
            content = node.getTextContent();
            if (!content.equals("---")) {
                String[] items = content.split("\\|");
                fq5.setRank(Double.valueOf(items[0]) / Double.valueOf(items[1]));
            }

            node = node.getNextSibling();
            content = node.getTextContent();
            if (!content.equals("---")) {
                String[] items = content.split("\\|");
                fq6.setRank(Double.valueOf(items[0]) / Double.valueOf(items[1]));
            }

            node = node.getNextSibling();
            content = node.getTextContent();
            if (!content.equals("---")) {
                String[] items = content.split("\\|");
                fq7.setRank(Double.valueOf(items[0]) / Double.valueOf(items[1]));
            }

            node = node.getNextSibling();
            content = node.getTextContent();
            if (!content.equals("---")) {
                String[] items = content.split("\\|");
                fq8.setRank(Double.valueOf(items[0]) / Double.valueOf(items[1]));
            }

            node = node.getNextSibling();
            content = node.getTextContent();
            if (!content.equals("---")) {
                String[] items = content.split("\\|");
                fq9.setRank(Double.valueOf(items[0]) / Double.valueOf(items[1]));
            }
//            logger.info("{}--{}--{}--{}--{}--{}--{}--{}--{}", n1.getTextContent(), r2, r3, r4, r5, r6, r7, r8, r9);


            if (null != fq2.getQuarter()) {
                fundQuarterPoList.add(fq2);
            }
            if (null != fq3.getQuarter()) {
                fundQuarterPoList.add(fq3);
            }
            if (null != fq4.getQuarter()) {
                fundQuarterPoList.add(fq4);
            }
            if (null != fq5.getQuarter()) {
                fundQuarterPoList.add(fq5);
            }
            if (null != fq6.getQuarter()) {
                fundQuarterPoList.add(fq6);
            }
            if (null != fq7.getQuarter()) {
                fundQuarterPoList.add(fq7);
            }
            if (null != fq8.getQuarter()) {
                fundQuarterPoList.add(fq8);
            }
            if (null != fq9.getQuarter()) {
                fundQuarterPoList.add(fq9);
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }

        return fundQuarterPoList;
    }

    private List<FundQuarterPo> getQuarterDataByCode(List<String> codeList) {
        List<FundQuarterPo> fqList = null;
        try (SqlSession session = sqlSessionFactory.openSession(true)) {
            IFundQuarter iFundQuarter = session.getMapper(IFundQuarter.class);
            fqList = iFundQuarter.getQuarterDataByCode(codeList);
        } finally {
        }
        return fqList;
    }

    private void downLoadFoudQuartData(List<String> notexistCodeList) {
        //阶段 http://fundf10.eastmoney.com/FundArchivesDatas.aspx?type=jdndzf&code=162006&rt=0.5906414726539972
        //季度 http://fundf10.eastmoney.com/FundArchivesDatas.aspx?type=quarterzf&code=162006&rt=0.13838623850422516
        //年度 http://fundf10.eastmoney.com/FundArchivesDatas.aspx?type=yearzf&code=162006&rt=0.35966013707740463

        List<FundQuarterPo> fundQuarterPoList = new ArrayList<>();
        for (String code : notexistCodeList) {
            String data = getQuarterData(code);
            List<FundQuarterPo> fql = parseQuarterData(code, data);
            fundQuarterPoList.addAll(fql);
        }

        if (!fundQuarterPoList.isEmpty()) {
            try (SqlSession session = sqlSessionFactory.openSession(true)) {
                IFundQuarter iFundQuarter = session.getMapper(IFundQuarter.class);
                iFundQuarter.insertFundQuarterBatch(fundQuarterPoList);
            } finally {
            }
        }
    }
}
