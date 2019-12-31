package com.xq.secser.secser.service;

import com.xq.secser.secser.model.FundTypeEnum;
import com.xq.secser.secser.pojo.po.*;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author sk-qianxiao
 * @date 2019/12/31
 */
@Service
public class InitService {
    private static Logger logger = LoggerFactory.getLogger(InitService.class);

    @Autowired
    FundService fundService;
    @Autowired
    ComService comService;

    @Autowired
    SqlSessionFactory sqlSessionFactory;


    public void initFoundData(boolean reInit) {
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

    public void initCompany(boolean initCompany) {
        if (!initCompany) {
            return;
        }

        comService.getAllCom(FundTypeEnum.GP);
        comService.getAllCom(FundTypeEnum.HH);
        comService.getAllCom(FundTypeEnum.ZQ);
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

    public void parseFund() {
        try (SqlSession session = sqlSessionFactory.openSession(true)) {
            List<FoundPo> foundPoList = fundService.parseFund();
            IFund iFund = session.getMapper(IFund.class);
            iFund.insertFundBatch(foundPoList);
        } finally {
        }
    }

    public void parseCompany() {
        comService.parseCompFile(FundTypeEnum.GP);
        comService.parseCompFile(FundTypeEnum.HH);
        comService.parseCompFile(FundTypeEnum.ZQ);
    }


}
