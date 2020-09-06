package com.xq.fin.analyser.service;

import com.xq.fin.analyser.data.GloableData;
import com.xq.fin.analyser.data.SingleData;
import com.xq.fin.analyser.data.ZcfzbUtil;
import com.xq.fin.analyser.pojo.ZcfzPo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InitService implements ApplicationRunner {
    private static Logger logger = LoggerFactory.getLogger(InitService.class);
    @Autowired
    private ZcfzbUtil zcfzbUtil;
    @Autowired
    private GloableData gloableData;

    @Override
    public void run(ApplicationArguments args) {
        String code = "605168";
        gloableData.allStockList.put(code, new SingleData());
        zcfzbUtil.getData(code);

        analyse();

        System.exit(0);
    }

    void analyse() {
        for (String code : gloableData.allStockList.keySet()) {
            logger.info("股票代码={} ==============", code);
            debtCapacityAnalyse(gloableData.allStockList.get(code).getZcfzPoList());
//            for (ZcfzPo zcfzPo : gloableData.allStockList.get(code).getZcfzPoList()) {
//                logger.info("代码={} 时间={} 流动资产={} 货币资金={} 流动负债={} 应付票据及应付账款={} 其他应付款合计={}", zcfzPo.getSECURITYCODE(), zcfzPo.getREPORTDATE(),
//                        zcfzPo.getSUMLASSET(), zcfzPo.getMONETARYFUND(),
//                        zcfzPo.getSUMLLIAB(), zcfzPo.getACCOUNTPAY(), zcfzPo.getTOTAL_OTHER_PAYABLE());
//            }
        }
    }

    //短期偿债能力分析
    void debtCapacityAnalyse(List<ZcfzPo> zcfzPoList) {
        ZcfzPo zcfzPo = zcfzPoList.get(0);
        logger.info("---短期偿债能力-现金流分析 {}", zcfzPo.getREPORTDATE());
        logger.info("---- 受限货币资金/货币资金<10%：{}", "\033[32;4m" + "请查看报表文件，搜索货币资金" + "\033[0m");
        logger.info("---- 其他货币资金/货币资金<10%：{}", "\033[32;4m" + "请查看报表文件，搜索货币资金" + "\033[0m");
        logger.info("---- 货币资金*3>=流动负债: {} (货币资金={}亿 流动负债={}亿)",
                zcfzPo.getMONETARYFUND() * 3 >= zcfzPo.getSUMLLIAB() ? "\033[36;4m" + "正常" + "\033[0m" : "\033[31;4m" + "注意" + "\033[0m",
                zcfzPo.getMONETARYFUND() / 100000000, zcfzPo.getSUMLLIAB() / 100000000);
        logger.info("---- 货币资金占总资产的比重[5%,60%]: {}   (货币资金={}亿 总资产={}亿 比重={}%)",
                (zcfzPo.getMONETARYFUND() / zcfzPo.getSUMASSET() >= 0.05 && zcfzPo.getMONETARYFUND() / zcfzPo.getSUMASSET() <= 0.60) ? "\033[36;4m" + "正常" + "\033[0m" : "\033[31;4m" + "注意" + "\033[0m",
                zcfzPo.getMONETARYFUND() / 100000000, zcfzPo.getSUMASSET() / 100000000, 100 * zcfzPo.getMONETARYFUND() / zcfzPo.getSUMASSET());
        logger.info("---- 流动资产>=流动负债: {}  (流动资产={}亿 流动负债={}亿)",
                zcfzPo.getSUMLASSET() >= zcfzPo.getSUMLLIAB() ? "\033[36;4m" + "正常" + "\033[0m" : "\033[31;4m" + "注意" + "\033[0m",
                zcfzPo.getSUMLASSET() / 100000000, zcfzPo.getSUMLLIAB() / 100000000);
        double dqysz = zcfzPo.getSTBORROW() + zcfzPo.getNONLLIABONEYEAR();  //短期有息债
        logger.info("---- 短期有息债/流动负债<=50%: {}  (短期有息债={}亿 短期借款={}亿 一年内到期的非流动负债={}亿 流动负债={}亿)",
                dqysz / zcfzPo.getSUMLLIAB() <= 0.5 ? "\033[36;4m" + "正常" + "\033[0m" : "\033[31;4m" + "注意" + "\033[0m",
                dqysz / 100000000, zcfzPo.getSTBORROW() / 100000000, zcfzPo.getNONLLIABONEYEAR() / 100000000, zcfzPo.getSUMLLIAB() / 100000000);
        logger.info("---- 现金比率=货币资金/短期有息债>70%:{}   (货币资金={}亿 短期有息债={}亿",
                zcfzPo.getMONETARYFUND() / dqysz > 0.7 ? "\033[36;4m" + "正常" + "\033[0m" : "\033[31;4m" + "注意" + "\033[0m",
                zcfzPo.getMONETARYFUND() / 100000000, dqysz / 100000000);
        logger.info("---- 速动比率=（流动资产-存货）/流动负债>70%:{}   (流动资产={}亿 存货={}亿 流动负债={}亿",
                (zcfzPo.getSUMLASSET() - zcfzPo.getINVENTORY()) / zcfzPo.getSUMLLIAB() > 0.7 ? "\033[36;4m" + "正常" + "\033[0m" : "\033[31;4m" + "注意" + "\033[0m",
                zcfzPo.getSUMLASSET() / 100000000, zcfzPo.getINVENTORY() / 100000000, zcfzPo.getSUMLLIAB() / 100000000);

        logger.info("---- 资产负债率<=75%: {}  (总负债={}亿 总资产={}亿)",
                zcfzPo.getSUMLIAB() / zcfzPo.getSUMASSET() <= 0.75 ? "\033[36;4m" + "正常" + "\033[0m" : "\033[31;4m" + "注意" + "\033[0m",
                zcfzPo.getSUMLIAB() / 100000000, zcfzPo.getSUMASSET() / 100000000);
    }
}
