package com.xq.secser.secser;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xq.secser.secser.model.FundTypeEnum;
import com.xq.secser.secser.model.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class SecserApplication {
    private static Logger logger = LoggerFactory.getLogger(SecserApplication.class);



    public static void main(String[] args) {
        SpringApplication.run(SecserApplication.class, args);
    }
}
