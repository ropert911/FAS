package com.xq.secser.ssbser.service;

import com.xq.secser.ssbser.model.ZQSubTypeEnum;
import com.xq.secser.ssbser.pojo.po.*;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author sk-qianxiao
 * @date 2020/1/7
 */
@Service
public class Strategy {
    private static Logger logger = LoggerFactory.getLogger(Strategy.class);

    @Value("${com.xq.secser.export.path}")
    private String outputpath;
    @Autowired
    FundHisService fundHisService;

    @Autowired
    SqlSessionFactory sqlSessionFactory;

    public void exportczq() {
        /**哪些类基金放在一起来找*/
        String[] ft = {"zq"};
        /**级别平均要大于等于4星*/
        double foudlevel = 4;
        /**近一年利率大于等于*/
        double l1y = 5;
        /**近三年利率大于等于*/
        double l3y = 15;

        /**季度历史排名平均在前50%--债券安全性高*/
        double qhisrank = 0.5;
        /**近5年历史排名平均在前50%--债券安全性高*/
        double yhisrank = 0.5;

        /**公司类型*/
        String[] cft = {"zq"};
        /**公司模型排名*/
        int topN = 40;
        /**公司成立要在这个时间点前，这样公司至少穿越了牛熊*/
        String esTime = "2014-06-06";

        //////////////////////////////////***********/////////////////
        /**公司筛选*/
        Map<String, CompPo> comMap = filterCompany(cft, esTime, topN);

        /**基金筛选:类型，级别，公司*/
        final List<FoundPo> foundPoResult = filterFund(ft, foudlevel, comMap);

        /**基金筛选:利润过滤，过滤纯债*/
        List<FoundPo> rFoundPoResult = foundPoResult.stream().filter(s -> s.getL1y() >= l1y).filter(s -> s.getL3y() >= l3y).
                filter(s->"lc".equals(s.getSubt())||"sc".equals(s.getSubt()))
                .collect(Collectors.toList());
        /**基金筛选:去重*/
        rFoundPoResult = rFoundPoResult.stream().collect(
                Collectors.collectingAndThen(
                        Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(FoundPo::getCode))), ArrayList::new)
        );

        /**对近2年季度排名进行处理*/
        List<String> foundCodeList = rFoundPoResult.stream().map(FoundPo::getCode).collect(Collectors.toList());
        fundHisService.dowloadHis(foundCodeList);
        /**获取季度数据*/
        List<FundQuarterPo> fundQuartList = fundHisService.getQuarterDataByCode(foundCodeList);
        //找到平均季度排名在0.3以下的
        Map<String, List<FundQuarterPo>> fqListMap = fundQuartList.stream().collect(Collectors.groupingBy(FundQuarterPo::getCode));
        {
            Set<String> qokcodelist = new HashSet<>();
            fqListMap.forEach((key, value) -> {
                if (!value.isEmpty()) {
                    Double rank = value.stream().filter(item -> item.getRank() != null).collect(Collectors.averagingDouble(FundQuarterPo::getRank));
                    if (rank <= qhisrank) {
                        qokcodelist.add(key);
                    }
                }
            });

            rFoundPoResult = rFoundPoResult.stream().filter(item -> qokcodelist.contains(item.getCode())).collect(Collectors.toList());
            foundCodeList = rFoundPoResult.stream().map(FoundPo::getCode).collect(Collectors.toList());
            logger.info("季度筛选后={}", foundCodeList.size());
        }

        //获取年度度数据筛选
        //获取季度数据
        List<FundYearPo> fundYearList = fundHisService.getYearDataByCode(foundCodeList);
        //找到平均季度排名在0.3以下的
        Map<String, List<FundYearPo>> fyListMap = fundYearList.stream().collect(Collectors.groupingBy(FundYearPo::getCode));
        {
            Set<String> qokcodelist = new HashSet<>();

            fyListMap.forEach((key, value) -> {
                if (!value.isEmpty()) {
                    Double rank = value.stream().filter(item -> item.getRank() != null).collect(Collectors.averagingDouble(FundYearPo::getRank));
                    if (rank <= yhisrank) {
                        qokcodelist.add(key);
                    }
                }
            });

            rFoundPoResult = rFoundPoResult.stream().filter(item -> qokcodelist.contains(item.getCode())).collect(Collectors.toList());
            foundCodeList = rFoundPoResult.stream().map(FoundPo::getCode).collect(Collectors.toList());
            logger.info("年筛选后={}", foundCodeList.size());
        }


        //基金筛选:排序
        rFoundPoResult.sort(Comparator.comparing(FoundPo::getL3y));
        Collections.reverse(rFoundPoResult);

        //显示
        printResult(rFoundPoResult, comMap, fyListMap, fqListMap);

        exportlzq(ft, rFoundPoResult, comMap, fyListMap, fqListMap);
    }

    public void searchZq() {
        /**哪些类基金放在一起来找*/
        String[] ft = {"zq"};
        /**级别平均要大于等于4星*/
        double foudlevel = 4;
        /**近一年利率大于等于*/
        double l1y = 7;
        /**近三年利率大于等于*/
        double l3y = 20;

        /**季度历史排名平均在前50%--债券安全性高*/
        double qhisrank = 0.5;
        /**近5年历史排名平均在前50%--债券安全性高*/
        double yhisrank = 0.5;

        /**公司类型*/
        String[] cft = {"zq"};
        /**公司模型排名*/
        int topN = 40;
        /**公司成立要在这个时间点前，这样公司至少穿越了牛熊*/
        String esTime = "2014-06-06";

        //////////////////////////////////***********/////////////////
        /**公司筛选*/
        Map<String, CompPo> comMap = filterCompany(cft, esTime, topN);

        /**基金筛选:类型，级别，公司*/
        final List<FoundPo> foundPoResult = filterFund(ft, foudlevel, comMap);

        /**基金筛选:利润过滤*/
        List<FoundPo> rFoundPoResult = foundPoResult.stream().filter(s -> s.getL1y() >= l1y).filter(s -> s.getL3y() >= l3y).collect(Collectors.toList());
        /**基金筛选:去重*/
        rFoundPoResult = rFoundPoResult.stream().collect(
                Collectors.collectingAndThen(
                        Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(FoundPo::getCode))), ArrayList::new)
        );

        /**对近2年季度排名进行处理*/
        List<String> foundCodeList = rFoundPoResult.stream().map(FoundPo::getCode).collect(Collectors.toList());
        fundHisService.dowloadHis(foundCodeList);
        /**获取季度数据*/
        List<FundQuarterPo> fundQuartList = fundHisService.getQuarterDataByCode(foundCodeList);
        //找到平均季度排名在0.3以下的
        Map<String, List<FundQuarterPo>> fqListMap = fundQuartList.stream().collect(Collectors.groupingBy(FundQuarterPo::getCode));
        {
            Set<String> qokcodelist = new HashSet<>();
            fqListMap.forEach((key, value) -> {
                if (!value.isEmpty()) {
                    Double rank = value.stream().filter(item -> item.getRank() != null).collect(Collectors.averagingDouble(FundQuarterPo::getRank));
                    if (rank <= qhisrank) {
                        qokcodelist.add(key);
                    }
                }
            });

            rFoundPoResult = rFoundPoResult.stream().filter(item -> qokcodelist.contains(item.getCode())).collect(Collectors.toList());
            foundCodeList = rFoundPoResult.stream().map(FoundPo::getCode).collect(Collectors.toList());
            logger.info("季度筛选后={}", foundCodeList.size());
        }

        //获取年度度数据筛选
        //获取季度数据
        List<FundYearPo> fundYearList = fundHisService.getYearDataByCode(foundCodeList);
        //找到平均季度排名在0.3以下的
        Map<String, List<FundYearPo>> fyListMap = fundYearList.stream().collect(Collectors.groupingBy(FundYearPo::getCode));
        {
            Set<String> qokcodelist = new HashSet<>();

            fyListMap.forEach((key, value) -> {
                if (!value.isEmpty()) {
                    Double rank = value.stream().filter(item -> item.getRank() != null).collect(Collectors.averagingDouble(FundYearPo::getRank));
                    if (rank <= yhisrank) {
                        qokcodelist.add(key);
                    }
                }
            });

            rFoundPoResult = rFoundPoResult.stream().filter(item -> qokcodelist.contains(item.getCode())).collect(Collectors.toList());
            foundCodeList = rFoundPoResult.stream().map(FoundPo::getCode).collect(Collectors.toList());
            logger.info("年筛选后={}", foundCodeList.size());
        }


        //基金筛选:排序
        rFoundPoResult.sort(Comparator.comparing(FoundPo::getL3y));
        Collections.reverse(rFoundPoResult);

        //显示
        printResult(rFoundPoResult, comMap, fyListMap, fqListMap);

        exportzqAll(ft, rFoundPoResult, comMap, fyListMap, fqListMap);
    }

    public void searchGpAHh() {
        /**哪些类基金放在一起来找*/
        String[] ft = {"gp", "hh"};
        /**级别要大于这个级别才会有4级的评价*/
        double foudlevel = 3.5;
        /**近一年利率大于等于*/
        double l1y = 8;
        /**近三年利率大于等于*/
        double l3y = 24;

        /**季度历史排名平均在前30%*/
        double qhisrank = 0.3;
        /**近5年历史排名平均在前30%*/
        double yhisrank = 0.3;

        /**公司类型*/
        String[] cft = {"gp", "hh"};
        /**公司模型排名*/
        int topN = 30;
        /**公司成立要在这个时间点前，这样公司至少穿越了牛熊*/
        String esTime = "2014-06-06";

        //////////////////////////////////***********/////////////////
        /**公司筛选*/
        Map<String, CompPo> comMap = filterCompany(cft, esTime, topN);

        /**基金筛选:类型，级别，公司*/
        final List<FoundPo> foundPoResult = filterFund(ft, foudlevel, comMap);

        /**基金筛选:利润过滤*/
        List<FoundPo> rFoundPoResult = foundPoResult.stream().filter(s -> s.getL1y() >= l1y).filter(s -> s.getL3y() >= l3y).collect(Collectors.toList());
        /**基金筛选:去重*/
        rFoundPoResult = rFoundPoResult.stream().collect(
                Collectors.collectingAndThen(
                        Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(FoundPo::getCode))), ArrayList::new)
        );

        /**对近2年季度排名进行处理*/
        List<String> foundCodeList = rFoundPoResult.stream().map(FoundPo::getCode).collect(Collectors.toList());
        fundHisService.dowloadHis(foundCodeList);
        /**获取季度数据*/
        List<FundQuarterPo> fundQuartList = fundHisService.getQuarterDataByCode(foundCodeList);
        //找到平均季度排名在0.3以下的
        Map<String, List<FundQuarterPo>> fqListMap = fundQuartList.stream().collect(Collectors.groupingBy(FundQuarterPo::getCode));
        {
            Set<String> qokcodelist = new HashSet<>();
            fqListMap.forEach((key, value) -> {
                if (!value.isEmpty()) {
                    Double rank = value.stream().filter(item -> item.getRank() != null).collect(Collectors.averagingDouble(FundQuarterPo::getRank));
                    if (rank <= qhisrank) {
                        qokcodelist.add(key);
                    }
                }
            });

            rFoundPoResult = rFoundPoResult.stream().filter(item -> qokcodelist.contains(item.getCode())).collect(Collectors.toList());
            foundCodeList = rFoundPoResult.stream().map(FoundPo::getCode).collect(Collectors.toList());
            logger.info("季度筛选后={}", foundCodeList.size());
        }

        //获取年度度数据筛选
        //获取季度数据
        List<FundYearPo> fundYearList = fundHisService.getYearDataByCode(foundCodeList);
        //找到平均季度排名在0.3以下的
        Map<String, List<FundYearPo>> fyListMap = fundYearList.stream().collect(Collectors.groupingBy(FundYearPo::getCode));
        {
            Set<String> qokcodelist = new HashSet<>();

            fyListMap.forEach((key, value) -> {
                if (!value.isEmpty()) {
                    Double rank = value.stream().filter(item -> item.getRank() != null).collect(Collectors.averagingDouble(FundYearPo::getRank));
                    if (rank <= yhisrank) {
                        qokcodelist.add(key);
                    }
                }
            });

            rFoundPoResult = rFoundPoResult.stream().filter(item -> qokcodelist.contains(item.getCode())).collect(Collectors.toList());
            foundCodeList = rFoundPoResult.stream().map(FoundPo::getCode).collect(Collectors.toList());
            logger.info("年筛选后={}", foundCodeList.size());
        }


        //基金筛选:排序
        rFoundPoResult.sort(Comparator.comparing(FoundPo::getL3y));
        Collections.reverse(rFoundPoResult);

        //显示
        printResult(rFoundPoResult, comMap, fyListMap, fqListMap);

        exportgphh(ft, rFoundPoResult, comMap, fyListMap, fqListMap);
    }

    private void printResult(List<FoundPo> rFoundPoResult, Map<String, CompPo> comMap, Map<String, List<FundYearPo>> fyListMap, Map<String, List<FundQuarterPo>> fqListMap) {
        DecimalFormat df = new DecimalFormat("0.000");

        for (FoundPo item : rFoundPoResult) {
            List<FundYearPo> fundYearPoList = fyListMap.get(item.getCode());
            List<FundQuarterPo> fundQuarterPoList = fqListMap.get(item.getCode());
            int leny = fundYearPoList.size();
            FundYearPo y1 = leny > 0 ? fundYearPoList.get(0) : null;
            FundYearPo y2 = leny > 1 ? fundYearPoList.get(1) : null;
            FundYearPo y3 = leny > 2 ? fundYearPoList.get(2) : null;
            FundYearPo y4 = leny > 3 ? fundYearPoList.get(3) : null;
            FundYearPo y5 = leny > 4 ? fundYearPoList.get(4) : null;
            int lenq = fundQuarterPoList.size();
            FundQuarterPo q1 = lenq > 0 ? fundQuarterPoList.get(0) : null;
            FundQuarterPo q2 = lenq > 1 ? fundQuarterPoList.get(1) : null;
            FundQuarterPo q3 = lenq > 2 ? fundQuarterPoList.get(2) : null;
            FundQuarterPo q4 = lenq > 3 ? fundQuarterPoList.get(3) : null;
            FundQuarterPo q5 = lenq > 4 ? fundQuarterPoList.get(4) : null;
            FundQuarterPo q6 = lenq > 5 ? fundQuarterPoList.get(5) : null;
            FundQuarterPo q7 = lenq > 6 ? fundQuarterPoList.get(6) : null;
            FundQuarterPo q8 = lenq > 7 ? fundQuarterPoList.get(7) : null;
            System.out.println(String.format(
                    "code=%s name=%s  level=%.3f 公司代号=%s 公司=%s 近1年=%s%%  近3年%s%%",
                    item.getCode(), item.getName(), item.getLevel(),
                    item.getComcode(), comMap.get(item.getComcode()).getName(),
                    df.format(item.getL1y()), df.format(item.getL3y())));
            System.out.println(String.format("        季度  %s=%s  %s=%s  %s=%s  %s=%s  %s=%s  %s=%s  %s=%s  %s=%s",
                    q1.getQuarter(), q1.getRank() != null ? df.format(q1.getRank()) : "-",
                    q2.getQuarter(), q2.getRank() != null ? df.format(q2.getRank()) : "-",
                    q3.getQuarter(), q3.getRank() != null ? df.format(q3.getRank()) : "-",
                    q4.getQuarter(), q4.getRank() != null ? df.format(q4.getRank()) : "-",
                    q5.getQuarter(), q5.getRank() != null ? df.format(q5.getRank()) : "-",
                    q6.getQuarter(), q6.getRank() != null ? df.format(q6.getRank()) : "-",
                    q7.getQuarter(), q7.getRank() != null ? df.format(q7.getRank()) : "-",
                    q8.getQuarter(), q8.getRank() != null ? df.format(q8.getRank()) : "-"));
            System.out.println(String.format("        年度  %s=%s  %s=%s  %s=%s  %s=%s  %s=%s",
                    y1 != null ? y1.getYear() : "null", y1 != null ? df.format(y1.getRank()) : "null",
                    y2 != null ? y2.getYear() : "null", y2 != null ? df.format(y2.getRank()) : "null",
                    y3 != null ? y3.getYear() : "null", y3 != null ? df.format(y3.getRank()) : "null",
                    y4 != null ? y4.getYear() : "null", y4 != null ? df.format(y4.getRank()) : "null",
                    y5 != null ? y5.getYear() : "null", y5 != null ? df.format(y5.getRank()) : "null"));
        }
    }

    private void exportgphh(String[] ft, List<FoundPo> rFoundPoResult, Map<String, CompPo> comMap, Map<String, List<FundYearPo>> fyListMap, Map<String, List<FundQuarterPo>> fqListMap) {
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet("股票混合");
        final XSSFRow row = sheet.createRow(0);
        XSSFCell cellCode = row.createCell(0);
        XSSFCell cellName = row.createCell(1);
        sheet.setColumnWidth(1, 6000);
        XSSFCell cellLevel = row.createCell(2);
        XSSFCell cellCCode = row.createCell(3);
        XSSFCell cellCName = row.createCell(4);
        sheet.setColumnWidth(4, 6500);
        XSSFCell cellCL1y = row.createCell(5);
        XSSFCell cellCL3y = row.createCell(6);
        cellCode.setCellValue("代码");
        cellName.setCellValue("名称");
        cellLevel.setCellValue("级别");
        cellCCode.setCellValue("公司代码");
        cellCName.setCellValue("公司名称");
        cellCL1y.setCellValue("近1年");
        cellCL3y.setCellValue("近3年");

        DecimalFormat df = new DecimalFormat("0.000");

        int rowNum = 1;
        for (FoundPo item : rFoundPoResult) {
            List<FundYearPo> fundYearPoList = fyListMap.get(item.getCode());
            List<FundQuarterPo> fundQuarterPoList = fqListMap.get(item.getCode());


            if (1 == rowNum) {
                int rowIndex = 7;
                for (FundQuarterPo fundQuarterPo : fundQuarterPoList) {
                    XSSFCell cq = row.createCell(rowIndex++);
                    cq.setCellValue(fundQuarterPo.getQuarter() + "季");
                }

                rowIndex = 15;
                for (FundYearPo fundYearPo : fundYearPoList) {
                    XSSFCell cy = row.createCell(rowIndex++);
                    cy.setCellValue(fundYearPo.getYear() + "年");
                }
            }

            XSSFRow row2 = sheet.createRow(rowNum++);
            cellCode = row2.createCell(0);
            cellCode.setCellValue(item.getCode());
            cellName = row2.createCell(1);
            cellName.setCellValue(item.getName());
            cellLevel = row2.createCell(2);
            cellLevel.setCellValue(df.format(item.getLevel()));
            cellCCode = row2.createCell(3);
            cellCCode.setCellValue(item.getComcode());
            cellCName = row2.createCell(4);
            cellCName.setCellValue(comMap.get(item.getComcode()).getName());
            cellCL1y = row2.createCell(5);
            cellCL1y.setCellValue(df.format(item.getL1y()));
            cellCL3y = row2.createCell(6);
            cellCL3y.setCellValue(df.format(item.getL3y()));

            //季度
            int rowIndex = 7;
            for (FundQuarterPo fundQuarterPo : fundQuarterPoList) {
                XSSFCell cq = row2.createCell(rowIndex++);
                cq.setCellValue(fundQuarterPo.getRank() != null ? df.format(fundQuarterPo.getRank()) : "-");
            }

            //年
            rowIndex = 15;
            for (FundYearPo fundYearPo : fundYearPoList) {
                XSSFCell cy1 = row2.createCell(rowIndex++);
                cy1.setCellValue(fundYearPo != null ? df.format(fundYearPo.getRank()) : "-");
            }
        }

        try {
            String fname = "out";
            for (String t : ft) {
                fname = fname + "_" + t;
            }
            fname += ".xlsx";
            File outfile = new File(outputpath + File.separator + fname);
            FileOutputStream outputStream = new FileOutputStream(outfile);
            wb.write(outputStream);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void exportczq(String[] ft, List<FoundPo> rFoundPoResult, Map<String, CompPo> comMap, Map<String, List<FundYearPo>> fyListMap, Map<String, List<FundQuarterPo>> fqListMap) {
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet("债券");
        final XSSFRow row = sheet.createRow(0);
        XSSFCell cellCode = row.createCell(0);
        XSSFCell cellName = row.createCell(1);
        sheet.setColumnWidth(1, 6000);
        XSSFCell cellSubT = row.createCell(2);
        XSSFCell cellLevel = row.createCell(3);
        XSSFCell cellCCode = row.createCell(4);
        XSSFCell cellCName = row.createCell(5);
        sheet.setColumnWidth(5, 6500);
        XSSFCell cellCL1y = row.createCell(6);
        XSSFCell cellCL3y = row.createCell(7);
        cellCode.setCellValue("代码");
        cellName.setCellValue("名称");
        cellSubT.setCellValue("子类型");
        cellLevel.setCellValue("级别");
        cellCCode.setCellValue("公司代码");
        cellCName.setCellValue("公司名称");
        cellCL1y.setCellValue("近1年");
        cellCL3y.setCellValue("近3年");


        XSSFFont fonlc = wb.createFont();
        fonlc.setColor(IndexedColors.GREEN.index);
        XSSFCellStyle stylelc = wb.createCellStyle();
        stylelc.setFont(fonlc);
        XSSFFont fonsc = wb.createFont();
        fonsc.setColor(IndexedColors.BLUE.index);
        XSSFCellStyle stylesc = wb.createCellStyle();
        stylesc.setFont(fonsc);
        XSSFFont fonkz = wb.createFont();
        fonkz.setColor(IndexedColors.YELLOW.index);
        XSSFCellStyle stylekz = wb.createCellStyle();
        stylekz.setFont(fonkz);

        DecimalFormat df = new DecimalFormat("0.000");

        int rowNum = 1;
        for (FoundPo item : rFoundPoResult) {
            List<FundYearPo> fundYearPoList = fyListMap.get(item.getCode());
            List<FundQuarterPo> fundQuarterPoList = fqListMap.get(item.getCode());


            if (1 == rowNum) {
                int rowIndex = 8;
                for (FundQuarterPo fundQuarterPo : fundQuarterPoList) {
                    XSSFCell cq = row.createCell(rowIndex++);
                    cq.setCellValue(fundQuarterPo.getQuarter() + "季");
                }

                rowIndex = 16;
                for (FundYearPo fundYearPo : fundYearPoList) {
                    XSSFCell cy = row.createCell(rowIndex++);
                    cy.setCellValue(fundYearPo.getYear() + "年");
                }
            }

            XSSFRow row2 = sheet.createRow(rowNum++);
            cellCode = row2.createCell(0);
            cellCode.setCellValue(item.getCode());
            cellName = row2.createCell(1);
            cellName.setCellValue(item.getName());
            cellSubT = row2.createCell(2);
            cellSubT.setCellValue(ZQSubTypeEnum.getDisStringBySubT(item.getSubt()));
            cellLevel = row2.createCell(3);
            cellLevel.setCellValue(df.format(item.getLevel()));
            cellCCode = row2.createCell(4);
            cellCCode.setCellValue(item.getComcode());
            cellCName = row2.createCell(5);
            cellCName.setCellValue(comMap.get(item.getComcode()).getName());
            cellCL1y = row2.createCell(6);
            cellCL1y.setCellValue(df.format(item.getL1y()));
            cellCL3y = row2.createCell(7);
            cellCL3y.setCellValue(df.format(item.getL3y()));

            switch (item.getSubt()) {
                case "lc": {
                    cellCode.setCellStyle(stylelc);
                    cellName.setCellStyle(stylelc);
                }
                break;
                case "sc": {
                    cellCode.setCellStyle(stylesc);
                    cellName.setCellStyle(stylesc);
                }
                break;
                case "kz": {
                    cellCode.setCellStyle(stylekz);
                    cellName.setCellStyle(stylekz);
                }
                break;
            }

            //季度
            int rowIndex = 8;
            for (FundQuarterPo fundQuarterPo : fundQuarterPoList) {
                XSSFCell cq = row2.createCell(rowIndex++);
                cq.setCellValue(fundQuarterPo.getRank() != null ? df.format(fundQuarterPo.getRank()) : "-");
            }

            //年
            rowIndex = 16;
            for (FundYearPo fundYearPo : fundYearPoList) {
                XSSFCell cy1 = row2.createCell(rowIndex++);
                cy1.setCellValue(fundYearPo != null ? df.format(fundYearPo.getRank()) : "-");
            }
        }

        try {
            String fname = "out";
            for (String t : ft) {
                fname = fname + "_" + t;
            }
            fname += ".xlsx";
            File outfile = new File(outputpath + File.separator + fname);
            FileOutputStream outputStream = new FileOutputStream(outfile);
            wb.write(outputStream);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void exportlzq(String[] ft, List<FoundPo> rFoundPoResult, Map<String, CompPo> comMap, Map<String, List<FundYearPo>> fyListMap, Map<String, List<FundQuarterPo>> fqListMap) {
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet("纯债");
        final XSSFRow row = sheet.createRow(0);
        XSSFCell cellCode = row.createCell(0);
        XSSFCell cellName = row.createCell(1);
        sheet.setColumnWidth(1, 6000);
        XSSFCell cellSubT = row.createCell(2);
        XSSFCell cellLevel = row.createCell(3);
        XSSFCell cellCCode = row.createCell(4);
        XSSFCell cellCName = row.createCell(5);
        sheet.setColumnWidth(5, 6500);
        XSSFCell cellCL1y = row.createCell(6);
        XSSFCell cellCL3y = row.createCell(7);
        cellCode.setCellValue("代码");
        cellName.setCellValue("名称");
        cellSubT.setCellValue("子类型");
        cellLevel.setCellValue("级别");
        cellCCode.setCellValue("公司代码");
        cellCName.setCellValue("公司名称");
        cellCL1y.setCellValue("近1年");
        cellCL3y.setCellValue("近3年");


        XSSFFont fonlc = wb.createFont();
        fonlc.setColor(IndexedColors.GREEN.index);
        XSSFCellStyle stylelc = wb.createCellStyle();
        stylelc.setFont(fonlc);
        XSSFFont fonsc = wb.createFont();
        fonsc.setColor(IndexedColors.BLUE.index);
        XSSFCellStyle stylesc = wb.createCellStyle();
        stylesc.setFont(fonsc);
        XSSFFont fonkz = wb.createFont();
        fonkz.setColor(IndexedColors.YELLOW.index);
        XSSFCellStyle stylekz = wb.createCellStyle();
        stylekz.setFont(fonkz);

        DecimalFormat df = new DecimalFormat("0.000");

        int rowNum = 1;
        for (FoundPo item : rFoundPoResult) {
            List<FundYearPo> fundYearPoList = fyListMap.get(item.getCode());
            List<FundQuarterPo> fundQuarterPoList = fqListMap.get(item.getCode());


            if (1 == rowNum) {
                int rowIndex = 8;
                for (FundQuarterPo fundQuarterPo : fundQuarterPoList) {
                    XSSFCell cq = row.createCell(rowIndex++);
                    cq.setCellValue(fundQuarterPo.getQuarter() + "季");
                }

                rowIndex = 16;
                for (FundYearPo fundYearPo : fundYearPoList) {
                    XSSFCell cy = row.createCell(rowIndex++);
                    cy.setCellValue(fundYearPo.getYear() + "年");
                }
            }

            XSSFRow row2 = sheet.createRow(rowNum++);
            cellCode = row2.createCell(0);
            cellCode.setCellValue(item.getCode());
            cellName = row2.createCell(1);
            cellName.setCellValue(item.getName());
            cellSubT = row2.createCell(2);
            cellSubT.setCellValue(ZQSubTypeEnum.getDisStringBySubT(item.getSubt()));
            cellLevel = row2.createCell(3);
            cellLevel.setCellValue(df.format(item.getLevel()));
            cellCCode = row2.createCell(4);
            cellCCode.setCellValue(item.getComcode());
            cellCName = row2.createCell(5);
            cellCName.setCellValue(comMap.get(item.getComcode()).getName());
            cellCL1y = row2.createCell(6);
            cellCL1y.setCellValue(df.format(item.getL1y()));
            cellCL3y = row2.createCell(7);
            cellCL3y.setCellValue(df.format(item.getL3y()));

            switch (item.getSubt()) {
                case "lc": {
                    cellCode.setCellStyle(stylelc);
                    cellName.setCellStyle(stylelc);
                }
                break;
                case "sc": {
                    cellCode.setCellStyle(stylesc);
                    cellName.setCellStyle(stylesc);
                }
                break;
                case "kz": {
                    cellCode.setCellStyle(stylekz);
                    cellName.setCellStyle(stylekz);
                }
                break;
            }

            //季度
            int rowIndex = 8;
            for (FundQuarterPo fundQuarterPo : fundQuarterPoList) {
                XSSFCell cq = row2.createCell(rowIndex++);
                cq.setCellValue(fundQuarterPo.getRank() != null ? df.format(fundQuarterPo.getRank()) : "-");
            }

            //年
            rowIndex = 16;
            for (FundYearPo fundYearPo : fundYearPoList) {
                XSSFCell cy1 = row2.createCell(rowIndex++);
                cy1.setCellValue(fundYearPo != null ? df.format(fundYearPo.getRank()) : "-");
            }
        }

        try {
            String fname = "out_lzq.xlsx";
            File outfile = new File(outputpath + File.separator + fname);
            FileOutputStream outputStream = new FileOutputStream(outfile);
            wb.write(outputStream);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void exportzqAll(String[] ft, List<FoundPo> rFoundPoResult, Map<String, CompPo> comMap, Map<String, List<FundYearPo>> fyListMap, Map<String, List<FundQuarterPo>> fqListMap) {
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet("债券");
        final XSSFRow row = sheet.createRow(0);
        XSSFCell cellCode = row.createCell(0);
        XSSFCell cellName = row.createCell(1);
        sheet.setColumnWidth(1, 6000);
        XSSFCell cellSubT = row.createCell(2);
        XSSFCell cellLevel = row.createCell(3);
        XSSFCell cellCCode = row.createCell(4);
        XSSFCell cellCName = row.createCell(5);
        sheet.setColumnWidth(5, 6500);
        XSSFCell cellCL1y = row.createCell(6);
        XSSFCell cellCL3y = row.createCell(7);
        cellCode.setCellValue("代码");
        cellName.setCellValue("名称");
        cellSubT.setCellValue("子类型");
        cellLevel.setCellValue("级别");
        cellCCode.setCellValue("公司代码");
        cellCName.setCellValue("公司名称");
        cellCL1y.setCellValue("近1年");
        cellCL3y.setCellValue("近3年");


        XSSFFont fonlc = wb.createFont();
        fonlc.setColor(IndexedColors.GREEN.index);
        XSSFCellStyle stylelc = wb.createCellStyle();
        stylelc.setFont(fonlc);
        XSSFFont fonsc = wb.createFont();
        fonsc.setColor(IndexedColors.BLUE.index);
        XSSFCellStyle stylesc = wb.createCellStyle();
        stylesc.setFont(fonsc);
        XSSFFont fonkz = wb.createFont();
        fonkz.setColor(IndexedColors.YELLOW.index);
        XSSFCellStyle stylekz = wb.createCellStyle();
        stylekz.setFont(fonkz);

        DecimalFormat df = new DecimalFormat("0.000");

        int rowNum = 1;
        for (FoundPo item : rFoundPoResult) {
            List<FundYearPo> fundYearPoList = fyListMap.get(item.getCode());
            List<FundQuarterPo> fundQuarterPoList = fqListMap.get(item.getCode());


            if (1 == rowNum) {
                int rowIndex = 8;
                for (FundQuarterPo fundQuarterPo : fundQuarterPoList) {
                    XSSFCell cq = row.createCell(rowIndex++);
                    cq.setCellValue(fundQuarterPo.getQuarter() + "季");
                }

                rowIndex = 16;
                for (FundYearPo fundYearPo : fundYearPoList) {
                    XSSFCell cy = row.createCell(rowIndex++);
                    cy.setCellValue(fundYearPo.getYear() + "年");
                }
            }

            XSSFRow row2 = sheet.createRow(rowNum++);
            cellCode = row2.createCell(0);
            cellCode.setCellValue(item.getCode());
            cellName = row2.createCell(1);
            cellName.setCellValue(item.getName());
            cellSubT = row2.createCell(2);
            cellSubT.setCellValue(ZQSubTypeEnum.getDisStringBySubT(item.getSubt()));
            cellLevel = row2.createCell(3);
            cellLevel.setCellValue(df.format(item.getLevel()));
            cellCCode = row2.createCell(4);
            cellCCode.setCellValue(item.getComcode());
            cellCName = row2.createCell(5);
            cellCName.setCellValue(comMap.get(item.getComcode()).getName());
            cellCL1y = row2.createCell(6);
            cellCL1y.setCellValue(df.format(item.getL1y()));
            cellCL3y = row2.createCell(7);
            cellCL3y.setCellValue(df.format(item.getL3y()));

            switch (item.getSubt()) {
                case "lc": {
                    cellCode.setCellStyle(stylelc);
                    cellName.setCellStyle(stylelc);
                }
                break;
                case "sc": {
                    cellCode.setCellStyle(stylesc);
                    cellName.setCellStyle(stylesc);
                }
                break;
                case "kz": {
                    cellCode.setCellStyle(stylekz);
                    cellName.setCellStyle(stylekz);
                }
                break;
            }

            //季度
            int rowIndex = 8;
            for (FundQuarterPo fundQuarterPo : fundQuarterPoList) {
                XSSFCell cq = row2.createCell(rowIndex++);
                cq.setCellValue(fundQuarterPo.getRank() != null ? df.format(fundQuarterPo.getRank()) : "-");
            }

            //年
            rowIndex = 16;
            for (FundYearPo fundYearPo : fundYearPoList) {
                XSSFCell cy1 = row2.createCell(rowIndex++);
                cy1.setCellValue(fundYearPo != null ? df.format(fundYearPo.getRank()) : "-");
            }
        }

        try {
            String fname = "out";
            for (String t : ft) {
                fname = fname + "_" + t;
            }
            fname += ".xlsx";
            File outfile = new File(outputpath + File.separator + fname);
            FileOutputStream outputStream = new FileOutputStream(outfile);
            wb.write(outputStream);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    List<FoundPo> filterFund(String[] ft, double foudLevel, Map<String, CompPo> comMap) {
        final List<FoundPo> foundPoResult = new ArrayList<>();
        try (SqlSession session = sqlSessionFactory.openSession(true)) {
            IFund iFund = session.getMapper(IFund.class);

            for (String t : ft) {
                List<FoundPo> foundPoList = iFund.getByLevel(t, foudLevel);
                foundPoList.forEach(item -> {
                    if (null != comMap.get(item.getComcode())) {
                        foundPoResult.add(item);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }

        return foundPoResult;
    }

    private Map<String, CompPo> filterCompany(String[] ft, String esTime, int topN) {
        Map<String, CompPo> comMap = new HashMap<>();

        try (SqlSession session = sqlSessionFactory.openSession(true)) {
            IComp iComp = session.getMapper(IComp.class);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date dateEs = format.parse(esTime);
            for (String t : ft) {
                List<CompPo> compPoList = iComp.getTopN(t, topN);
                compPoList.stream().filter(item -> item.getEstime().compareTo(dateEs) < 0).forEach(item -> comMap.put(item.getComcode(), item));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }

        return comMap;
    }
}
