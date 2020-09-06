package com.xq.fin.analyser.pojo;

//营业各项统计
public class BfbPo {
    String date;        //时间
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getYysr() {
        return yysr;
    }

    public void setYysr(double yysr) {
        this.yysr = yysr;
    }

    public double getYycb() {
        return yycb;
    }

    public void setYycb(double yycb) {
        this.yycb = yycb;
    }

    public double getYysjjfj() {
        return yysjjfj;
    }

    public void setYysjjfj(double yysjjfj) {
        this.yysjjfj = yysjjfj;
    }

    public double getQjfy() {
        return qjfy;
    }

    public void setQjfy(double qjfy) {
        this.qjfy = qjfy;
    }

    public double getXsfy() {
        return xsfy;
    }

    public void setXsfy(double xsfy) {
        this.xsfy = xsfy;
    }

    public double getGlfy() {
        return glfy;
    }

    public void setGlfy(double glfy) {
        this.glfy = glfy;
    }

    public double getCwfy() {
        return cwfy;
    }

    public void setCwfy(double cwfy) {
        this.cwfy = cwfy;
    }

    public double getZcjzss() {
        return zcjzss;
    }

    public void setZcjzss(double zcjzss) {
        this.zcjzss = zcjzss;
    }

    public double getQtjysy() {
        return qtjysy;
    }

    public void setQtjysy(double qtjysy) {
        this.qtjysy = qtjysy;
    }

    public double getGyjzbdsy() {
        return gyjzbdsy;
    }

    public void setGyjzbdsy(double gyjzbdsy) {
        this.gyjzbdsy = gyjzbdsy;
    }

    public double getTzsy() {
        return tzsy;
    }

    public void setTzsy(double tzsy) {
        this.tzsy = tzsy;
    }

    public double getYylr() {
        return yylr;
    }

    public void setYylr(double yylr) {
        this.yylr = yylr;
    }

    public double getYywsr() {
        return yywsr;
    }

    public void setYywsr(double yywsr) {
        this.yywsr = yywsr;
    }

    public double getBtsr() {
        return btsr;
    }

    public void setBtsr(double btsr) {
        this.btsr = btsr;
    }

    public double getYywzc() {
        return yywzc;
    }

    public void setYywzc(double yywzc) {
        this.yywzc = yywzc;
    }

    public double getLrze() {
        return lrze;
    }

    public void setLrze(double lrze) {
        this.lrze = lrze;
    }

    public double getSds() {
        return sds;
    }

    public void setSds(double sds) {
        this.sds = sds;
    }

    public double getJlr() {
        return jlr;
    }

    public void setJlr(double jlr) {
        this.jlr = jlr;
    }
}
