package com.xq.fin.analyser.service;

import com.xq.fin.analyser.model.SingleData;
import com.xq.fin.analyser.model.po.*;
import com.xq.fin.analyser.model.repository.*;
import com.xq.fin.analyser.tt.TTDataService;
import com.xq.fin.analyser.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class InitService implements ApplicationRunner {
    private static Logger logger = LoggerFactory.getLogger(InitService.class);

    @Autowired
    private TTDataService ttDataService;
    @Autowired
    private DataService dataService;
    @Autowired
    private GpAnalyser gpAnalyser;

    @Autowired
    private ZcfzRepository zcfzRepository;


    @Override
    public void run(ApplicationArguments args) {
        String codes = "605100";

        //获取数据
        String lastReportTime = StringUtil.getLastReportTime();
        String[] codsArray = codes.split(" ");
        for (String code : codsArray) {
            List<ZcfzPo> zcfzPoList = zcfzRepository.findByCodeAndTime(code, lastReportTime);
            if (CollectionUtils.isEmpty(zcfzPoList)) {
                SingleData singleData = ttDataService.getData(code);
                dataService.save(singleData);
            } else {
                logger.info("no need dowload {} {}", code, lastReportTime);
            }
        }


        //加载数据，进行单个指标的分析
        for (String code : codsArray) {
            SingleData singleData = dataService.loadData(code);
            gpAnalyser.analyse(singleData);
        }

        System.exit(0);
    }


}
