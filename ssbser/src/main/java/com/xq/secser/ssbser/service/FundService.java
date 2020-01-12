package com.xq.secser.ssbser.service;

import com.xq.secser.provider.CompanyProvider;
import com.xq.secser.provider.FundHisProvider;
import com.xq.secser.provider.FundProvider;
import com.xq.secser.provider.tt.pojo.ITtFund;
import com.xq.secser.ssbser.pojo.po.FoundFlPo;
import com.xq.secser.ssbser.pojo.po.FoundPo;
import com.xq.secser.ssbser.pojo.po.IFund;
import com.xq.secser.ssbser.pojo.po.IFundFl;
import com.xq.secser.ssbser.pojo.vo.RedeemRate;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    public void initHbFoundData() {
        fundProvider.initHbFoundData();
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

    public void parseHbFund() {
        List<FoundPo> foundPoList = fundProvider.parseHbFund();

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

    public List<FoundFlPo> getflinfo(List<String> foundCodeList) {
        /**找到数据库里没有的*/
        List<String> noCodeList = getNotExistFlCode(foundCodeList);


        /**对没有的进行重新下载,并写入数据库*/
        List<FoundFlPo> flInfoList = downloadFlInfo(noCodeList);
        saveFlInfo(flInfoList);

        List<FoundFlPo> resultFlList = new ArrayList<>(8);
        {
            /**获取最新数据*/
            try (SqlSession session = sqlSessionFactory.openSession(true)) {
                IFundFl iFundFl = session.getMapper(IFundFl.class);
                resultFlList = iFundFl.getFlByCodeList(foundCodeList);
            } finally {
            }
            for (FoundFlPo fl : resultFlList) {
                logger.debug("最后得到的费率:{}", fl);
            }
        }

        return resultFlList;
    }

    /**
     * 得到没有费率的基金号
     *
     * @param foundCodeList
     * @return
     */
    private List<String> getNotExistFlCode(List<String> foundCodeList) {
        List<String> noCodeList = new ArrayList<>(4);
        try (SqlSession session = sqlSessionFactory.openSession(true)) {
            IFundFl iFundFl = session.getMapper(IFundFl.class);
            List<String> exCodeList = iFundFl.getExistCodeByCodeList(foundCodeList);
            logger.debug("存在的code={}", exCodeList);
            Set<String> exCodeSet = exCodeList.stream().collect(Collectors.toSet());
            noCodeList = foundCodeList.stream().filter(item -> !exCodeSet.contains(item)).collect(Collectors.toList());
            logger.debug("不存在的code={}", noCodeList);
        } finally {
        }
        return noCodeList;
    }

    /**
     * 下载费率数据并写入数据库
     *
     * @param foundCodeList
     */
    private List<FoundFlPo> downloadFlInfo(List<String> foundCodeList) {
        List<FoundFlPo> dlFlPoList = new ArrayList<>(4);
        for (String fcode : foundCodeList) {
            RedeemRate redeemRate = fundProvider.getflinfo(fcode);
            FoundFlPo foundFlPo = FoundFlPo.builder().code(fcode).zcgm(redeemRate.getZcgm())
                    .sgfl(redeemRate.getSgfl()).yzfl(redeemRate.getYzfl()).managefl(redeemRate.getManagefl()).tgfl(redeemRate.getTgfl())
                    .c1(redeemRate.getC1()).f1(redeemRate.getFl())
                    .c2(redeemRate.getC2()).f2(redeemRate.getF2())
                    .c3(redeemRate.getC3()).f3(redeemRate.getF3())
                    .c4(redeemRate.getC4()).f4(redeemRate.getF4())
                    .c5(redeemRate.getC5()).f5(redeemRate.getF5()).build();
            dlFlPoList.add(foundFlPo);
        }

        return dlFlPoList;
    }

    /**
     * 保存费率信息
     *
     * @param foundFlPoList
     */
    private void saveFlInfo(List<FoundFlPo> foundFlPoList) {
        if (!foundFlPoList.isEmpty()) {
            try (SqlSession session = sqlSessionFactory.openSession(true)) {
                IFundFl iFundFl = session.getMapper(IFundFl.class);

                int beginIndex = 0, step = 50, endIndex = 0;
                int len = foundFlPoList.size();
                while (beginIndex < len) {
                    endIndex = beginIndex + 50;
                    endIndex = endIndex > len ? len : endIndex;
                    iFundFl.batchInsert(foundFlPoList.subList(beginIndex, endIndex));
                    beginIndex += step;
                }
            } finally {
            }
        }
    }
}
