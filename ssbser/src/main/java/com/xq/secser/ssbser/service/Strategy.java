package com.xq.secser.ssbser.service;

import com.xq.secser.ssbser.pojo.po.*;
import com.xq.secser.ssbser.utils.ConsoleTable;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        {
            Set<String> qokcodelist = new HashSet<>();
            //获取季度数据
            List<FundQuarterPo> fundQuartList = fundHisService.getQuarterDataByCode(foundCodeList);
            //找到平均季度排名在0.3以下的
            Map<String, List<FundQuarterPo>> fqListMap = fundQuartList.stream().collect(Collectors.groupingBy(FundQuarterPo::getCode));
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
        {
            Set<String> qokcodelist = new HashSet<>();
            //获取季度数据
            List<FundYearPo> fundYearList = fundHisService.getYearDataByCode(foundCodeList);

            //找到平均季度排名在0.3以下的
            Map<String, List<FundYearPo>> fqListMap = fundYearList.stream().collect(Collectors.groupingBy(FundYearPo::getCode));
            fqListMap.forEach((key, value) -> {
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
        printResult(rFoundPoResult, comMap);

    }

    private void printResult(List<FoundPo> rFoundPoResult, Map<String, CompPo> comMap) {
        for (FoundPo item : rFoundPoResult) {
            logger.info("code={} name={}, level={}, ly={}% l3y={}% 公司代号={},公司={}",
                    item.getCode(), item.getName(), item.getLevel(),
                    item.getL1y(), item.getL3y(),
                    item.getComcode(), comMap.get(item.getComcode()).getName());
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
