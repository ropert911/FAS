package com.xq.fin.analyser.model.po;

import lombok.Data;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

//主要指标
@Entity
@ToString
@Data
@Table(name = "gp_zyzb")
@IdClass(CodeTimeKey.class)
public class ZyzbPo {
    @Id
    private String code;        //代码
    @Id
    private String time;          //时间：202003，202006
                                    //每股指标
    private double jbmgsy;              //基本每股收益(元)
    private double kfmgsy;              //扣非每股收益(元)
    private double xsmgsy;              //稀释每股收益(元)
    private double mgjzc;               //每股净资产(元)
    private double mggjj;               //每股公积金(元)
    private double mgwfply;             //每股未分配利润(元)
    private double mgjyxjl;            //每股经营现金流(元)
                                    //成长能力
    private double yyzsr;               //营业总收入(元)
    private double mlr;                 //毛利润(元)
    private double gsjlr;               //归属净利润(元)
    private double kfjlr;               //扣非净利润(元)
    private double yyzsrtbzz;           //营业总收入同比增长(%)
    private double gsjlrtbzz;           //归属净利润同比增长(%)
    private double kfjlrtbzz;           //扣非净利润同比增长(%)
    private double yyzsrgdhbzz;         //营业总收入滚动环比增长(%)
    private double gsjlrgdhbzz;         //归属净利润滚动环比增长(%)
    private double kfjlrgdhbzz;         //扣非净利润滚动环比增长(%)
                                    //盈利能力
    private double jqjzcsyl;            //加权净资产收益率(%)
    private double tbjzcsyl;            //摊薄净资产收益率(%)
    private double tbzzcsyl;            //摊薄总资产收益率(%)
    private double mll;                 //毛利率(%)
    private double jll;                 //净利率(%)
    private double sjsl;                //实际税率(%)
                                    //盈利质量指标
    private double yskyysr;             //预收款/营业收入
    private double xsxjlyysr;           //销售现金流/营业收入
    private double jyxjlyysr;           //经营现金流/营业收入
                                    //运营能力指标
    private double zzczzy;              //总资产周转率(次)
    private double yszkzzts;            //应收账款周转天数(天)
    private double chzzts;              //存货周转天数(天)
                                    //财务风险指标
    private double zcfzl;               //资产负债率(%)
    private double ldzczfz;             //流动负债/总负债(%)
    private double ldbl;                //流动比率
    private double sdbl;                //速动比率
}
