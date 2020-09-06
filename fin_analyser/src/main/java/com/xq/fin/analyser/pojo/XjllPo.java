package com.xq.fin.analyser.pojo;

public class XjllPo {
    private String SECURITYCODE;  //代码
    private String REPORTDATE; //时间
    double SALEGOODSSERVICEREC;         //销售商品、提供劳务收到的现金
    double OTHEROPERATEREC;             //收到其他与经营活动有关的现金
    double SUMOPERATEFLOWIN;        //经营活动现金流入小计
    double BUYGOODSSERVICEPAY;          //购买商品、接受劳务支付的现金
    double EMPLOYEEPAY;                 //支付给职工以及为职工支付的现金
    double TAXPAY;                      //支付的各项税费
    double OTHEROPERATEPAY;             //支付其他与经营活动有关的现金
    double SUMOPERATEFLOWOUT;       //经营活动现金流出小计;
    double NETOPERATECASHFLOW;      //经营活动产生的现金流量净额

    double DISPFILASSETREC;             //处置固定资产、无形资产和其他长期资产收回的现金净额
    double SUMINVFLOWIN;            //投资活动现金流入小计
    double BUYFILASSETPAY;              //购建固定资产、无形资产和其他长期资产支付的现金
    double INVPAY;                      //投资支付的现金
    double SUMINVFLOWOUT;           //投资活动现金流出小计
    double NETINVCASHFLOW;          //投资活动产生的现金流量净额

    double ACCEPTINVREC;                //吸收投资收到的现金
    double LOANREC;                     //取得借款收到的现金
    double SUMFINAFLOWIN;           //筹资活动现金流入小计
    double REPAYDEBTPAY;                //偿还债务支付的现金
    double DIVIPROFITORINTPAY;          //分配股利、利润或偿付利息支付的现金
    double OTHERFINAPAY;                //支付其他与筹资活动有关的现金
    double SUMFINAFLOWOUT;          //筹资活动现金流出小计
    double NETFINACASHFLOW;         //筹资活动产生的现金流量净额

    double NICASHEQUI;              //现金及现金等价物净增加额
    double CASHEQUIBEGINNING;       //加:期初现金及现金等价物余额
    double CASHEQUIENDING;          //期末现金及现金等价物余额

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

    public double getSALEGOODSSERVICEREC() {
        return SALEGOODSSERVICEREC;
    }

    public void setSALEGOODSSERVICEREC(double SALEGOODSSERVICEREC) {
        this.SALEGOODSSERVICEREC = SALEGOODSSERVICEREC;
    }

    public double getOTHEROPERATEREC() {
        return OTHEROPERATEREC;
    }

    public void setOTHEROPERATEREC(double OTHEROPERATEREC) {
        this.OTHEROPERATEREC = OTHEROPERATEREC;
    }

    public double getSUMOPERATEFLOWIN() {
        return SUMOPERATEFLOWIN;
    }

    public void setSUMOPERATEFLOWIN(double SUMOPERATEFLOWIN) {
        this.SUMOPERATEFLOWIN = SUMOPERATEFLOWIN;
    }

    public double getBUYGOODSSERVICEPAY() {
        return BUYGOODSSERVICEPAY;
    }

    public void setBUYGOODSSERVICEPAY(double BUYGOODSSERVICEPAY) {
        this.BUYGOODSSERVICEPAY = BUYGOODSSERVICEPAY;
    }

    public double getEMPLOYEEPAY() {
        return EMPLOYEEPAY;
    }

    public void setEMPLOYEEPAY(double EMPLOYEEPAY) {
        this.EMPLOYEEPAY = EMPLOYEEPAY;
    }

    public double getTAXPAY() {
        return TAXPAY;
    }

    public void setTAXPAY(double TAXPAY) {
        this.TAXPAY = TAXPAY;
    }

    public double getOTHEROPERATEPAY() {
        return OTHEROPERATEPAY;
    }

    public void setOTHEROPERATEPAY(double OTHEROPERATEPAY) {
        this.OTHEROPERATEPAY = OTHEROPERATEPAY;
    }

    public double getSUMOPERATEFLOWOUT() {
        return SUMOPERATEFLOWOUT;
    }

    public void setSUMOPERATEFLOWOUT(double SUMOPERATEFLOWOUT) {
        this.SUMOPERATEFLOWOUT = SUMOPERATEFLOWOUT;
    }

    public double getNETOPERATECASHFLOW() {
        return NETOPERATECASHFLOW;
    }

    public void setNETOPERATECASHFLOW(double NETOPERATECASHFLOW) {
        this.NETOPERATECASHFLOW = NETOPERATECASHFLOW;
    }

    public double getDISPFILASSETREC() {
        return DISPFILASSETREC;
    }

    public void setDISPFILASSETREC(double DISPFILASSETREC) {
        this.DISPFILASSETREC = DISPFILASSETREC;
    }

    public double getSUMINVFLOWIN() {
        return SUMINVFLOWIN;
    }

    public void setSUMINVFLOWIN(double SUMINVFLOWIN) {
        this.SUMINVFLOWIN = SUMINVFLOWIN;
    }

    public double getBUYFILASSETPAY() {
        return BUYFILASSETPAY;
    }

    public void setBUYFILASSETPAY(double BUYFILASSETPAY) {
        this.BUYFILASSETPAY = BUYFILASSETPAY;
    }

    public double getINVPAY() {
        return INVPAY;
    }

    public void setINVPAY(double INVPAY) {
        this.INVPAY = INVPAY;
    }

    public double getSUMINVFLOWOUT() {
        return SUMINVFLOWOUT;
    }

    public void setSUMINVFLOWOUT(double SUMINVFLOWOUT) {
        this.SUMINVFLOWOUT = SUMINVFLOWOUT;
    }

    public double getNETINVCASHFLOW() {
        return NETINVCASHFLOW;
    }

    public void setNETINVCASHFLOW(double NETINVCASHFLOW) {
        this.NETINVCASHFLOW = NETINVCASHFLOW;
    }

    public double getACCEPTINVREC() {
        return ACCEPTINVREC;
    }

    public void setACCEPTINVREC(double ACCEPTINVREC) {
        this.ACCEPTINVREC = ACCEPTINVREC;
    }

    public double getLOANREC() {
        return LOANREC;
    }

    public void setLOANREC(double LOANREC) {
        this.LOANREC = LOANREC;
    }

    public double getSUMFINAFLOWIN() {
        return SUMFINAFLOWIN;
    }

    public void setSUMFINAFLOWIN(double SUMFINAFLOWIN) {
        this.SUMFINAFLOWIN = SUMFINAFLOWIN;
    }

    public double getREPAYDEBTPAY() {
        return REPAYDEBTPAY;
    }

    public void setREPAYDEBTPAY(double REPAYDEBTPAY) {
        this.REPAYDEBTPAY = REPAYDEBTPAY;
    }

    public double getDIVIPROFITORINTPAY() {
        return DIVIPROFITORINTPAY;
    }

    public void setDIVIPROFITORINTPAY(double DIVIPROFITORINTPAY) {
        this.DIVIPROFITORINTPAY = DIVIPROFITORINTPAY;
    }

    public double getOTHERFINAPAY() {
        return OTHERFINAPAY;
    }

    public void setOTHERFINAPAY(double OTHERFINAPAY) {
        this.OTHERFINAPAY = OTHERFINAPAY;
    }

    public double getSUMFINAFLOWOUT() {
        return SUMFINAFLOWOUT;
    }

    public void setSUMFINAFLOWOUT(double SUMFINAFLOWOUT) {
        this.SUMFINAFLOWOUT = SUMFINAFLOWOUT;
    }

    public double getNETFINACASHFLOW() {
        return NETFINACASHFLOW;
    }

    public void setNETFINACASHFLOW(double NETFINACASHFLOW) {
        this.NETFINACASHFLOW = NETFINACASHFLOW;
    }

    public double getNICASHEQUI() {
        return NICASHEQUI;
    }

    public void setNICASHEQUI(double NICASHEQUI) {
        this.NICASHEQUI = NICASHEQUI;
    }

    public double getCASHEQUIBEGINNING() {
        return CASHEQUIBEGINNING;
    }

    public void setCASHEQUIBEGINNING(double CASHEQUIBEGINNING) {
        this.CASHEQUIBEGINNING = CASHEQUIBEGINNING;
    }

    public double getCASHEQUIENDING() {
        return CASHEQUIENDING;
    }

    public void setCASHEQUIENDING(double CASHEQUIENDING) {
        this.CASHEQUIENDING = CASHEQUIENDING;
    }
}
