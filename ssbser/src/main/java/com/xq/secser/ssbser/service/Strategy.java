package com.xq.secser.ssbser.service;

import com.xq.secser.constant.TransactionTypeEnum;
import com.xq.secser.constant.ZQSubTypeEnum;
import com.xq.secser.ssbser.pojo.po.*;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.poi.ss.usermodel.*;
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
    FundService fundService;
    @Autowired
    FundHisService fundHisService;

    @Autowired
    SqlSessionFactory sqlSessionFactory;

    public void search() {
        List<StrategySearchInfo> strategySearchInfoList = new ArrayList<>();
        StrategySearchInfo czqSearchInfo = StrategySearchInfo.builder().ft(new String[]{"zq"}).subt(new String[]{"lc", "sc"}).foudLevel(4).l1y(5).l3y(15)
                .qhisrank(0.5).yhisrank(0.5)
                .cft(new String[]{"zq"}).topN(40).esTime("2014-06-06")
                .build();
        czqSearchInfo.setSheetName("低_纯债");
        strategySearchInfoList.add(czqSearchInfo);

        StrategySearchInfo zqSearchInfo = StrategySearchInfo.builder().ft(new String[]{"zq"}).subt(new String[]{}).foudLevel(4).l1y(7).l3y(20)
                .qhisrank(0.5).yhisrank(0.5)
                .cft(new String[]{"zq"}).topN(40).esTime("2014-06-06")
                .build();
        zqSearchInfo.setSheetName("中_债券");
        strategySearchInfoList.add(zqSearchInfo);

        StrategySearchInfo gphhSearchInfo = StrategySearchInfo.builder().ft(new String[]{"gp", "hh"}).subt(new String[]{}).foudLevel(3.5).l1y(8).l3y(24)
                .qhisrank(0.3).yhisrank(0.3)
                .cft(new String[]{"gp", "hh"}).topN(30).esTime("2014-06-06")
                .build();
        gphhSearchInfo.setSheetName("高_股票混合");
        strategySearchInfoList.add(gphhSearchInfo);

        XSSFWorkbook wb = new XSSFWorkbook();
        for (StrategySearchInfo s : strategySearchInfoList) {
            search_inner(wb, s);
        }

        try {
            String fileName = "out_fund.xlsx";
            File outfile = new File(outputpath + File.separator + fileName);
            FileOutputStream outputStream = new FileOutputStream(outfile);
            wb.write(outputStream);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void search_inner(XSSFWorkbook wb, StrategySearchInfo searchInfo) {
        /**公司筛选：类型，成立时间，规模排名*/
        Map<String, CompPo> comMap = filterCompany(searchInfo.getCft(), searchInfo.getEsTime(), searchInfo.getTopN());

        /**基金筛选:类型，级别，公司列表*/
        final List<FoundPo> foundPoResult = filterFund(searchInfo.getFt(), searchInfo.getFoudLevel(), comMap);
        logger.info("1. 初始基金 级别[{}] Topn[{}]=={}", searchInfo.getFoudLevel(), searchInfo.getTopN(), foundPoResult.stream().map(FoundPo::getCode).collect(Collectors.toSet()));

        /**基金筛选：近1、3年利润过滤；子类型过滤：如果为空，不过滤*/
        List<FoundPo> rFoundPoResult = foundPoResult.stream().filter(s -> s.getL1y() >= searchInfo.getL1y()).filter(s -> s.getL3y() >= searchInfo.getL3y())
                .filter(item -> {
                    if (searchInfo.getSubt().length == 0) {
                        return true;
                    } else {
                        boolean bMatch = false;
                        for (String sub : searchInfo.getSubt()) {
                            if (sub.equals(item.getSubt())) {
                                bMatch = true;
                            }
                        }
                        return bMatch;
                    }
                })
                .collect(Collectors.toList());

        /**基金筛选:去重*/
        rFoundPoResult = rFoundPoResult.stream().collect(
                Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(FoundPo::getCode))), ArrayList::new)
        );
        logger.info("2. 最近1、3年[{}][{}]过滤后=={}", searchInfo.getL1y(), searchInfo.getL3y(), rFoundPoResult.stream().map(FoundPo::getCode).collect(Collectors.toSet()));

        /**下载季和年历史；进行季度排名过滤；年排名过滤*/
        List<String> foundCodeList = rFoundPoResult.stream().map(FoundPo::getCode).collect(Collectors.toList());
        fundHisService.dowloadHis(foundCodeList);
        List<FundQuarterPo> fundQuartList = fundHisService.getQuarterDataByCode(foundCodeList);
        Map<String, List<FundQuarterPo>> fqListMap = fundQuartList.stream().collect(Collectors.groupingBy(FundQuarterPo::getCode));
        {
            Set<String> qokcodelist = new HashSet<>();
            fqListMap.forEach((key, value) -> {
                if (!value.isEmpty()) {
                    Double rank = value.stream().filter(item -> item.getRank() != null).collect(Collectors.averagingDouble(FundQuarterPo::getRank));
                    if (rank <= searchInfo.getQhisrank()) {
                        qokcodelist.add(key);
                    }
                }
            });

            rFoundPoResult = rFoundPoResult.stream().filter(item -> qokcodelist.contains(item.getCode())).collect(Collectors.toList());
            foundCodeList = rFoundPoResult.stream().map(FoundPo::getCode).collect(Collectors.toList());
        }
        logger.info("3. 季度排名{}过滤后[{}]=={}", searchInfo.getQhisrank(), foundCodeList.size(), foundCodeList);
        List<FundYearPo> fundYearList = fundHisService.getYearDataByCode(foundCodeList);
        Map<String, List<FundYearPo>> fyListMap = fundYearList.stream().collect(Collectors.groupingBy(FundYearPo::getCode));
        {
            Set<String> yokcodelist = new HashSet<>();
            fyListMap.forEach((key, value) -> {
                if (!value.isEmpty()) {
                    Double rank = value.stream().filter(item -> item.getRank() != null).collect(Collectors.averagingDouble(FundYearPo::getRank));
                    if (rank <= searchInfo.getYhisrank()) {
                        yokcodelist.add(key);
                    }
                }
            });

            rFoundPoResult = rFoundPoResult.stream().filter(item -> yokcodelist.contains(item.getCode())).collect(Collectors.toList());
            foundCodeList = rFoundPoResult.stream().map(FoundPo::getCode).collect(Collectors.toList());
        }
        logger.info("4. 年排名{}后过滤后[{}]个=={}", searchInfo.getYhisrank(), foundCodeList.size(), foundCodeList);


        //基金筛选:排序
        rFoundPoResult.sort(Comparator.comparing(FoundPo::getL3y));
        Collections.reverse(rFoundPoResult);

        List<FoundFlPo> flList = fundService.getflinfo(foundCodeList);
        Map<String, FoundFlPo> flMap = flList.stream().collect(Collectors.toMap(FoundFlPo::getCode, s -> s, (key1, key2) -> key2));

        //显示
        printResult(rFoundPoResult, comMap, fyListMap, fqListMap);

        exportFunds(wb, searchInfo.getSheetName(), rFoundPoResult, comMap, fyListMap, fqListMap, flMap);
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
//            System.out.println(String.format("        季度  %s=%s  %s=%s  %s=%s  %s=%s  %s=%s  %s=%s  %s=%s  %s=%s",
//                    q1.getQuarter(), q1.getRank() != null ? df.format(q1.getRank()) : "-",
//                    q2.getQuarter(), q2.getRank() != null ? df.format(q2.getRank()) : "-",
//                    q3.getQuarter(), q3.getRank() != null ? df.format(q3.getRank()) : "-",
//                    q4.getQuarter(), q4.getRank() != null ? df.format(q4.getRank()) : "-",
//                    q5.getQuarter(), q5.getRank() != null ? df.format(q5.getRank()) : "-",
//                    q6.getQuarter(), q6.getRank() != null ? df.format(q6.getRank()) : "-",
//                    q7.getQuarter(), q7.getRank() != null ? df.format(q7.getRank()) : "-",
//                    q8.getQuarter(), q8.getRank() != null ? df.format(q8.getRank()) : "-"));
//            System.out.println(String.format("        年度  %s=%s  %s=%s  %s=%s  %s=%s  %s=%s",
//                    y1 != null ? y1.getYear() : "null", y1 != null ? df.format(y1.getRank()) : "null",
//                    y2 != null ? y2.getYear() : "null", y2 != null ? df.format(y2.getRank()) : "null",
//                    y3 != null ? y3.getYear() : "null", y3 != null ? df.format(y3.getRank()) : "null",
//                    y4 != null ? y4.getYear() : "null", y4 != null ? df.format(y4.getRank()) : "null",
//                    y5 != null ? y5.getYear() : "null", y5 != null ? df.format(y5.getRank()) : "null"));
        }
    }

    private void exportFunds(XSSFWorkbook wb, String sheetName, List<FoundPo> rFoundPoResult, Map<String, CompPo> comMap,
                             Map<String, List<FundYearPo>> fyListMap, Map<String, List<FundQuarterPo>> fqListMap, Map<String, FoundFlPo> flMap) {
        XSSFSheet sheet = wb.createSheet(sheetName);
        int rowNum = 0;
        final XSSFRow row = sheet.createRow(rowNum++);
        int columnIndex = 0;
        int yearColumnIndex = 22;
        int flColumnIndex = yearColumnIndex + 5;
        XSSFCell cellCode = row.createCell(columnIndex++);
        XSSFCell cellName = row.createCell(columnIndex++);
        XSSFCell cellSubT = row.createCell(columnIndex++);
        XSSFCell cellLevel = row.createCell(columnIndex++);
        XSSFCell cellZcgf = row.createCell(columnIndex++);
        XSSFCell cellCCode = row.createCell(columnIndex++);
        XSSFCell cellCName = row.createCell(columnIndex++);
        XSSFCell cellCL1m = row.createCell(columnIndex++);
        XSSFCell cellCL3m = row.createCell(columnIndex++);
        XSSFCell cellCL6m = row.createCell(columnIndex++);
        XSSFCell cellCL1y = row.createCell(columnIndex++);
        XSSFCell cellCL2y = row.createCell(columnIndex++);
        XSSFCell cellCL3y = row.createCell(columnIndex++);
        XSSFCell cellComment = row.createCell(columnIndex++);
        cellCode.setCellValue("代码");
        cellName.setCellValue("名称");
        cellSubT.setCellValue("子类型");
        cellLevel.setCellValue("级别");
        cellZcgf.setCellValue("模型(亿元)");
        cellCCode.setCellValue("公司代码");
        cellCName.setCellValue("公司名称");
        cellCL1m.setCellValue("近1月");
        cellCL3m.setCellValue("近3月");
        cellCL6m.setCellValue("近6月");
        cellCL1y.setCellValue("近1年");
        cellCL2y.setCellValue("近2年");
        cellCL3y.setCellValue("近3年");
        cellComment.setCellValue("备注");

        sheet.setColumnWidth(cellName.getColumnIndex(), 6000);
        sheet.setColumnWidth(cellCCode.getColumnIndex(), 0);
        sheet.setColumnWidth(cellCName.getColumnIndex(), 6500);
        sheet.setColumnWidth(cellComment.getColumnIndex(), 6500);


        /**初始化行字体*/
        XSSFFont fongreen = wb.createFont();
        fongreen.setColor(IndexedColors.GREEN.index);
        XSSFCellStyle stylegreen = wb.createCellStyle();
        stylegreen.setFont(fongreen);

        XSSFFont fonSc = wb.createFont();
        fonSc.setColor(IndexedColors.BLUE.index);
        XSSFCellStyle styleSc = wb.createCellStyle();
        styleSc.setFont(fonSc);

        XSSFFont fonKz = wb.createFont();
        fonKz.setColor(IndexedColors.YELLOW.index);
        XSSFCellStyle styleKz = wb.createCellStyle();
        styleKz.setFont(fonKz);

        XSSFFont fonRed = wb.createFont();
        fonRed.setColor(IndexedColors.RED.index);
        XSSFCellStyle styleRed = wb.createCellStyle();
        styleRed.setFont(fonRed);

        XSSFFont fonRedBold = wb.createFont();
        fonRedBold.setColor(IndexedColors.RED.index);
        fonRedBold.setBold(true);
        XSSFCellStyle styleRedBold = wb.createCellStyle();
        styleRedBold.setFont(fonRedBold);


        DecimalFormat df = new DecimalFormat("0.000");


        for (FoundPo item : rFoundPoResult) {
            List<FundYearPo> fundYearPoList = fyListMap.get(item.getCode());
            List<FundQuarterPo> fundQuarterPoList = fqListMap.get(item.getCode());

            /**建表头*/
            if (1 == rowNum) {
                for (FundQuarterPo fundQuarterPo : fundQuarterPoList) {
                    XSSFCell cq = row.createCell(columnIndex++);
                    cq.setCellValue(fundQuarterPo.getQuarter() + "季");
                    sheet.setColumnWidth(cq.getColumnIndex(), 0);
                }

                columnIndex = yearColumnIndex;
                for (FundYearPo fundYearPo : fundYearPoList) {
                    XSSFCell cy = row.createCell(columnIndex++);
                    cy.setCellValue(fundYearPo.getYear() + "年");
                    sheet.setColumnWidth(cy.getColumnIndex(), 0);
                }

                columnIndex = flColumnIndex;
                XSSFCell sg = row.createCell(columnIndex++);
                sg.setCellValue("申购费");

                XSSFCell yz = row.createCell(columnIndex++);
                yz.setCellValue("动作费");

                XSSFCell sh1 = row.createCell(columnIndex++);
                sh1.setCellValue("赎回1");

                XSSFCell sh2 = row.createCell(columnIndex++);
                sh2.setCellValue("赎回2");

                XSSFCell sh3 = row.createCell(columnIndex++);
                sh3.setCellValue("赎回3");

                XSSFCell sh4 = row.createCell(columnIndex++);
                sh4.setCellValue("赎回3");
            }

            FoundFlPo flPo = flMap.get(item.getCode());
            XSSFRow row2 = sheet.createRow(rowNum++);
            columnIndex = 0;
            cellCode = row2.createCell(columnIndex++);
            cellCode.setCellValue(item.getCode());
            cellName = row2.createCell(columnIndex++);
            cellName.setCellValue(item.getName());
            cellSubT = row2.createCell(columnIndex++);
            cellSubT.setCellValue(ZQSubTypeEnum.getDisStringBySubT(item.getSubt()));
            cellLevel = row2.createCell(columnIndex++);
            cellLevel.setCellValue(df.format(item.getLevel()));
            cellZcgf = row2.createCell(columnIndex++);
            if (null != flPo) {
                cellZcgf.setCellValue(df.format(flPo.getZcgm()));
                if (flPo.getZcgm() < 8.0d) {
                    cellZcgf.setCellStyle(styleRedBold);
                }
            }
            cellCCode = row2.createCell(columnIndex++);
            cellCCode.setCellValue(item.getComcode());
            cellCName = row2.createCell(columnIndex++);
            cellCName.setCellValue(comMap.get(item.getComcode()).getName());
            cellCL1m = row2.createCell(columnIndex++);
            cellCL1m.setCellValue(df.format(item.getL1m()));
            cellCL3m = row2.createCell(columnIndex++);
            cellCL3m.setCellValue(df.format(item.getL3m()));
            cellCL6m = row2.createCell(columnIndex++);
            cellCL6m.setCellValue(df.format(item.getL6m()));
            cellCL1y = row2.createCell(columnIndex++);
            cellCL1y.setCellValue(df.format(item.getL1y()));
            cellCL2y = row2.createCell(columnIndex++);
            cellCL2y.setCellValue(df.format(item.getL2y()));
            cellCL3y = row2.createCell(columnIndex++);
            cellCL3y.setCellValue(df.format(item.getL3y()));
            row2.createCell(columnIndex++);

            //季度
            for (FundQuarterPo fundQuarterPo : fundQuarterPoList) {
                XSSFCell cq = row2.createCell(columnIndex++);
                cq.setCellValue(fundQuarterPo.getRank() != null ? df.format(fundQuarterPo.getRank()) : "-");
            }

            //年
            columnIndex = yearColumnIndex;
            for (FundYearPo fundYearPo : fundYearPoList) {
                XSSFCell cy1 = row2.createCell(columnIndex++);
                cy1.setCellValue(fundYearPo != null ? df.format(fundYearPo.getRank()) : "-");
            }

            columnIndex = flColumnIndex;

            if (null != flPo) {
                XSSFCell sgcell = row2.createCell(columnIndex++);
                if (flPo.getSgfl() != null) {
                    sgcell.setCellValue(flPo.getSgfl());
                    if (!TransactionTypeEnum.isFlReasonable(item.getFt(), item.getSubt(), TransactionTypeEnum.SG, flPo.getSgfl())) {
                        sgcell.setCellStyle(styleRed);
                    }
                }

                XSSFCell yzcell = row2.createCell(columnIndex++);
                if (flPo.getYzfl() != null) {
                    yzcell.setCellValue(flPo.getYzfl());
                    if (!TransactionTypeEnum.isFlReasonable(item.getFt(), item.getSubt(), TransactionTypeEnum.YZ, flPo.getYzfl())) {
                        yzcell.setCellStyle(styleRed);
                    }
                }

                XSSFCell shf1cell = row2.createCell(columnIndex++);
                if (flPo.getF1() != null) {
                    shf1cell.setCellValue(flPo.getF1());
                    shf1cell.setCellComment(createComment(wb, sheet, flPo.getC1()));
                }

                XSSFCell shf2cell = row2.createCell(columnIndex++);
                if (flPo.getF2() != null) {
                    shf2cell.setCellValue(flPo.getF2());
                    shf2cell.setCellComment(createComment(wb, sheet, flPo.getC2()));
                    if (!TransactionTypeEnum.isFlReasonable(item.getFt(), item.getSubt(), TransactionTypeEnum.SH, flPo.getF2())) {
                        shf2cell.setCellStyle(styleRed);
                    } else {
                        shf2cell.setCellStyle(stylegreen);
                    }
                }

                XSSFCell shf3cell = row2.createCell(columnIndex++);
                if (flPo.getF3() != null) {
                    shf3cell.setCellValue(flPo.getF3());
                    shf3cell.setCellComment(createComment(wb, sheet, flPo.getC3()));
                    if (!TransactionTypeEnum.isFlReasonable(item.getFt(), item.getSubt(), TransactionTypeEnum.SH, flPo.getF3())) {
                        shf3cell.setCellStyle(styleRed);
                    } else {
                        shf3cell.setCellStyle(stylegreen);
                    }
                }

                XSSFCell shf4cell = row2.createCell(columnIndex++);
                if (flPo.getF4() != null) {
                    shf4cell.setCellValue(flPo.getF4());
                    shf4cell.setCellComment(createComment(wb, sheet, flPo.getC4()));
                    if (!TransactionTypeEnum.isFlReasonable(item.getFt(), item.getSubt(), TransactionTypeEnum.SH, flPo.getF4())) {
                        shf4cell.setCellStyle(styleRed);
                    } else {
                        shf4cell.setCellStyle(stylegreen);
                    }
                }
            }

            //设置本行显示格式
            switch (item.getSubt()) {
                case "lc": {
                    cellCode.setCellStyle(stylegreen);
                    cellName.setCellStyle(stylegreen);
                }
                break;
                case "sc": {
                    cellCode.setCellStyle(styleSc);
                    cellName.setCellStyle(styleSc);
                }
                break;
                case "kz": {
                    cellCode.setCellStyle(styleKz);
                    cellName.setCellStyle(styleKz);
                }
                break;
                default:
                    break;
            }

        }
    }

    Comment createComment(XSSFWorkbook wb, XSSFSheet sheet, String content) {
        CreationHelper factory = wb.getCreationHelper();
        Drawing drawing = sheet.createDrawingPatriarch();
        ClientAnchor anchor = factory.createClientAnchor();
        Comment comment1 = drawing.createCellComment(anchor);
        comment1.setString(factory.createRichTextString(content));
        return comment1;
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
