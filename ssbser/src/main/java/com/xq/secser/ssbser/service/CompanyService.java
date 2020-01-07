package com.xq.secser.ssbser.service;

import com.xq.secser.provider.CompanyProvider;
import com.xq.secser.ssbser.pojo.po.CompPo;
import com.xq.secser.ssbser.pojo.po.IComp;
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
public class CompanyService {
    @Autowired
    CompanyProvider companyProvider;
    @Autowired
    SqlSessionFactory sqlSessionFactory;

    public void initCompany() {
        companyProvider.initCompany();
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
