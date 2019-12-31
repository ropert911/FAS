package com.xq.secser.ssbser.service;

import com.xq.secser.ssbser.pojo.po.FoundPo;
import com.xq.secser.ssbser.pojo.po.IFund;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author sk-qianxiao
 * @date 2019/12/31
 */
@Service
public class InitService {
    private static Logger logger = LoggerFactory.getLogger(InitService.class);

    @Autowired
    FundProvider fundProvider;
    @Autowired
    CompanyProvider companyProvider;

    @Autowired
    SqlSessionFactory sqlSessionFactory;


    public void initFoundData(boolean reInit) {
        if (!reInit) {
            return;
        }

        fundProvider.initFoundData();
    }

    public void initCompany(boolean initCompany) {
        if (!initCompany) {
            return;
        }

        companyProvider.initCompany();
    }


    public void parseFund() {
        try (SqlSession session = sqlSessionFactory.openSession(true)) {
            List<FoundPo> foundPoList = fundProvider.parseFund();
            IFund iFund = session.getMapper(IFund.class);
            iFund.insertFundBatch(foundPoList);
        } finally {
        }
    }

    public void parseCompany() {
        companyProvider.parseCompany();
    }
}
