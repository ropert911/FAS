package com.xq.fin.analyser.pojo;

//资产负债
public class ZcfzVo {
    String SECURITYCODE;        //代码
    String REPORTDATE;          //时间：202003，202006
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

    public String getSECURITYCODE() {
        return SECURITYCODE;
    }

    public void setSECURITYCODE(String SECURITYCODE) {
        this.SECURITYCODE = SECURITYCODE;
    }

    public String getREPORTDATE() {
        return REPORTDATE;
    }

    public void setREPORTDATE(String REPORTDATE) {
        this.REPORTDATE = REPORTDATE;
    }

    public double getSUMLASSET() {
        return SUMLASSET;
    }

    public void setSUMLASSET(double SUMLASSET) {
        this.SUMLASSET = SUMLASSET;
    }

    public double getMONETARYFUND() {
        return MONETARYFUND;
    }

    public void setMONETARYFUND(double MONETARYFUND) {
        this.MONETARYFUND = MONETARYFUND;
    }

    public double getACCOUNTBILLREC() {
        return ACCOUNTBILLREC;
    }

    public void setACCOUNTBILLREC(double ACCOUNTBILLREC) {
        this.ACCOUNTBILLREC = ACCOUNTBILLREC;
    }

    public double getBILLREC() {
        return BILLREC;
    }

    public void setBILLREC(double BILLREC) {
        this.BILLREC = BILLREC;
    }

    public double getACCOUNTREC() {
        return ACCOUNTREC;
    }

    public void setACCOUNTREC(double ACCOUNTREC) {
        this.ACCOUNTREC = ACCOUNTREC;
    }

    public double getADVANCEPAY() {
        return ADVANCEPAY;
    }

    public void setADVANCEPAY(double ADVANCEPAY) {
        this.ADVANCEPAY = ADVANCEPAY;
    }

    public double getTOTAL_OTHER_RECE() {
        return TOTAL_OTHER_RECE;
    }

    public void setTOTAL_OTHER_RECE(double TOTAL_OTHER_RECE) {
        this.TOTAL_OTHER_RECE = TOTAL_OTHER_RECE;
    }

    public double getDIVIDENDREC() {
        return DIVIDENDREC;
    }

    public void setDIVIDENDREC(double DIVIDENDREC) {
        this.DIVIDENDREC = DIVIDENDREC;
    }

    public double getOTHERREC() {
        return OTHERREC;
    }

    public void setOTHERREC(double OTHERREC) {
        this.OTHERREC = OTHERREC;
    }

    public double getINVENTORY() {
        return INVENTORY;
    }

    public void setINVENTORY(double INVENTORY) {
        this.INVENTORY = INVENTORY;
    }

    public double getOTHERLASSET() {
        return OTHERLASSET;
    }

    public void setOTHERLASSET(double OTHERLASSET) {
        this.OTHERLASSET = OTHERLASSET;
    }

    public double getSUMNONLASSET() {
        return SUMNONLASSET;
    }

    public void setSUMNONLASSET(double SUMNONLASSET) {
        this.SUMNONLASSET = SUMNONLASSET;
    }

    public double getLTREC() {
        return LTREC;
    }

    public void setLTREC(double LTREC) {
        this.LTREC = LTREC;
    }

    public double getFIXEDASSET() {
        return FIXEDASSET;
    }

    public void setFIXEDASSET(double FIXEDASSET) {
        this.FIXEDASSET = FIXEDASSET;
    }

    public double getINTANGIBLEASSET() {
        return INTANGIBLEASSET;
    }

    public void setINTANGIBLEASSET(double INTANGIBLEASSET) {
        this.INTANGIBLEASSET = INTANGIBLEASSET;
    }

    public double getLTDEFERASSET() {
        return LTDEFERASSET;
    }

    public void setLTDEFERASSET(double LTDEFERASSET) {
        this.LTDEFERASSET = LTDEFERASSET;
    }

    public double getDEFERINCOMETAXASSET() {
        return DEFERINCOMETAXASSET;
    }

    public void setDEFERINCOMETAXASSET(double DEFERINCOMETAXASSET) {
        this.DEFERINCOMETAXASSET = DEFERINCOMETAXASSET;
    }

    public double getSUMASSET() {
        return SUMASSET;
    }

    public void setSUMASSET(double SUMASSET) {
        this.SUMASSET = SUMASSET;
    }

    public double getSUMLLIAB() {
        return SUMLLIAB;
    }

    public void setSUMLLIAB(double SUMLLIAB) {
        this.SUMLLIAB = SUMLLIAB;
    }

    public double getSTBORROW() {
        return STBORROW;
    }

    public void setSTBORROW(double STBORROW) {
        this.STBORROW = STBORROW;
    }

    public double getACCOUNTPAY() {
        return ACCOUNTPAY;
    }

    public void setACCOUNTPAY(double ACCOUNTPAY) {
        this.ACCOUNTPAY = ACCOUNTPAY;
    }

    public double getACCOUNTBILLPAY() {
        return ACCOUNTBILLPAY;
    }

    public void setACCOUNTBILLPAY(double ACCOUNTBILLPAY) {
        this.ACCOUNTBILLPAY = ACCOUNTBILLPAY;
    }

    public double getSALARYPAY() {
        return SALARYPAY;
    }

    public void setSALARYPAY(double SALARYPAY) {
        this.SALARYPAY = SALARYPAY;
    }

    public double getTAXPAY() {
        return TAXPAY;
    }

    public void setTAXPAY(double TAXPAY) {
        this.TAXPAY = TAXPAY;
    }

    public double getTOTAL_OTHER_PAYABLE() {
        return TOTAL_OTHER_PAYABLE;
    }

    public void setTOTAL_OTHER_PAYABLE(double TOTAL_OTHER_PAYABLE) {
        this.TOTAL_OTHER_PAYABLE = TOTAL_OTHER_PAYABLE;
    }

    public double getINTERESTPAY() {
        return INTERESTPAY;
    }

    public void setINTERESTPAY(double INTERESTPAY) {
        this.INTERESTPAY = INTERESTPAY;
    }

    public double getDIVIDENDPAY() {
        return DIVIDENDPAY;
    }

    public void setDIVIDENDPAY(double DIVIDENDPAY) {
        this.DIVIDENDPAY = DIVIDENDPAY;
    }

    public double getOTHERPAY() {
        return OTHERPAY;
    }

    public void setOTHERPAY(double OTHERPAY) {
        this.OTHERPAY = OTHERPAY;
    }

    public double getNONLLIABONEYEAR() {
        return NONLLIABONEYEAR;
    }

    public void setNONLLIABONEYEAR(double NONLLIABONEYEAR) {
        this.NONLLIABONEYEAR = NONLLIABONEYEAR;
    }

    public double getSUMNONLLIAB() {
        return SUMNONLLIAB;
    }

    public void setSUMNONLLIAB(double SUMNONLLIAB) {
        this.SUMNONLLIAB = SUMNONLLIAB;
    }

    public double getLTACCOUNTPAY() {
        return LTACCOUNTPAY;
    }

    public void setLTACCOUNTPAY(double LTACCOUNTPAY) {
        this.LTACCOUNTPAY = LTACCOUNTPAY;
    }

    public double getSUMLIAB() {
        return SUMLIAB;
    }

    public void setSUMLIAB(double SUMLIAB) {
        this.SUMLIAB = SUMLIAB;
    }

    public double getSUMSHEQUITY() {
        return SUMSHEQUITY;
    }

    public void setSUMSHEQUITY(double SUMSHEQUITY) {
        this.SUMSHEQUITY = SUMSHEQUITY;
    }

    public double getSHARECAPITAL() {
        return SHARECAPITAL;
    }

    public void setSHARECAPITAL(double SHARECAPITAL) {
        this.SHARECAPITAL = SHARECAPITAL;
    }

    public double getCAPITALRESERVE() {
        return CAPITALRESERVE;
    }

    public void setCAPITALRESERVE(double CAPITALRESERVE) {
        this.CAPITALRESERVE = CAPITALRESERVE;
    }

    public double getSURPLUSRESERVE() {
        return SURPLUSRESERVE;
    }

    public double getGOODWILL() {
        return GOODWILL;
    }

    public void setGOODWILL(double GOODWILL) {
        this.GOODWILL = GOODWILL;
    }

    public void setSURPLUSRESERVE(double SURPLUSRESERVE) {
        this.SURPLUSRESERVE = SURPLUSRESERVE;
    }

    public double getRETAINEDEARNING() {
        return RETAINEDEARNING;
    }

    public void setRETAINEDEARNING(double RETAINEDEARNING) {
        this.RETAINEDEARNING = RETAINEDEARNING;
    }

    public double getSUMPARENTEQUITY() {
        return SUMPARENTEQUITY;
    }

    public void setSUMPARENTEQUITY(double SUMPARENTEQUITY) {
        this.SUMPARENTEQUITY = SUMPARENTEQUITY;
    }

    public double getSUMLIABSHEQUITY() {
        return SUMLIABSHEQUITY;
    }

    public void setSUMLIABSHEQUITY(double SUMLIABSHEQUITY) {
        this.SUMLIABSHEQUITY = SUMLIABSHEQUITY;
    }
}
