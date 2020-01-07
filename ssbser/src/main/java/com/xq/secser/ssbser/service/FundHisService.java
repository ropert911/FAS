package com.xq.secser.ssbser.service;

import com.xq.secser.provider.FundHisProvider;
import com.xq.secser.ssbser.pojo.po.FundQuarterPo;
import com.xq.secser.ssbser.pojo.po.FundYearPo;
import com.xq.secser.ssbser.pojo.po.IFundHistory;
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
public class FundHisService {
    @Autowired
    FundHisProvider fundHisProvider;
    @Autowired
    SqlSessionFactory sqlSessionFactory;

    List<FundQuarterPo> downLoadFoudQuartData(List<String> notexistCodeList){
        return fundHisProvider.downLoadFoudQuartData(notexistCodeList);
    }
    List<FundYearPo> downLoadFoudYearData(List<String> notexistCodeList){
        return fundHisProvider.downLoadFoudYearData(notexistCodeList);
    }
    public List<FundQuarterPo> getQuarterDataByCode(List<String> codeList) {
        List<FundQuarterPo> fqList = null;
        try (SqlSession session = sqlSessionFactory.openSession(true)) {
            IFundHistory iFundHistory = session.getMapper(IFundHistory.class);
            fqList = iFundHistory.getQuarterDataByCode(codeList);
        } finally {
        }
        return fqList;
    }

    public List<FundYearPo> getYearDataByCode(List<String> codeList) {
        List<FundYearPo> fyList = null;
        try (SqlSession session = sqlSessionFactory.openSession(true)) {
            IFundHistory iFundHistory = session.getMapper(IFundHistory.class);
            fyList = iFundHistory.getYearDataByCode(codeList);
        } finally {
        }
        return fyList;
    }


    public List<String> getExistCodes(List<String> codes) {
        List<String> existCodes = null;
        try (SqlSession session = sqlSessionFactory.openSession(true)) {
            IFundHistory iFundHistory = session.getMapper(IFundHistory.class);
            existCodes = iFundHistory.getExistCodes(codes);
        } finally {
        }

        return existCodes;
    }

    public void insertBatchQuarterHis(List<FundQuarterPo> fqList) {
        if (!fqList.isEmpty()) {
            try (SqlSession session = sqlSessionFactory.openSession(true)) {
                IFundHistory iFundHistory = session.getMapper(IFundHistory.class);
                iFundHistory.insertFundQuarterBatch(fqList);
            } finally {
            }
        }
    }

    public void insertBatchYearHis(List<FundYearPo> fyList) {
        if (!fyList.isEmpty()) {
            try (SqlSession session = sqlSessionFactory.openSession(true)) {
                IFundHistory iFundHistory = session.getMapper(IFundHistory.class);
                iFundHistory.insertFundYearBatch(fyList);
            } finally {
            }
        }
    }
}
