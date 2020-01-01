package com.xq.secser.ssbser.service;

import com.xq.secser.ssbser.pojo.po.CompPo;
import com.xq.secser.ssbser.pojo.po.FoundPo;
import com.xq.secser.ssbser.pojo.po.IComp;
import com.xq.secser.ssbser.pojo.po.IFund;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import java.util.List;

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
    }


    public void parseFund() {
        List<FoundPo> foundPoList = fundProvider.parseFund();

        try (SqlSession session = sqlSessionFactory.openSession(true)) {
            IFund iFund = session.getMapper(IFund.class);
            iFund.insertFundBatch(foundPoList);
        } finally {
        }
    }

    public void parseCompany() {
        List<CompPo> compPoList = companyProvider.parseCompany();

       try (SqlSession session = sqlSessionFactory.openSession(true)) {
           IComp iComp = session.getMapper(IComp.class);
           iComp.insertCompBatch(compPoList);
        } finally {
        }
    }
}
