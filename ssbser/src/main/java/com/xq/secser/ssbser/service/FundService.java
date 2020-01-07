package com.xq.secser.ssbser.service;

import com.xq.secser.provider.CompanyProvider;
import com.xq.secser.provider.FundHisProvider;
import com.xq.secser.provider.FundProvider;
import com.xq.secser.ssbser.pojo.po.FoundPo;
import com.xq.secser.ssbser.pojo.po.IFund;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author sk-qianxiao
 * @date 2020/1/7
 */
@Service
public class FundService {
    @Autowired
    FundProvider fundProvider;
    @Autowired
    SqlSessionFactory sqlSessionFactory;

    public void initFoundData() {
        fundProvider.initFoundData();
    }
    public void parseFund() {
        List<FoundPo> foundPoList = fundProvider.parseFund();

        try (SqlSession session = sqlSessionFactory.openSession(true)) {
            IFund iFund = session.getMapper(IFund.class);
            iFund.insertFundBatch(foundPoList);
        } finally {
        }
    }
}
