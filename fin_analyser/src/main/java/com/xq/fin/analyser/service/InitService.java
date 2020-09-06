package com.xq.fin.analyser.service;

import com.xq.fin.analyser.data.*;
import com.xq.fin.analyser.pojo.*;
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
    private ZcfzbUtil zcfzbUtil;  //资产负债
    @Autowired
    private LrbUtil lrbUtil;  //利润表
    @Autowired
    private XjllUtil xjllUtil;     //现金流量
    @Autowired
    private ZyzbUtil zyzbUtil;  //主要指标
    @Autowired
    private BfbUtil bfbUtil;  //百分比
    @Autowired
    private GloableData gloableData;

    @Override
    public void run(ApplicationArguments args) {
        String codes = "300746";
        String[] codsArray = codes.split(" ");
        for (String code : codsArray) {
            gloableData.allStockList.put(code, new SingleData());
            zcfzbUtil.getData(code);    //资产负债
            lrbUtil.getData(code);      //利润三
            xjllUtil.getData(code);      //现金流量
            zyzbUtil.getData(code);     //主要指标
            bfbUtil.getData(code);      //百分比
        }

        analyse();

        System.exit(0);
    }

    void analyse() {
        for (String code : gloableData.allStockList.keySet()) {
            logger.info("股票代码={} 名称={}  ==============", code, gloableData.allStockList.get(code).getLrbPoList().get(0).getSECURITYSHORTNAME());
            debtCapacityAnalyse(gloableData.allStockList.get(code).getZcfzPoList());
            zczbfxAnayAnalyse(gloableData.allStockList.get(code).getZcfzPoList());
            xjllAnalyse(gloableData.allStockList.get(code).getZcfzPoList(), gloableData.allStockList.get(code).getLrbPoList(), gloableData.allStockList.get(code).getXjllPoList());
            ylllAnalyse(gloableData.allStockList.get(code).getZcfzPoList(), gloableData.allStockList.get(code).getLrbPoList(),
                    gloableData.allStockList.get(code).getZyzbPoList(), gloableData.allStockList.get(code).getBfbPoList());
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
        logger.info("---短期偿债能力-现金流分析 时间={}", zcfzPo.getREPORTDATE());
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
        logger.info("---- 短期有息债/流动负债<=50%: {} 值={}% (短期有息债={}亿 短期借款={}亿 一年内到期的非流动负债={}亿 流动负债={}亿)",
                dqysz / zcfzPo.getSUMLLIAB() <= 0.5 ? "\033[36;4m" + "正常" + "\033[0m" : "\033[31;4m" + "注意" + "\033[0m",
                100 * dqysz / zcfzPo.getSUMLLIAB(),
                dqysz / 100000000, zcfzPo.getSTBORROW() / 100000000, zcfzPo.getNONLLIABONEYEAR() / 100000000, zcfzPo.getSUMLLIAB() / 100000000);
        logger.info("---- 现金比率=货币资金/短期有息债>70%:{} 值={}%  (货币资金={}亿 短期有息债={}亿",
                zcfzPo.getMONETARYFUND() / dqysz > 0.7 ? "\033[36;4m" + "正常" + "\033[0m" : "\033[31;4m" + "注意" + "\033[0m",
                100 * zcfzPo.getMONETARYFUND() / dqysz,
                zcfzPo.getMONETARYFUND() / 100000000, dqysz / 100000000);
        logger.info("---- 速动比率=（流动资产-存货）/流动负债>70%:{} 值={}%   (流动资产={}亿 存货={}亿 流动负债={}亿",
                (zcfzPo.getSUMLASSET() - zcfzPo.getINVENTORY()) / zcfzPo.getSUMLLIAB() > 0.7 ? "\033[36;4m" + "正常" + "\033[0m" : "\033[31;4m" + "注意" + "\033[0m",
                100 * (zcfzPo.getSUMLASSET() - zcfzPo.getINVENTORY()) / zcfzPo.getSUMLLIAB(),
                zcfzPo.getSUMLASSET() / 100000000, zcfzPo.getINVENTORY() / 100000000, zcfzPo.getSUMLLIAB() / 100000000);

        logger.info("---- 资产负债率<=75%: {}  (总负债={}亿 总资产={}亿)",
                zcfzPo.getSUMLIAB() / zcfzPo.getSUMASSET() <= 0.75 ? "\033[36;4m" + "正常" + "\033[0m" : "\033[31;4m" + "注意" + "\033[0m",
                zcfzPo.getSUMLIAB() / 100000000, zcfzPo.getSUMASSET() / 100000000);
    }

    //资产占比分析
    void zczbfxAnayAnalyse(List<ZcfzPo> zcfzPoList) {
        ZcfzPo zcfzPo = zcfzPoList.get(0);
        logger.info("---资产-负债占比分析 时间={}", zcfzPo.getREPORTDATE());

        logger.info("---- 货币资金/总资产>10%: {} 值={}% (货币资金={}亿 总资产={}亿)",
                zcfzPo.getMONETARYFUND() / zcfzPo.getSUMASSET() > 0.1 ? "\033[36;4m" + "正常" + "\033[0m" : "\033[31;4m" + "注意" + "\033[0m",
                100 * zcfzPo.getMONETARYFUND() / zcfzPo.getSUMASSET(),
                zcfzPo.getMONETARYFUND() / 100000000, zcfzPo.getSUMASSET() / 100000000);
        logger.info("---- 应收票据及应收账款/总资产<10%: {} 值={}% (应收票据及应收账款={}亿)",
                zcfzPo.getACCOUNTBILLREC() / zcfzPo.getSUMASSET() < 0.10 ? "\033[36;4m" + "正常" + "\033[0m" : "\033[31;4m" + "注意" + "\033[0m",
                100 * zcfzPo.getACCOUNTBILLREC() / zcfzPo.getSUMASSET(),
                zcfzPo.getACCOUNTBILLREC() / 100000000);
        logger.info("---- 存货/总资产<10%: {}  (存货={}亿) 存货/总资产={}%",
                zcfzPo.getINVENTORY() / zcfzPo.getSUMASSET() < 0.1 ? "\033[36;4m" + "正常" + "\033[0m" : "\033[31;4m" + "注意" + "\033[0m",
                zcfzPo.getINVENTORY() / 100000000,
                100 * zcfzPo.getINVENTORY() / zcfzPo.getSUMASSET());
        logger.info("---- 固定资产/总资产<25%: {}  (固定资产={}亿) 固定资产/总资产={}%",
                zcfzPo.getFIXEDASSET() / zcfzPo.getSUMASSET() < 0.25 ? "\033[36;4m" + "正常" + "\033[0m" : "\033[31;4m" + "注意" + "\033[0m",
                zcfzPo.getFIXEDASSET() / 100000000,
                100 * zcfzPo.getFIXEDASSET() / zcfzPo.getSUMASSET());
        logger.info("---- 无形资产/总资产<10%: {} 值={}% (无形资产={}亿)",
                zcfzPo.getINTANGIBLEASSET() / zcfzPo.getSUMASSET() < 0.10 ? "\033[36;4m" + "正常" + "\033[0m" : "\033[31;4m" + "注意" + "\033[0m",
                100 * zcfzPo.getINTANGIBLEASSET() / zcfzPo.getSUMASSET(),
                zcfzPo.getINTANGIBLEASSET() / 100000000);
        logger.info("---- 金融资产/总资产<25%: {} 值={}% (金融资产={}亿)",
                zcfzPo.getGOODWILL() / zcfzPo.getSUMASSET() < 0.25 ? "\033[36;4m" + "正常" + "\033[0m" : "\033[31;4m" + "注意" + "\033[0m",
                100 * zcfzPo.getGOODWILL() / zcfzPo.getSUMASSET(),
                zcfzPo.getGOODWILL() / 100000000);

        logger.info("---- 商誉/净资产<=15%: {} 值={}% (商誉={}亿 净资产={}亿)",
                zcfzPo.getGOODWILL() / zcfzPo.getSUMSHEQUITY() <= 0.15 ? "\033[36;4m" + "正常" + "\033[0m" : "\033[31;4m" + "注意" + "\033[0m",
                100 * zcfzPo.getGOODWILL() / zcfzPo.getSUMSHEQUITY(),
                zcfzPo.getGOODWILL() / 100000000, zcfzPo.getSUMSHEQUITY() / 100000000);
    }

    //现金流分析
    void xjllAnalyse(List<ZcfzPo> zcfzPoList, List<LrbPo> lrbPoList, List<XjllPo> xjllPoList) {
        ZcfzPo zcfzPo = zcfzPoList.get(0);  //资产负债
        LrbPo lrbPo = lrbPoList.get(0);     //利润表
        XjllPo xjllPo = xjllPoList.get(0);  //现金流量表
        logger.info("---现金流分析 时间={}", xjllPo.getREPORTDATE());

        logger.info("---- 经营活动产生的现金流量净额>0: {}  (经营活动产生的现金流量净额={}亿",
                xjllPo.getNETOPERATECASHFLOW() > 0 ? "\033[36;4m" + "正常" + "\033[0m" : "\033[31;4m" + "注意" + "\033[0m",
                xjllPo.getNETOPERATECASHFLOW() / 100000000);
        logger.info("---- 投资活动产生的现金流量净额>0: {}  (投资活动产生的现金流量净额={}亿",
                xjllPo.getNETINVCASHFLOW() > 0 ? "\033[36;4m" + "正常" + "\033[0m" : "\033[31;4m" + "注意" + "\033[0m",
                xjllPo.getNETINVCASHFLOW() / 100000000);
        logger.info("---- 筹资活动产生的现金流量净额<=0: {}  (筹资活动产生的现金流量净额={}亿",
                xjllPo.getNETFINACASHFLOW() <= 0 ? "\033[36;4m" + "正常" + "\033[0m" : "\033[31;4m" + "注意" + "\033[0m",
                xjllPo.getNETFINACASHFLOW() / 100000000);
        logger.info("---- 现金及现金等价物净增加额>0: {}  (现金及现金等价物净增加额={}亿",
                xjllPo.getNICASHEQUI() > 0 ? "\033[36;4m" + "正常" + "\033[0m" : "\033[31;4m" + "注意" + "\033[0m",
                xjllPo.getNICASHEQUI() / 100000000);
        logger.info("---- 期末现金及现金等价物余额>0: {}  (期末现金及现金等价物余额={}亿",
                xjllPo.getCASHEQUIENDING() > 0 ? "\033[36;4m" + "正常" + "\033[0m" : "\033[31;4m" + "注意" + "\033[0m",
                xjllPo.getCASHEQUIENDING() / 100000000);

        double dqysz = zcfzPo.getSTBORROW() + zcfzPo.getNONLLIABONEYEAR();  //短期有息债
        logger.info("---- 期末现金及现金等价物余额/短期有息债 > 75%: 值={}% {}  (期末现金及现金等价物余额={}亿 短期有息债={}亿",
                100 * xjllPo.getCASHEQUIENDING() / dqysz,
                xjllPo.getCASHEQUIENDING() / dqysz > 0.75 ? "\033[36;4m" + "正常" + "\033[0m" : "\033[31;4m" + "注意" + "\033[0m",
                xjllPo.getCASHEQUIENDING() / 100000000, dqysz / 100000000);


        logger.info("---- 经营活动产生的现金流量净额/净利润 > = 70%: {} 值={}% (经营活动产生的现金流量净额={}亿  净利润={}亿",
                xjllPo.getNETOPERATECASHFLOW() / lrbPo.getNETPROFIT() >= 0.7 ? "\033[36;4m" + "正常" + "\033[0m" : "\033[31;4m" + "注意" + "\033[0m",
                100 * xjllPo.getNETOPERATECASHFLOW() / lrbPo.getNETPROFIT(),
                xjllPo.getNETOPERATECASHFLOW() / 100000000, lrbPo.getNETPROFIT() / 100000000);
    }

    //盈利经营分析
    void ylllAnalyse(List<ZcfzPo> zcfzPoList, List<LrbPo> lrbPoList, List<ZyzbPo> zyzbPoList, List<BfbPo> bfbPoList) {
        ZcfzPo zcfzPoThis = zcfzPoList.get(0);
        ZcfzPo zcfzPoLast = null;
        LrbPo lrbPoThis = lrbPoList.get(0);
        LrbPo lrbPoLast = null;
        ZyzbPo zyzbPoThis = zyzbPoList.get(0);
        ZyzbPo zyzbPoLast = null;
        BfbPo bfbPoThis = bfbPoList.get(0);
        BfbPo bfbPoLast = null;

        String thisYear = zyzbPoThis.getDate().substring(0, 4);
        for (ZcfzPo tmp : zcfzPoList) {
            String lastYear = tmp.getREPORTDATE().substring(0, 4);
            if (!thisYear.equals(lastYear)) {
                zcfzPoLast = tmp;
                break;
            }
        }
        for (LrbPo tmp : lrbPoList) {
            String lastYear = tmp.getREPORTDATE().substring(0, 4);
            if (!thisYear.equals(lastYear)) {
                lrbPoLast = tmp;
                break;
            }
        }
        for (ZyzbPo tmp : zyzbPoList) {
            String lastYear = tmp.getDate().substring(0, 4);
            if (!thisYear.equals(lastYear)) {
                zyzbPoLast = tmp;
                break;
            }
        }
        for (BfbPo tmp : bfbPoList) {
            String lastYear = tmp.getDate().substring(0, 4);
            if (!thisYear.equals(lastYear)) {
                bfbPoLast = tmp;
                break;
            }
        }

        logger.info("---盈利经营分析 时间={}", zyzbPoThis.getDate());
        double yysrzz = zyzbPoThis.getYyzsrtbzz() / 100;    //经营收入增长


        logger.info("---- 营业收入增长={}%", yysrzz * 100);
        logger.info("---- 货币资金/上期 >=营业收入增长: {} 增长={}% (货币资金={}亿 上期={}亿",
                (zcfzPoThis.getMONETARYFUND() - zcfzPoLast.getMONETARYFUND()) / zcfzPoLast.getMONETARYFUND() >= yysrzz ? "\033[36;4m" + "正常" + "\033[0m" : "\033[31;4m" + "注意" + "\033[0m",
                100 * (zcfzPoThis.getMONETARYFUND() - zcfzPoLast.getMONETARYFUND()) / zcfzPoLast.getMONETARYFUND(),
                zcfzPoThis.getMONETARYFUND() / 100000000, zcfzPoLast.getMONETARYFUND() / 100000000);
        logger.info("---- 应收票据及应收账款/上期 <= 1.1*营业收入增长: {} 增长={}%  (应收票据及应收账款{}亿 上期={}亿",
                ((zcfzPoThis.getACCOUNTBILLREC() - zcfzPoLast.getACCOUNTBILLREC()) / zcfzPoLast.getACCOUNTBILLREC()) <= (1.1 * yysrzz) ? "\033[36;4m" + "正常" + "\033[0m" : "\033[31;4m" + "注意" + "\033[0m",
                (100 * (zcfzPoThis.getACCOUNTBILLREC() - zcfzPoLast.getACCOUNTBILLREC()) / zcfzPoLast.getACCOUNTBILLREC()),
                zcfzPoThis.getACCOUNTBILLREC() / 100000000, zcfzPoLast.getACCOUNTBILLREC() / 100000000);
        logger.info("---- 存货/上期<10% <= 1.1*营业收入增长: {}  (存货{}亿 上期={}亿)",
                ((zcfzPoThis.getINVENTORY() - zcfzPoLast.getINVENTORY()) / zcfzPoLast.getINVENTORY()) <= (1.1 * yysrzz) ? "\033[36;4m" + "正常" + "\033[0m" : "\033[31;4m" + "注意" + "\033[0m",
                zcfzPoThis.getINVENTORY() / 100000000, zcfzPoLast.getINVENTORY() / 100000000);
        logger.info("---- 固定资产/上期<10% <= 1.1*营业收入增长: {}  (固定资产{}亿 上期={}亿",
                ((zcfzPoThis.getFIXEDASSET() - zcfzPoLast.getFIXEDASSET()) / zcfzPoLast.getFIXEDASSET()) <= (1.1 * yysrzz) ? "\033[36;4m" + "正常" + "\033[0m" : "\033[31;4m" + "注意" + "\033[0m",
                zcfzPoThis.getFIXEDASSET() / 100000000, zcfzPoLast.getFIXEDASSET() / 100000000);
        logger.info("---- 无形资产/上期<10% <= 1.1*营业收入增长: {}  (无形资产{}亿 上期={}亿",
                ((zcfzPoThis.getINTANGIBLEASSET() - zcfzPoLast.getINTANGIBLEASSET()) / zcfzPoLast.getINTANGIBLEASSET()) <= (1.1 * yysrzz) ? "\033[36;4m" + "正常" + "\033[0m" : "\033[31;4m" + "注意" + "\033[0m",
                zcfzPoThis.getINTANGIBLEASSET() / 100000000, zcfzPoLast.getINTANGIBLEASSET() / 100000000);
        logger.info("---- 三费支出/上期 <=1.1*营业收入增长: {}  (三费支出{}亿 上期={}亿",
                ((lrbPoThis.getSALEEXP() + lrbPoThis.getMANAGEEXP() + lrbPoThis.getFINANCEEXP()) / (lrbPoLast.getSALEEXP() + lrbPoLast.getMANAGEEXP() + lrbPoLast.getFINANCEEXP()) - 1) <= (1.1 * yysrzz) ? "\033[36;4m" + "正常" + "\033[0m" : "\033[31;4m" + "注意" + "\033[0m",
                (lrbPoThis.getSALEEXP() + lrbPoThis.getMANAGEEXP() + lrbPoThis.getFINANCEEXP()) / 100000000,
                (lrbPoLast.getSALEEXP() + lrbPoLast.getMANAGEEXP() + lrbPoLast.getFINANCEEXP()) / 100000000);

        logger.info("---- 资产减值损失/营业收入<=20%: {} 值={}% (资产减值损失{}亿 营业收入={}亿",
                bfbPoThis.getZcjzss() / bfbPoThis.getYysr() < 0.2 ? "\033[36;4m" + "正常" + "\033[0m" : "\033[31;4m" + "注意" + "\033[0m",
                100 * bfbPoThis.getZcjzss() / bfbPoThis.getYysr(),
                bfbPoThis.getZcjzss() / 100000000, bfbPoThis.getYysr() / 100000000);


        logger.info("---- 加权净资产收益率 >= 7%: {}  (加权净资产收益率={}%",
                zyzbPoThis.getJqjzcsyl() >= 7 ? "\033[36;4m" + "正常" + "\033[0m" : "\033[31;4m" + "注意" + "\033[0m",
                zyzbPoThis.getJqjzcsyl());
    }
}
