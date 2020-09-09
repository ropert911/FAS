package com.xq.fin.analyser.model.po;

import lombok.Data;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

//营业各项统计
@Entity
@ToString
@Data
@Table(name = "gp_bfb")
@IdClass(CodeTimeKey.class)
public class BfbPo {
    @Id
    private String code;        //代码
    @Id
    private String time;          //时间：202003，202006
    private double yysr;            //营业收入(元)
    private double yycb;            //营业成本(元)
    private double yysjjfj;             //营业税金及附加(元)
    private double qjfy;                //期间费用(元)
    private double xsfy;                    //销售费用(元)
    private double glfy;                    //管理费用(元)
    private double cwfy;                    //财务费用(元)
    private double zcjzss;              //资产减值损失(元)
    private double qtjysy;          //其他经营收益(元)
    private double gyjzbdsy;            //公允价值变动损益(元)
    private double tzsy;                //投资收益(元)
    private double yylr;            //营业利润(元)
    private double yywsr;               //加: 营业外收入(元)
    private double btsr;                //补贴收入(元)
    private double yywzc;               //减:营业外支出(元)
    private double lrze;            //利润总额(元)
    private double sds;                 //减:所得税(元)
    private double jlr;                 //净利润(元)
}
