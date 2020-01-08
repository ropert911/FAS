package com.xq.secser.ssbser.service;

import com.xq.secser.provider.CompanyProvider;
import com.xq.secser.provider.FundHisProvider;
import com.xq.secser.provider.FundProvider;
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
 * @date 2020/1/7
 */
@Service
public class FundService {
    private static Logger logger = LoggerFactory.getLogger(FundService.class);
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
            int beginIndex = 0, step = 500, endIndex = 0;
            int len = foundPoList.size();
            while (beginIndex < len) {
                endIndex = beginIndex + 500;
                endIndex = endIndex > len ? len : endIndex;
                iFund.insertFundBatch(foundPoList.subList(beginIndex, endIndex));
                beginIndex += step;
            }
        } finally {
        }
    }

    public void getflinfo(String code){
        fundProvider.getflinfo(code);
    }
}
