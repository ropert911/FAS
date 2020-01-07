package com.xq.secser.ssbser.service;

import com.xq.secser.ssbser.pojo.po.*;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Autowired
    FundHisService fundHisService;

    @Autowired
    SqlSessionFactory sqlSessionFactory;

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

        /**公司模型排名*/
        int topN = 30;
        /**公司成立要在这个时间点前，这样公司至少穿越了牛熊*/
        String esTime = "2014-06-06";

        //////////////////////////////////***********/////////////////
        /**公司筛选*/
        Map<String, CompPo> comMap = filterCompany(ft, esTime, topN);

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
        {
            /**判断哪些code没有季度数据：然后进行下载下载季度数据*/
            Set<String> existCodeSet = fundHisService.getExistCodes(foundCodeList).stream().collect(Collectors.toSet());
            List<String> notexistCodeList = foundCodeList.stream().filter(code -> !existCodeSet.contains(code)).collect(Collectors.toList());
            if (!notexistCodeList.isEmpty()) {
                List<FundQuarterPo> fqList = fundHisService.downLoadFoudQuartData(notexistCodeList);
                fundHisService.insertBatchQuarterHis(fqList);
                List<FundYearPo> fyList = fundHisService.downLoadFoudYearData(notexistCodeList);
                fundHisService.insertBatchYearHis(fyList);
            }
        }
        //获取季度数据筛选
        //获取季度数据
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
                    q1.getQuarter(), df.format(q1.getRank()),
                    q2.getQuarter(), df.format(q2.getRank()),
                    q3.getQuarter(), df.format(q3.getRank()),
                    q4.getQuarter(), df.format(q4.getRank()),
                    q5.getQuarter(), df.format(q5.getRank()),
                    q6.getQuarter(), df.format(q6.getRank()),
                    q7.getQuarter(), df.format(q7.getRank()),
                    q8.getQuarter(), df.format(q8.getRank())));
            System.out.println(String.format("        年度  %s=%s  %s=%s  %s=%s  %s=%s  %s=%s",
                    y1 != null ? y1.getYear() : "null", y1 != null ? df.format(y1.getRank()) : "null",
                    y2 != null ? y2.getYear() : "null", y2 != null ? df.format(y2.getRank()) : "null",
                    y3 != null ? y3.getYear() : "null", y3 != null ? df.format(y3.getRank()) : "null",
                    y4 != null ? y4.getYear() : "null", y4 != null ? df.format(y4.getRank()) : "null",
                    y5 != null ? y5.getYear() : "null", y5 != null ? df.format(y5.getRank()) : "null"));
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

    Map<String, CompPo> filterCompany(String[] ft, String esTime, int topN) {
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
