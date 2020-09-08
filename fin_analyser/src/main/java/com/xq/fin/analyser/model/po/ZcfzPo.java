package com.xq.fin.analyser.model.po;

import lombok.Data;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 资产负债表
 */
@Entity
@ToString
@Data
@Table(name = "gp_zcvz")
public class ZcfzPo {
    @Id
    private String code;    //代码
    @Id
    private String time;    //时间：202003，202006

    double SUMLASSET;           //流动资产
    double MONETARYFUND;            //货币资金
    double ACCOUNTBILLREC;          //应收票据及应收账款
    double BILLREC;                     //其中:应收票据
    double ACCOUNTREC;                  //应收账款
    double ADVANCEPAY;             //预付款项
    double TOTAL_OTHER_RECE;       //其他应收款合计
    double DIVIDENDREC;                 //其中：应收股利
    double OTHERREC;                    //其他应收款
    double INVENTORY;              //存货
    double OTHERLASSET;            //其他流动资产
    double SUMNONLASSET;        //非流动资产合计
    double LTREC;                   //长期应收款
    double FIXEDASSET;              //固定资产
    double INTANGIBLEASSET;         //无形资产
    double LTDEFERASSET;            //长期待摊费用
    double DEFERINCOMETAXASSET;     //递延所得税资产
    double GOODWILL;                //商誉
    double SUMASSET;            //资产总计
    double SUMLLIAB;            //流动负债合计
    double STBORROW;                //短期借款
    double ACCOUNTPAY;              //应付票据及应付账款
    double ACCOUNTBILLPAY;          //其中:应付账款
    double SALARYPAY;               //应付职工薪酬
    double TAXPAY;                  //应交税费
    double TOTAL_OTHER_PAYABLE;     //其他应付款合计
    double INTERESTPAY;                 //其中:应付利息
    double DIVIDENDPAY;                 //应付股利
    double OTHERPAY;                    //其他应付款
    double NONLLIABONEYEAR;         //一年内到期的非流动负债
    double SUMNONLLIAB;         //非流动负债合计
    double LTACCOUNTPAY;            //长期应付款
    double SUMLIAB;             //负债合计
    double SUMSHEQUITY;         //股东权益合计
    double SHARECAPITAL;            //实收资本（或股本）
    double CAPITALRESERVE;          //资本公积
    double SURPLUSRESERVE;          //盈余公积
    double RETAINEDEARNING;         //非分配利润
    double SUMPARENTEQUITY;     //归属于母公司股东权益合计
    double SUMLIABSHEQUITY;     //负债和股东权益合计
}
