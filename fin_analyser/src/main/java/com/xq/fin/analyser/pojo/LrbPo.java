package com.xq.fin.analyser.pojo;

public class LrbPo {
    private String SECURITYCODE;    //代码
    private String SECURITYSHORTNAME; //名称
    private String REPORTDATE;      //时间
    double TOTALOPERATEREVE;            //营业总收入
    private double OPERATEREVE;             //营业收入
    private double TOTALOPERATEEXP;     //营业总成本
    private double OPERATEEXP;              //营业成本
    private double RDEXP;                   //研发费用
    private double OPERATETAX;              //营业税金及附加
    private double SALEEXP;                 //销售费用
    private double MANAGEEXP;               //管理费用
    private double FINANCEEXP;              //财务费用
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

    public String getSECURITYCODE() {
        return SECURITYCODE;
    }

    public void setSECURITYCODE(String SECURITYCODE) {
        this.SECURITYCODE = SECURITYCODE;
    }

    public String getSECURITYSHORTNAME() {
        return SECURITYSHORTNAME;
    }

    public void setSECURITYSHORTNAME(String SECURITYSHORTNAME) {
        this.SECURITYSHORTNAME = SECURITYSHORTNAME;
    }

    public String getREPORTDATE() {
        return REPORTDATE;
    }

    public void setREPORTDATE(String REPORTDATE) {
        this.REPORTDATE = REPORTDATE;
    }

    public double getTOTALOPERATEREVE() {
        return TOTALOPERATEREVE;
    }

    public void setTOTALOPERATEREVE(double TOTALOPERATEREVE) {
        this.TOTALOPERATEREVE = TOTALOPERATEREVE;
    }

    public double getOPERATEREVE() {
        return OPERATEREVE;
    }

    public void setOPERATEREVE(double OPERATEREVE) {
        this.OPERATEREVE = OPERATEREVE;
    }

    public double getTOTALOPERATEEXP() {
        return TOTALOPERATEEXP;
    }

    public void setTOTALOPERATEEXP(double TOTALOPERATEEXP) {
        this.TOTALOPERATEEXP = TOTALOPERATEEXP;
    }

    public double getOPERATEEXP() {
        return OPERATEEXP;
    }

    public void setOPERATEEXP(double OPERATEEXP) {
        this.OPERATEEXP = OPERATEEXP;
    }

    public double getRDEXP() {
        return RDEXP;
    }

    public void setRDEXP(double RDEXP) {
        this.RDEXP = RDEXP;
    }

    public double getOPERATETAX() {
        return OPERATETAX;
    }

    public void setOPERATETAX(double OPERATETAX) {
        this.OPERATETAX = OPERATETAX;
    }

    public double getSALEEXP() {
        return SALEEXP;
    }

    public void setSALEEXP(double SALEEXP) {
        this.SALEEXP = SALEEXP;
    }

    public double getMANAGEEXP() {
        return MANAGEEXP;
    }

    public void setMANAGEEXP(double MANAGEEXP) {
        this.MANAGEEXP = MANAGEEXP;
    }

    public double getFINANCEEXP() {
        return FINANCEEXP;
    }

    public void setFINANCEEXP(double FINANCEEXP) {
        this.FINANCEEXP = FINANCEEXP;
    }

    public double getOPERATEPROFIT() {
        return OPERATEPROFIT;
    }

    public void setOPERATEPROFIT(double OPERATEPROFIT) {
        this.OPERATEPROFIT = OPERATEPROFIT;
    }

    public double getNONOPERATEREVE() {
        return NONOPERATEREVE;
    }

    public void setNONOPERATEREVE(double NONOPERATEREVE) {
        this.NONOPERATEREVE = NONOPERATEREVE;
    }

    public double getNONOPERATEEXP() {
        return NONOPERATEEXP;
    }

    public void setNONOPERATEEXP(double NONOPERATEEXP) {
        this.NONOPERATEEXP = NONOPERATEEXP;
    }

    public double getSUMPROFIT() {
        return SUMPROFIT;
    }

    public void setSUMPROFIT(double SUMPROFIT) {
        this.SUMPROFIT = SUMPROFIT;
    }

    public double getINCOMETAX() {
        return INCOMETAX;
    }

    public void setINCOMETAX(double INCOMETAX) {
        this.INCOMETAX = INCOMETAX;
    }

    public double getNETPROFIT() {
        return NETPROFIT;
    }

    public void setNETPROFIT(double NETPROFIT) {
        this.NETPROFIT = NETPROFIT;
    }

    public double getPARENTNETPROFIT() {
        return PARENTNETPROFIT;
    }

    public void setPARENTNETPROFIT(double PARENTNETPROFIT) {
        this.PARENTNETPROFIT = PARENTNETPROFIT;
    }

    public double getKCFJCXSYJLR() {
        return KCFJCXSYJLR;
    }

    public void setKCFJCXSYJLR(double KCFJCXSYJLR) {
        this.KCFJCXSYJLR = KCFJCXSYJLR;
    }

    public double getBASICEPS() {
        return BASICEPS;
    }

    public void setBASICEPS(double BASICEPS) {
        this.BASICEPS = BASICEPS;
    }

    public double getDILUTEDEPS() {
        return DILUTEDEPS;
    }

    public void setDILUTEDEPS(double DILUTEDEPS) {
        this.DILUTEDEPS = DILUTEDEPS;
    }

    public double getSUMCINCOME() {
        return SUMCINCOME;
    }

    public void setSUMCINCOME(double SUMCINCOME) {
        this.SUMCINCOME = SUMCINCOME;
    }

    public double getPARENTCINCOME() {
        return PARENTCINCOME;
    }

    public void setPARENTCINCOME(double PARENTCINCOME) {
        this.PARENTCINCOME = PARENTCINCOME;
    }
}
