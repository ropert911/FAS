package com.xq.secser.provider.tt.provider;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xq.secser.provider.FundHisProvider;
import com.xq.secser.ssbser.pojo.po.FundQuarterPo;
import com.xq.secser.ssbser.pojo.po.FundYearPo;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.ArrayList;
import java.util.List;

/**
 * @author sk-qianxiao
 * @date 2020/1/7
 */
@Service
public class TTFundHisProvider implements FundHisProvider {
    private static Logger logger = LoggerFactory.getLogger(TTFundHisProvider.class);
    @Autowired
    SqlSessionFactory sqlSessionFactory;

    private static RestTemplate restTemplate = new RestTemplate();


    @Override
    public List<FundQuarterPo> downLoadFoudQuartData(List<String> notexistCodeList) {
        //阶段 http://fundf10.eastmoney.com/FundArchivesDatas.aspx?type=jdndzf&code=162006&rt=0.5906414726539972
        //季度 http://fundf10.eastmoney.com/FundArchivesDatas.aspx?type=quarterzf&code=162006&rt=0.13838623850422516
        //年度 http://fundf10.eastmoney.com/FundArchivesDatas.aspx?type=yearzf&code=162006&rt=0.35966013707740463

        List<FundQuarterPo> fundQuarterPoList = new ArrayList<>();
        for (String code : notexistCodeList) {
            String data = getQuarterData(code);
            List<FundQuarterPo> fql = parseQuarterData(code, data);
            fundQuarterPoList.addAll(fql);
        }

        return fundQuarterPoList;
    }

    @Override
    public List<FundYearPo> downLoadFoudYearData(List<String> notexistCodeList) {
        List<FundYearPo> fundYearPos = new ArrayList<>();
        for (String code : notexistCodeList) {
            String data = getYearData(code);
            List<FundYearPo> fql = parseYearData(code, data);
            fundYearPos.addAll(fql);
        }

        return fundYearPos;
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


    private String getYearData(String code) {
        String pattern = "http://fundf10.eastmoney.com/FundArchivesDatas.aspx?type=yearzf&code=%s&rt=0.35966013707740463";
        String url = String.format(pattern, code);
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
        String data = responseEntity.getBody();
//        logger.info("{}",data);
        data = data.replaceAll("var yapidata=", "");
//        logger.info("{}",data);
        data = data.replaceAll(";$", "");
//        logger.info("{}",data);
        data = data.replaceAll("<img.+gif' >", "");
//        logger.info("{}",data);
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


    private List<FundYearPo> parseYearData(String code, String data) {
        FundYearPo fy2 = FundYearPo.builder().code(code).build();
        FundYearPo fy3 = FundYearPo.builder().code(code).build();
        FundYearPo fy4 = FundYearPo.builder().code(code).build();
        FundYearPo fy5 = FundYearPo.builder().code(code).build();
        FundYearPo fy6 = FundYearPo.builder().code(code).build();
        JSONObject configJson = JSON.parseObject(data);
        String content = configJson.getString("content");
        List<FundYearPo> fundYearPoList = new ArrayList<>();
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
            String yearTime = node.getTextContent().substring(0, 4);
            fy2.setYear(yearTime);

            node = node.getNextSibling();
            yearTime = node.getTextContent().substring(0, 4);
            fy3.setYear(yearTime);

            node = node.getNextSibling();
            yearTime = node.getTextContent().substring(0, 4);
            fy4.setYear(yearTime);

            node = node.getNextSibling();
            yearTime = node.getTextContent().substring(0, 4);
            fy5.setYear(yearTime);

            node = node.getNextSibling();
            yearTime = node.getTextContent().substring(0, 4);
            fy6.setYear(yearTime);

//            logger.info("{}--{}--{}--{}--{}--{}--{}--{}--{}", "", date2, date3, date4, date5, date6, date7, date8, date9);

            Node tbodyNode = table.getLastChild();
            //阶段涨幅
            Node tr1Node = tbodyNode.getFirstChild();
            n1 = tr1Node.getFirstChild();
            node = n1.getNextSibling();
            content = node.getTextContent();
            if (!content.equals("---")) {
                Double r = Double.valueOf(content.replace("%", ""));
                fy2.setRise(r);
            }

            node = node.getNextSibling();
            content = node.getTextContent();
            if (!content.equals("---")) {
                Double r = Double.valueOf(content.replace("%", ""));
                fy3.setRise(r);
            }

            node = node.getNextSibling();
            content = node.getTextContent();
            if (!content.equals("---")) {
                Double r = Double.valueOf(content.replace("%", ""));
                fy4.setRise(r);
            }

            node = node.getNextSibling();
            content = node.getTextContent();
            if (!content.equals("---")) {
                Double r5 = Double.valueOf(content.replace("%", ""));
                fy5.setRise(r5);
            }

            node = node.getNextSibling();
            content = node.getTextContent();
            if (!content.equals("---")) {
                Double r = Double.valueOf(content.replace("%", ""));
                fy6.setRise(r);
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

            //同类排名
            Node tr4Node = tr3Node.getNextSibling();
            n1 = tr4Node.getFirstChild();
            node = n1.getNextSibling();
            content = node.getTextContent();
            if (!content.equals("---")) {
                String[] items = content.split("\\|");
                fy2.setRank(Double.valueOf(items[0]) / Double.valueOf(items[1]));
            }

            node = node.getNextSibling();
            content = node.getTextContent();
            if (!content.equals("---")) {
                String[] items = content.split("\\|");
                fy3.setRank(Double.valueOf(items[0]) / Double.valueOf(items[1]));
            }

            node = node.getNextSibling();
            content = node.getTextContent();
            if (!content.equals("---")) {
                String[] items = content.split("\\|");
                fy4.setRank(Double.valueOf(items[0]) / Double.valueOf(items[1]));
            }

            node = node.getNextSibling();
            content = node.getTextContent();
            if (!content.equals("---")) {
                String[] items = content.split("\\|");
                fy5.setRank(Double.valueOf(items[0]) / Double.valueOf(items[1]));
            }

            node = node.getNextSibling();
            content = node.getTextContent();
            if (!content.equals("---")) {
                String[] items = content.split("\\|");
                fy6.setRank(Double.valueOf(items[0]) / Double.valueOf(items[1]));
            }

//            logger.info("{}--{}--{}--{}--{}--{}--{}--{}--{}", n1.getTextContent(), r2, r3, r4, r5, r6, r7, r8, r9);


            if (null != fy2.getRank()) {
                fundYearPoList.add(fy2);
            }
            if (null != fy3.getRank()) {
                fundYearPoList.add(fy3);
            }
            if (null != fy4.getRank()) {
                fundYearPoList.add(fy4);
            }
            if (null != fy5.getRank()) {
                fundYearPoList.add(fy5);
            }
            if (null != fy6.getRank()) {
                fundYearPoList.add(fy6);
            }

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }

        return fundYearPoList;
    }
}
