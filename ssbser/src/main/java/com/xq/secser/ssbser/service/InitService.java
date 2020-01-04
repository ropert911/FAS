package com.xq.secser.ssbser.service;

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
import org.springframework.stereotype.Service;

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
        parseFund();

        //解析公司数据
        parseCompany();

        search();
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

    private void search() {
        try (SqlSession session = sqlSessionFactory.openSession(true)) {
            IFund iFund = session.getMapper(IFund.class);
            IComp iComp = session.getMapper(IComp.class);

            Map<String, CompPo> comMap = new HashMap<>();

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date d2014 = format.parse("2014-06-06");
            List<CompPo> compPoList = iComp.getTopN("gp", 20);
            compPoList.stream().filter(item -> item.getEstime().compareTo(d2014) < 0).forEach(item -> comMap.put(item.getComcode(), item));
            compPoList = iComp.getTopN("hh", 20);
            compPoList.stream().filter(item -> item.getEstime().compareTo(d2014) < 0).forEach(item -> comMap.put(item.getComcode(), item));


            List<FoundPo> foundPoResult = new ArrayList<>();
            List<FoundPo> foundPoList = iFund.getByLevel("gp", 3.5);
            foundPoList.forEach(item -> {
                if (null != comMap.get(item.getComcode())) {
                    foundPoResult.add(item);
                }
            });
            foundPoList = iFund.getByLevel("hh", 3.5);
            foundPoList.forEach(item -> {
                if (null != comMap.get(item.getComcode())) {
                    foundPoResult.add(item);
                }
            });

            //过滤
            List<FoundPo> filtrResult = foundPoResult.stream().filter(s -> s.getL1y() >= 30).filter(s -> s.getL3y() >= 50).collect(Collectors.toList());
            //去重
            List<FoundPo> unique = filtrResult.stream().collect(
                    Collectors.collectingAndThen(
                            Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(FoundPo::getCode))), ArrayList::new)
            );

            //排序
            unique.sort(Comparator.comparing(FoundPo::getL3y));
            Collections.reverse(unique);

            logger.info("size={}", unique.size());
            for (FoundPo item : unique) {
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
                and comp.ordernum<= 20      #基金公司规模在前20名
            group by code  order by l3y desc
             */
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }
}
