package com.xq.fin.analyser.pojo;

//主要指标
public class ZyzbPo {
    private String code;    //code
    private String date;    //时间
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getJbmgsy() {
        return jbmgsy;
    }

    public void setJbmgsy(double jbmgsy) {
        this.jbmgsy = jbmgsy;
    }

    public double getKfmgsy() {
        return kfmgsy;
    }

    public void setKfmgsy(double kfmgsy) {
        this.kfmgsy = kfmgsy;
    }

    public double getXsmgsy() {
        return xsmgsy;
    }

    public void setXsmgsy(double xsmgsy) {
        this.xsmgsy = xsmgsy;
    }

    public double getMgjzc() {
        return mgjzc;
    }

    public void setMgjzc(double mgjzc) {
        this.mgjzc = mgjzc;
    }

    public double getMggjj() {
        return mggjj;
    }

    public void setMggjj(double mggjj) {
        this.mggjj = mggjj;
    }

    public double getMgwfply() {
        return mgwfply;
    }

    public void setMgwfply(double mgwfply) {
        this.mgwfply = mgwfply;
    }

    public double getMgjyxjl() {
        return mgjyxjl;
    }

    public void setMgjyxjl(double mgjyxjl) {
        this.mgjyxjl = mgjyxjl;
    }

    public double getYyzsr() {
        return yyzsr;
    }

    public void setYyzsr(double yyzsr) {
        this.yyzsr = yyzsr;
    }

    public double getMlr() {
        return mlr;
    }

    public void setMlr(double mlr) {
        this.mlr = mlr;
    }

    public double getGsjlr() {
        return gsjlr;
    }

    public void setGsjlr(double gsjlr) {
        this.gsjlr = gsjlr;
    }

    public double getKfjlr() {
        return kfjlr;
    }

    public void setKfjlr(double kfjlr) {
        this.kfjlr = kfjlr;
    }

    public double getYyzsrtbzz() {
        return yyzsrtbzz;
    }

    public void setYyzsrtbzz(double yyzsrtbzz) {
        this.yyzsrtbzz = yyzsrtbzz;
    }

    public double getGsjlrtbzz() {
        return gsjlrtbzz;
    }

    public void setGsjlrtbzz(double gsjlrtbzz) {
        this.gsjlrtbzz = gsjlrtbzz;
    }

    public double getKfjlrtbzz() {
        return kfjlrtbzz;
    }

    public void setKfjlrtbzz(double kfjlrtbzz) {
        this.kfjlrtbzz = kfjlrtbzz;
    }

    public double getYyzsrgdhbzz() {
        return yyzsrgdhbzz;
    }

    public void setYyzsrgdhbzz(double yyzsrgdhbzz) {
        this.yyzsrgdhbzz = yyzsrgdhbzz;
    }

    public double getGsjlrgdhbzz() {
        return gsjlrgdhbzz;
    }

    public void setGsjlrgdhbzz(double gsjlrgdhbzz) {
        this.gsjlrgdhbzz = gsjlrgdhbzz;
    }

    public double getKfjlrgdhbzz() {
        return kfjlrgdhbzz;
    }

    public void setKfjlrgdhbzz(double kfjlrgdhbzz) {
        this.kfjlrgdhbzz = kfjlrgdhbzz;
    }

    public double getJqjzcsyl() {
        return jqjzcsyl;
    }

    public void setJqjzcsyl(double jqjzcsyl) {
        this.jqjzcsyl = jqjzcsyl;
    }

    public double getTbjzcsyl() {
        return tbjzcsyl;
    }

    public void setTbjzcsyl(double tbjzcsyl) {
        this.tbjzcsyl = tbjzcsyl;
    }

    public double getTbzzcsyl() {
        return tbzzcsyl;
    }

    public void setTbzzcsyl(double tbzzcsyl) {
        this.tbzzcsyl = tbzzcsyl;
    }

    public double getMll() {
        return mll;
    }

    public void setMll(double mll) {
        this.mll = mll;
    }

    public double getJll() {
        return jll;
    }

    public void setJll(double jll) {
        this.jll = jll;
    }

    public double getSjsl() {
        return sjsl;
    }

    public void setSjsl(double sjsl) {
        this.sjsl = sjsl;
    }

    public double getYskyysr() {
        return yskyysr;
    }

    public void setYskyysr(double yskyysr) {
        this.yskyysr = yskyysr;
    }

    public double getXsxjlyysr() {
        return xsxjlyysr;
    }

    public void setXsxjlyysr(double xsxjlyysr) {
        this.xsxjlyysr = xsxjlyysr;
    }

    public double getJyxjlyysr() {
        return jyxjlyysr;
    }

    public void setJyxjlyysr(double jyxjlyysr) {
        this.jyxjlyysr = jyxjlyysr;
    }

    public double getZzczzy() {
        return zzczzy;
    }

    public void setZzczzy(double zzczzy) {
        this.zzczzy = zzczzy;
    }

    public double getYszkzzts() {
        return yszkzzts;
    }

    public void setYszkzzts(double yszkzzts) {
        this.yszkzzts = yszkzzts;
    }

    public double getChzzts() {
        return chzzts;
    }

    public void setChzzts(double chzzts) {
        this.chzzts = chzzts;
    }

    public double getZcfzl() {
        return zcfzl;
    }

    public void setZcfzl(double zcfzl) {
        this.zcfzl = zcfzl;
    }

    public double getLdzczfz() {
        return ldzczfz;
    }

    public void setLdzczfz(double ldzczfz) {
        this.ldzczfz = ldzczfz;
    }

    public double getLdbl() {
        return ldbl;
    }

    public void setLdbl(double ldbl) {
        this.ldbl = ldbl;
    }

    public double getSdbl() {
        return sdbl;
    }

    public void setSdbl(double sdbl) {
        this.sdbl = sdbl;
    }
}
