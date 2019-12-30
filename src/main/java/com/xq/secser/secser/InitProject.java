package com.xq.secser.secser;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xq.secser.secser.model.FundTypeEnum;
import com.xq.secser.secser.model.PageInfo;
import com.xq.secser.secser.service.FundService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

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
    FundService fundService;


    @Override
    public void run(ApplicationArguments args) {
//        List<String> allData = fundService.getAllGPFund();
//        logger.info("data size = {}", allData.size());
//        allData.forEach(item -> logger.info("{}", item));
        String data = "003834,华夏能源革新股票,HXNYGXGP,2019-12-27,1.2080,1.2080,-0.4942,7.6649,15.3773,23.5174,25.4413,48.5855,11.1316,,48.4029,20.80,2017-06-07,1,20.8,1.50%,0.15%,1,0.15%,1,";

        String[] items = data.split(",");
        String code = items[0];
        String name = items[1];
        double l1y =  Double.valueOf(items[11]) / 100;
        double l2y =  Double.valueOf(items[12]) / 100;
        double l3y =  Double.valueOf(items[13]);
        double ty =  Double.valueOf(items[14]) / 100;
        double cy =  Double.valueOf(items[15]) / 100;


    }
}