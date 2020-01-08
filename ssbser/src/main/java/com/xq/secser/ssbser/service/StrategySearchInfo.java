package com.xq.secser.ssbser.service;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

/**
 * @author sk-qianxiao
 * @date 2020/1/8
 */
@Data
@Builder
@ToString
public class StrategySearchInfo {
    /**
     * excel标签页名
     */
    private String sheetName;
    /**
     * 类型
     */
    private String[] ft;
    /**
     * 子类型
     */
    private String[] subt;
    /**
     * 级别
     */
    private double foudLevel;
    /**
     * 近一年利率大于等于
     */
    private double l1y;
    /**
     * 近三年利率大于等于
     */
    private double l3y;

    /**
     * 近2年季度历史排名平均排名
     */
    private double qhisrank ;
    /**
     * 近5年年历史排名平均排名
     */
    private double yhisrank;

    /**
     * 公司类型
     */
    private String[] cft;
    /**
     * 公司规模排名
     */
    private int topN ;
    /**
     * 公司成立要在这个时间点前
     */
    private String esTime;

    public StrategySearchInfo(String sheetName, String[] ft, String[] subt, double foudLevel, double l1y, double l3y, double qhisrank, double yhisrank, String[] cft, int topN, String esTime) {
        this.ft = ft;
        this.subt = subt;
        this.foudLevel = foudLevel;
        this.l1y = l1y;
        this.l3y = l3y;
        this.qhisrank = qhisrank;
        this.yhisrank = yhisrank;
        this.cft = cft;
        this.topN = topN;
        this.esTime = esTime;
    }
}
