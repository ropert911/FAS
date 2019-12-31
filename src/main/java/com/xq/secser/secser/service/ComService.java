package com.xq.secser.secser.service;

import com.xq.secser.secser.model.FundTypeEnum;
import com.xq.secser.secser.utils.FileWirter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;

/**
 * @author sk-qianxiao
 * @date 2019/12/31
 */
@Service
public class ComService {
    private static Logger logger = LoggerFactory.getLogger(ComService.class);

    //todo:有没有法根据平台设置不同的值，而不是不同的bean
    @Value("${com.xq.secser.download.path}")
    private String fileApath;
    private static RestTemplate restTemplate = new RestTemplate();

    public void getAllCom(FundTypeEnum ft) {
        String pattern = "http://fund.eastmoney.com/Company/home/gspmlist?fundType=%d";
        String url = String.format(pattern, ft.getIcode());

        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
        String data = responseEntity.getBody();
        data = data.replace("&nbsp;", "");

        FileWirter.writeFile(getFilePath(ft), data);
    }

    public String getFilePath(FundTypeEnum ft) {
        String fileFullName = fileApath + File.separator + ft.getUrlParam() + ".xml";
        logger.debug("file path={}", fileFullName);
        return fileFullName;
    }
}
