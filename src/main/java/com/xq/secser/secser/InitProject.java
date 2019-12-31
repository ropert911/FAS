package com.xq.secser.secser;

import com.xq.secser.secser.service.InitService;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * @author sk-qianxiao
 * @date 2019/11/6
 */
@Component
public class InitProject implements ApplicationRunner {
    private static Logger logger = LoggerFactory.getLogger(InitProject.class);

    @Autowired
    InitService initService;

    @Value("${com.xq.secser.download.fund}")
    private boolean bDowloadFound;

    @Value("${com.xq.secser.download.company}")
    private boolean bDowloadCompany;

    @Autowired
    SqlSessionFactory sqlSessionFactory;

    @Override
    public void run(ApplicationArguments args) {
        //TODO:添加时间统计
        //下载found数据
        initService.initFoundData(bDowloadFound);

        //下载com数据
        initService.initCompany(bDowloadCompany);

        //把foud原始数据解析到数据结构表里
        initService.parseOrigData();
    }


}