package com.xq.secser.secser.service;

import com.xq.secser.secser.model.FundTypeEnum;
import com.xq.secser.secser.pojo.po.FundDownPo;
import com.xq.secser.secser.pojo.po.IFundDown;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author sk-qianxiao
 * @date 2019/12/31
 */
@Service
public class FundInitService {
    private static Logger logger = LoggerFactory.getLogger(FundInitService.class);
    @Autowired
    FundService fundService;

    private static SqlSessionFactory sqlSessionFactory;
    private static Reader reader;

    static {
        try {
            reader = Resources.getResourceAsReader("mybatis/Configure.xml");
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void initOrigData(boolean reInit) {
        if (!reInit) {
            return;
        }

        FundTypeEnum fundTypeEnum = FundTypeEnum.GP;
        List<String> allData1 = fundService.getAllFund(fundTypeEnum);
        batchInsertDb(fundTypeEnum, allData1);

        fundTypeEnum = FundTypeEnum.HH;
        List<String> allData2 = fundService.getAllFund(fundTypeEnum);
        batchInsertDb(fundTypeEnum, allData2);

        fundTypeEnum = FundTypeEnum.ZQ;
        List<String> allData3 = fundService.getAllFund(fundTypeEnum);
        batchInsertDb(fundTypeEnum, allData3);
    }


    private void batchInsertDb(FundTypeEnum fundTypeEnum, List<String> allData) {
        List<FundDownPo> fundDownPoList = new ArrayList<>();
        allData.forEach(data -> {
            String[] items = data.split(",", 2);
            FundDownPo fundDownPo = FundDownPo.builder().code(items[0]).ft(fundTypeEnum.getUrlParam()).info(data).build();
            fundDownPoList.add(fundDownPo);
        });

        SqlSession session = sqlSessionFactory.openSession(true);
        try {
            IFundDown iFundDown = session.getMapper(IFundDown.class);
            iFundDown.insertFundBatch(fundDownPoList);
        } finally {
            session.close();
        }
    }
}
