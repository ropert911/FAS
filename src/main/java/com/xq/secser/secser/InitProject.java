package com.xq.secser.secser;

import com.xq.secser.secser.model.FundTypeEnum;
import com.xq.secser.secser.pojo.po.FundDownPo;
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


    @Override
    public void run(ApplicationArguments args) {
        //默认不进行原数据的初始化
        fundInitService.initOrigData(false);

//        String code = items[0];
//        String name = items[1];
//        double l1y =  Double.valueOf(items[11]) / 100;
//        double l2y =  Double.valueOf(items[12]) / 100;
//        double l3y =  Double.valueOf(items[13]);
//        double ty =  Double.valueOf(items[14]) / 100;
//        double cy =  Double.valueOf(items[15]) / 100;
    }


}