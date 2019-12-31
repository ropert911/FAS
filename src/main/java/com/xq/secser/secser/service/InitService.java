package com.xq.secser.secser.service;

import com.xq.secser.secser.model.FundTypeEnum;
import com.xq.secser.secser.pojo.po.FoundPo;
import com.xq.secser.secser.pojo.po.FundDownPo;
import com.xq.secser.secser.pojo.po.IFund;
import com.xq.secser.secser.pojo.po.IFundDown;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public void parseOrigData() {
        SqlSession session = sqlSessionFactory.openSession(true);
        try {
            LocalDate localDate = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            List<FoundPo> foundPoList = new ArrayList<>();
            IFundDown iFundDown = session.getMapper(IFundDown.class);
            List<FundDownPo> fundDownPoList = iFundDown.getAll();
            fundDownPoList.forEach(data -> {
                String[] items = data.getInfo().split(",");
                String code = items[0];
                String name = items[1];
                String date = items[3];
                if (date.length() == 0) {
                    date = localDate.format(formatter);
                }
                Double l1y = items[11].length() == 0 ? null : Double.valueOf(items[11]);
                Double l2y = items[12].length() == 0 ? null : Double.valueOf(items[12]);
                Double l3y = items[13].length() == 0 ? null : Double.valueOf(items[13]);
                Double ty = items[14].length() == 0 ? null : Double.valueOf(items[14]);
                Double cy = items[15].length() == 0 ? null : Double.valueOf(items[15]);
                FoundPo foundPo = FoundPo.builder().code(code).name(name).ft(data.getFt()).date(date).l1y(l1y).l2y(l2y).l3y(l3y).ty(ty).cy(cy).build();
                foundPoList.add(foundPo);
            });

            IFund iFund = session.getMapper(IFund.class);
            iFund.insertFundBatch(foundPoList);
        } finally {
            session.close();
        }
    }
}
