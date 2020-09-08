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
        String codes = "300815";
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
            logger.info("股票代码={} 名称={}  ==============", code, gloableData.allStockList.get(code).getLrbVoList().get(0).getSECURITYSHORTNAME());
            debtCapacityAnalyse(gloableData.allStockList.get(code).getZcfzVoList());
            zczbfxAnayAnalyse(gloableData.allStockList.get(code).getZcfzVoList());
            xjllAnalyse(gloableData.allStockList.get(code).getZcfzVoList(), gloableData.allStockList.get(code).getLrbVoList(), gloableData.allStockList.get(code).getXjllVoList());
            ylllAnalyse(gloableData.allStockList.get(code).getZcfzVoList(), gloableData.allStockList.get(code).getLrbVoList(),
                    gloableData.allStockList.get(code).getZyzbVoList(), gloableData.allStockList.get(code).getBfbVoList());
//            for (ZcfzPo zcfzPo : gloableData.allStockList.get(code).getZcfzPoList()) {
//                logger.info("代码={} 时间={} 流动资产={} 货币资金={} 流动负债={} 应付票据及应付账款={} 其他应付款合计={}", zcfzPo.getSECURITYCODE(), zcfzPo.getREPORTDATE(),
//                        zcfzPo.getSUMLASSET(), zcfzPo.getMONETARYFUND(),
//                        zcfzPo.getSUMLLIAB(), zcfzPo.getACCOUNTPAY(), zcfzPo.getTOTAL_OTHER_PAYABLE());
//            }
        }
    }

    //短期偿债能力分析
    void debtCapacityAnalyse(List<ZcfzVo> zcfzVoList) {
        ZcfzVo zcfzVo = zcfzVoList.get(0);
        logger.info("---短期偿债能力-现金流分析 时间={}", zcfzVo.getREPORTDATE());
        logger.info("---- 受限货币资金/货币资金<10%：{}", "\033[32;4m" + "请查看报表文件，搜索货币资金" + "\033[0m");
        logger.info("---- 其他货币资金/货币资金<10%：{}", "\033[32;4m" + "请查看报表文件，搜索货币资金" + "\033[0m");
        logger.info("---- 货币资金*3>=流动负债: {} (货币资金={}亿 流动负债={}亿)",
                zcfzVo.getMONETARYFUND() * 3 >= zcfzVo.getSUMLLIAB() ? "\033[36;4m" + "正常" + "\033[0m" : "\033[31;4m" + "注意" + "\033[0m",
                zcfzVo.getMONETARYFUND() / 100000000, zcfzVo.getSUMLLIAB() / 100000000);
        logger.info("---- 货币资金占总资产的比重[5%,60%]: {}   (货币资金={}亿 总资产={}亿 比重={}%)",
                (zcfzVo.getMONETARYFUND() / zcfzVo.getSUMASSET() >= 0.05 && zcfzVo.getMONETARYFUND() / zcfzVo.getSUMASSET() <= 0.60) ? "\033[36;4m" + "正常" + "\033[0m" : "\033[31;4m" + "注意" + "\033[0m",
                zcfzVo.getMONETARYFUND() / 100000000, zcfzVo.getSUMASSET() / 100000000, 100 * zcfzVo.getMONETARYFUND() / zcfzVo.getSUMASSET());
        logger.info("---- 流动资产>=流动负债: {}  (流动资产={}亿 流动负债={}亿)",
                zcfzVo.getSUMLASSET() >= zcfzVo.getSUMLLIAB() ? "\033[36;4m" + "正常" + "\033[0m" : "\033[31;4m" + "注意" + "\033[0m",
                zcfzVo.getSUMLASSET() / 100000000, zcfzVo.getSUMLLIAB() / 100000000);
        double dqysz = zcfzVo.getSTBORROW() + zcfzVo.getNONLLIABONEYEAR();  //短期有息债
        logger.info("---- 短期有息债/流动负债<=50%: {} 值={}% (短期有息债={}亿 短期借款={}亿 一年内到期的非流动负债={}亿 流动负债={}亿)",
                dqysz / zcfzVo.getSUMLLIAB() <= 0.5 ? "\033[36;4m" + "正常" + "\033[0m" : "\033[31;4m" + "注意" + "\033[0m",
                100 * dqysz / zcfzVo.getSUMLLIAB(),
                dqysz / 100000000, zcfzVo.getSTBORROW() / 100000000, zcfzVo.getNONLLIABONEYEAR() / 100000000, zcfzVo.getSUMLLIAB() / 100000000);
        logger.info("---- 现金比率=货币资金/短期有息债>70%:{} 值={}%  (货币资金={}亿 短期有息债={}亿",
                zcfzVo.getMONETARYFUND() / dqysz > 0.7 ? "\033[36;4m" + "正常" + "\033[0m" : "\033[31;4m" + "注意" + "\033[0m",
                100 * zcfzVo.getMONETARYFUND() / dqysz,
                zcfzVo.getMONETARYFUND() / 100000000, dqysz / 100000000);
        logger.info("---- 速动比率=（流动资产-存货）/流动负债>70%:{} 值={}%   (流动资产={}亿 存货={}亿 流动负债={}亿",
                (zcfzVo.getSUMLASSET() - zcfzVo.getINVENTORY()) / zcfzVo.getSUMLLIAB() > 0.7 ? "\033[36;4m" + "正常" + "\033[0m" : "\033[31;4m" + "注意" + "\033[0m",
                100 * (zcfzVo.getSUMLASSET() - zcfzVo.getINVENTORY()) / zcfzVo.getSUMLLIAB(),
                zcfzVo.getSUMLASSET() / 100000000, zcfzVo.getINVENTORY() / 100000000, zcfzVo.getSUMLLIAB() / 100000000);

        logger.info("---- 资产负债率<=75%: {}  (总负债={}亿 总资产={}亿)",
                zcfzVo.getSUMLIAB() / zcfzVo.getSUMASSET() <= 0.75 ? "\033[36;4m" + "正常" + "\033[0m" : "\033[31;4m" + "注意" + "\033[0m",
                zcfzVo.getSUMLIAB() / 100000000, zcfzVo.getSUMASSET() / 100000000);
    }

    //资产占比分析
    void zczbfxAnayAnalyse(List<ZcfzVo> zcfzVoList) {
        ZcfzVo zcfzVo = zcfzVoList.get(0);
        logger.info("---资产-负债占比分析 时间={}", zcfzVo.getREPORTDATE());

        logger.info("---- 货币资金/总资产>10%: {} 值={}% (货币资金={}亿 总资产={}亿)",
                zcfzVo.getMONETARYFUND() / zcfzVo.getSUMASSET() > 0.1 ? "\033[36;4m" + "正常" + "\033[0m" : "\033[31;4m" + "注意" + "\033[0m",
                100 * zcfzVo.getMONETARYFUND() / zcfzVo.getSUMASSET(),
                zcfzVo.getMONETARYFUND() / 100000000, zcfzVo.getSUMASSET() / 100000000);
        logger.info("---- 应收票据及应收账款/总资产<10%: {} 值={}% (应收票据及应收账款={}亿)",
                zcfzVo.getACCOUNTBILLREC() / zcfzVo.getSUMASSET() < 0.10 ? "\033[36;4m" + "正常" + "\033[0m" : "\033[31;4m" + "注意" + "\033[0m",
                100 * zcfzVo.getACCOUNTBILLREC() / zcfzVo.getSUMASSET(),
                zcfzVo.getACCOUNTBILLREC() / 100000000);
        logger.info("---- 存货/总资产<10%: {} 值={}%  (存货={}亿)",
                zcfzVo.getINVENTORY() / zcfzVo.getSUMASSET() < 0.1 ? "\033[36;4m" + "正常" + "\033[0m" : "\033[31;4m" + "注意" + "\033[0m",
                100 * zcfzVo.getINVENTORY() / zcfzVo.getSUMASSET(),
                zcfzVo.getINVENTORY() / 100000000);
        logger.info("---- 固定资产/总资产<25%: {}  值={}% (固定资产={}亿)",
                zcfzVo.getFIXEDASSET() / zcfzVo.getSUMASSET() < 0.25 ? "\033[36;4m" + "正常" + "\033[0m" : "\033[31;4m" + "注意" + "\033[0m",
                100 * zcfzVo.getFIXEDASSET() / zcfzVo.getSUMASSET(),
                zcfzVo.getFIXEDASSET() / 100000000);
        logger.info("---- 无形资产/总资产<10%: {} 值={}% (无形资产={}亿)",
                zcfzVo.getINTANGIBLEASSET() / zcfzVo.getSUMASSET() < 0.10 ? "\033[36;4m" + "正常" + "\033[0m" : "\033[31;4m" + "注意" + "\033[0m",
                100 * zcfzVo.getINTANGIBLEASSET() / zcfzVo.getSUMASSET(),
                zcfzVo.getINTANGIBLEASSET() / 100000000);
        logger.info("---- 金融资产/总资产<25%: {} 值={}% (金融资产={}亿)",
                zcfzVo.getGOODWILL() / zcfzVo.getSUMASSET() < 0.25 ? "\033[36;4m" + "正常" + "\033[0m" : "\033[31;4m" + "注意" + "\033[0m",
                100 * zcfzVo.getGOODWILL() / zcfzVo.getSUMASSET(),
                zcfzVo.getGOODWILL() / 100000000);

        logger.info("---- 商誉/净资产<=15%: {} 值={}% (商誉={}亿 净资产={}亿)",
                zcfzVo.getGOODWILL() / zcfzVo.getSUMSHEQUITY() <= 0.15 ? "\033[36;4m" + "正常" + "\033[0m" : "\033[31;4m" + "注意" + "\033[0m",
                100 * zcfzVo.getGOODWILL() / zcfzVo.getSUMSHEQUITY(),
                zcfzVo.getGOODWILL() / 100000000, zcfzVo.getSUMSHEQUITY() / 100000000);
    }

    //现金流分析
    void xjllAnalyse(List<ZcfzVo> zcfzVoList, List<LrbVo> lrbVoList, List<XjllVo> xjllVoList) {
        ZcfzVo zcfzVo = zcfzVoList.get(0);  //资产负债
        LrbVo lrbVo = lrbVoList.get(0);     //利润表
        XjllVo xjllVo = xjllVoList.get(0);  //现金流量表
        logger.info("---现金流分析 时间={}", xjllVo.getREPORTDATE());

        logger.info("---- 经营活动产生的现金流量净额>0: {}  (经营活动产生的现金流量净额={}亿",
                xjllVo.getNETOPERATECASHFLOW() > 0 ? "\033[36;4m" + "正常" + "\033[0m" : "\033[31;4m" + "注意" + "\033[0m",
                xjllVo.getNETOPERATECASHFLOW() / 100000000);
        logger.info("---- 投资活动产生的现金流量净额>0: {}  (投资活动产生的现金流量净额={}亿",
                xjllVo.getNETINVCASHFLOW() > 0 ? "\033[36;4m" + "正常" + "\033[0m" : "\033[31;4m" + "注意" + "\033[0m",
                xjllVo.getNETINVCASHFLOW() / 100000000);
        logger.info("---- 筹资活动产生的现金流量净额<=0: {}  (筹资活动产生的现金流量净额={}亿",
                xjllVo.getNETFINACASHFLOW() <= 0 ? "\033[36;4m" + "正常" + "\033[0m" : "\033[31;4m" + "注意" + "\033[0m",
                xjllVo.getNETFINACASHFLOW() / 100000000);
        logger.info("---- 现金及现金等价物净增加额>0: {}  (现金及现金等价物净增加额={}亿",
                xjllVo.getNICASHEQUI() > 0 ? "\033[36;4m" + "正常" + "\033[0m" : "\033[31;4m" + "注意" + "\033[0m",
                xjllVo.getNICASHEQUI() / 100000000);
        logger.info("---- 期末现金及现金等价物余额>0: {}  (期末现金及现金等价物余额={}亿",
                xjllVo.getCASHEQUIENDING() > 0 ? "\033[36;4m" + "正常" + "\033[0m" : "\033[31;4m" + "注意" + "\033[0m",
                xjllVo.getCASHEQUIENDING() / 100000000);

        double dqysz = zcfzVo.getSTBORROW() + zcfzVo.getNONLLIABONEYEAR();  //短期有息债
        logger.info("---- 期末现金及现金等价物余额/短期有息债 > 75%: {} 值={}%  (期末现金及现金等价物余额={}亿 短期有息债={}亿",
                xjllVo.getCASHEQUIENDING() / dqysz > 0.75 ? "\033[36;4m" + "正常" + "\033[0m" : "\033[31;4m" + "注意" + "\033[0m",
                100 * xjllVo.getCASHEQUIENDING() / dqysz,
                xjllVo.getCASHEQUIENDING() / 100000000, dqysz / 100000000);


        logger.info("---- 经营活动产生的现金流量净额/净利润 > = 70%: {} 值={}% (经营活动产生的现金流量净额={}亿  净利润={}亿",
                xjllVo.getNETOPERATECASHFLOW() / lrbVo.getNETPROFIT() >= 0.7 ? "\033[36;4m" + "正常" + "\033[0m" : "\033[31;4m" + "注意" + "\033[0m",
                100 * xjllVo.getNETOPERATECASHFLOW() / lrbVo.getNETPROFIT(),
                xjllVo.getNETOPERATECASHFLOW() / 100000000, lrbVo.getNETPROFIT() / 100000000);
    }

    //盈利经营分析
    void ylllAnalyse(List<ZcfzVo> zcfzVoList, List<LrbVo> lrbVoList, List<ZyzbVo> zyzbVoList, List<BfbVo> bfbVoList) {
        ZcfzVo zcfzVoThis = zcfzVoList.get(0);
        ZcfzVo zcfzVoLast = null;
        LrbVo lrbVoThis = lrbVoList.get(0);
        LrbVo lrbVoLast = null;
        ZyzbVo zyzbVoThis = zyzbVoList.get(0);
        ZyzbVo zyzbVoLast = null;
        BfbVo bfbVoThis = bfbVoList.get(0);
        BfbVo bfbVoLast = null;

        String thisYear = zyzbVoThis.getDate().substring(0, 4);
        for (ZcfzVo tmp : zcfzVoList) {
            String lastYear = tmp.getREPORTDATE().substring(0, 4);
            if (!thisYear.equals(lastYear)) {
                zcfzVoLast = tmp;
                break;
            }
        }
        for (LrbVo tmp : lrbVoList) {
            String lastYear = tmp.getREPORTDATE().substring(0, 4);
            if (!thisYear.equals(lastYear)) {
                lrbVoLast = tmp;
                break;
            }
        }
        for (ZyzbVo tmp : zyzbVoList) {
            String lastYear = tmp.getDate().substring(0, 4);
            if (!thisYear.equals(lastYear)) {
                zyzbVoLast = tmp;
                break;
            }
        }
        for (BfbVo tmp : bfbVoList) {
            String lastYear = tmp.getDate().substring(0, 4);
            if (!thisYear.equals(lastYear)) {
                bfbVoLast = tmp;
                break;
            }
        }

        logger.info("---盈利经营分析 时间={}", zyzbVoThis.getDate());
        double yysrzz = zyzbVoThis.getYyzsrtbzz() / 100;    //经营收入增长


        logger.info("---- 营业收入同比增长={}%  扣非净利润同比增长={}%", yysrzz * 100, zyzbVoThis.getKfjlrtbzz());
        logger.info("---- 货币资金/上期 >=营业收入增长: {} 增长={}% (货币资金={}亿 上期={}亿",
                (zcfzVoThis.getMONETARYFUND() - zcfzVoLast.getMONETARYFUND()) / zcfzVoLast.getMONETARYFUND() >= yysrzz ? "\033[36;4m" + "正常" + "\033[0m" : "\033[31;4m" + "注意" + "\033[0m",
                100 * (zcfzVoThis.getMONETARYFUND() - zcfzVoLast.getMONETARYFUND()) / zcfzVoLast.getMONETARYFUND(),
                zcfzVoThis.getMONETARYFUND() / 100000000, zcfzVoLast.getMONETARYFUND() / 100000000);
        logger.info("---- 应收票据及应收账款/上期 <= 1.1*营业收入增长: {} 增长={}%  (应收票据及应收账款{}亿 上期={}亿",
                ((zcfzVoThis.getACCOUNTBILLREC() - zcfzVoLast.getACCOUNTBILLREC()) / zcfzVoLast.getACCOUNTBILLREC()) <= (1.1 * yysrzz) ? "\033[36;4m" + "正常" + "\033[0m" : "\033[31;4m" + "注意" + "\033[0m",
                (100 * (zcfzVoThis.getACCOUNTBILLREC() - zcfzVoLast.getACCOUNTBILLREC()) / zcfzVoLast.getACCOUNTBILLREC()),
                zcfzVoThis.getACCOUNTBILLREC() / 100000000, zcfzVoLast.getACCOUNTBILLREC() / 100000000);
        logger.info("---- 存货/上期<10% <= 1.1*营业收入增长: {} 值={}% (存货={}亿 上期={}亿)",
                ((zcfzVoThis.getINVENTORY() - zcfzVoLast.getINVENTORY()) / zcfzVoLast.getINVENTORY()) <= (1.1 * yysrzz) ? "\033[36;4m" + "正常" + "\033[0m" : "\033[31;4m" + "注意" + "\033[0m",
                100 * ((zcfzVoThis.getINVENTORY() - zcfzVoLast.getINVENTORY()) / zcfzVoLast.getINVENTORY()),
                zcfzVoThis.getINVENTORY() / 100000000, zcfzVoLast.getINVENTORY() / 100000000);
        logger.info("---- 固定资产/上期<10% <= 1.1*营业收入增长: {} 值={}% (固定资产{}亿 上期={}亿",
                ((zcfzVoThis.getFIXEDASSET() - zcfzVoLast.getFIXEDASSET()) / zcfzVoLast.getFIXEDASSET()) <= (1.1 * yysrzz) ? "\033[36;4m" + "正常" + "\033[0m" : "\033[31;4m" + "注意" + "\033[0m",
                100 * ((zcfzVoThis.getFIXEDASSET() - zcfzVoLast.getFIXEDASSET()) / zcfzVoLast.getFIXEDASSET()),
                zcfzVoThis.getFIXEDASSET() / 100000000, zcfzVoLast.getFIXEDASSET() / 100000000);
        logger.info("---- 无形资产/上期<10% <= 1.1*营业收入增长: {} 值={}%  (无形资产{}亿 上期={}亿",
                ((zcfzVoThis.getINTANGIBLEASSET() - zcfzVoLast.getINTANGIBLEASSET()) / zcfzVoLast.getINTANGIBLEASSET()) <= (1.1 * yysrzz) ? "\033[36;4m" + "正常" + "\033[0m" : "\033[31;4m" + "注意" + "\033[0m",
                100 * ((zcfzVoThis.getINTANGIBLEASSET() - zcfzVoLast.getINTANGIBLEASSET()) / zcfzVoLast.getINTANGIBLEASSET()),
                zcfzVoThis.getINTANGIBLEASSET() / 100000000, zcfzVoLast.getINTANGIBLEASSET() / 100000000);
        logger.info("---- 三费支出/上期 <=1.1*营业收入增长: {}  值={}% (三费支出{}亿 上期={}亿",
                ((lrbVoThis.getSALEEXP() + lrbVoThis.getMANAGEEXP() + lrbVoThis.getFINANCEEXP()) / (lrbVoLast.getSALEEXP() + lrbVoLast.getMANAGEEXP() + lrbVoLast.getFINANCEEXP()) - 1) <= (1.1 * yysrzz) ? "\033[36;4m" + "正常" + "\033[0m" : "\033[31;4m" + "注意" + "\033[0m",
                100 * ((lrbVoThis.getSALEEXP() + lrbVoThis.getMANAGEEXP() + lrbVoThis.getFINANCEEXP()) / (lrbVoLast.getSALEEXP() + lrbVoLast.getMANAGEEXP() + lrbVoLast.getFINANCEEXP()) - 1),
                (lrbVoThis.getSALEEXP() + lrbVoThis.getMANAGEEXP() + lrbVoThis.getFINANCEEXP()) / 100000000,
                (lrbVoLast.getSALEEXP() + lrbVoLast.getMANAGEEXP() + lrbVoLast.getFINANCEEXP()) / 100000000);

        logger.info("---- 资产减值损失/营业收入<=20%: {} 值={}% (资产减值损失{}亿 营业收入={}亿",
                bfbVoThis.getZcjzss() / bfbVoThis.getYysr() < 0.2 ? "\033[36;4m" + "正常" + "\033[0m" : "\033[31;4m" + "注意" + "\033[0m",
                100 * bfbVoThis.getZcjzss() / bfbVoThis.getYysr(),
                bfbVoThis.getZcjzss() / 100000000, bfbVoThis.getYysr() / 100000000);


        logger.info("---- 加权净资产收益率 >= 7%: {}  值={}%",
                zyzbVoThis.getJqjzcsyl() >= 7 ? "\033[36;4m" + "正常" + "\033[0m" : "\033[31;4m" + "注意" + "\033[0m",
                zyzbVoThis.getJqjzcsyl());
    }
}
