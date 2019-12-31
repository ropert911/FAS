package com.xq.secser.secser;

import com.xq.secser.secser.model.FundTypeEnum;
import com.xq.secser.secser.pojo.po.FoundPo;
import com.xq.secser.secser.pojo.po.FundDownPo;
import com.xq.secser.secser.pojo.po.IFund;
import com.xq.secser.secser.pojo.po.IFundDown;
import com.xq.secser.secser.service.FundInitService;
import com.xq.secser.secser.service.FundService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.Reader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author sk-qianxiao
 * @date 2019/11/6
 */
@Component
public class InitProject implements ApplicationRunner {
    private static Logger logger = LoggerFactory.getLogger(InitProject.class);

    @Autowired
    FundInitService fundInitService;

    @Value("${com.xq.secser.reinit}")
    private boolean reinit;

    @Autowired
    SqlSessionFactory sqlSessionFactory;


    @Override
    public void run(ApplicationArguments args) {
        //默认不进行原数据的初始化
        fundInitService.initOrigData(reinit);

        //把原因数据解析到数据结构表里
        fundInitService.parseOrigData();


    }


}