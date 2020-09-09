package com.xq.fin.analyser.model.po;

import lombok.Data;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * 利润表
 */
@Entity
@ToString
@Data
@Table(name = "gp_lrb")
public class LrbPo implements Serializable {
    private static final long serialVersionUID = 3L;
    @Id
    private String code;    //代码
    @Id
    private String time;    //时间：202003，202006

    double TOTALOPERATEREVE;            //营业总收入
    private double OPERATEREVE;             //营业收入
    private double TOTALOPERATEEXP;     //营业总成本
    private double OPERATEEXP;              //营业成本
    private double RDEXP;                   //研发费用
    private double OPERATETAX;              //营业税金及附加
    private double SALEEXP;                 //销售费用
    private double MANAGEEXP;               //管理费用
    private double FINANCEEXP;              //财务费用
    //其它营业收益
    private double OPERATEPROFIT;       //营业利润
    private double NONOPERATEREVE;          //加:营业外收入
    private double NONOPERATEEXP;           //减:营业外支出
    private double SUMPROFIT;           //利润总额
    private double INCOMETAX;               //减:所得税费用
    private double NETPROFIT;           //净利润
    private double PARENTNETPROFIT;         //其中:归属于母公司股东的净利润
    private double KCFJCXSYJLR;             //扣除非经常性损益后的净利润
    private double BASICEPS;            //基本每股收益
    private double DILUTEDEPS;          //稀释每股收益
    private double SUMCINCOME;          //综合收益总额
    private double PARENTCINCOME;       //归属于母公司所有者的综合收益总额
}
