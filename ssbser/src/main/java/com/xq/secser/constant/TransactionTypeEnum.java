package com.xq.secser.constant;

/**
 * @author sk-qianxiao
 * @date 2020/1/9
 */
public enum TransactionTypeEnum {
    //申购
    SG(1, 0.005, 0.08, 0.08, 0.15,0.15),
    //运作
    YZ(2, 0.3, 0.4, 0.8, 1.75,1.75),
    //赎回
    SH(3, 0.001, 0.2, 0.25, 0.5,0.5);
    int code;
    //货币
    double hbfl;
    //纯债
    double zqlsfl;
    //其它债
    double zqfl;
    //股票混合
    double gphhfl;
    //qd2
    double qd2fl;

    TransactionTypeEnum(int code, Double hbfl, Double zqlsfl, Double zqfl, Double gphhfl, Double qd2fl) {
        this.code = code;
        this.hbfl = hbfl;
        this.zqlsfl = zqlsfl;
        this.zqfl = zqfl;
        this.gphhfl = gphhfl;
        this.qd2fl = qd2fl;
    }

    public static boolean isFlReasonable(String ft, String subt, TransactionTypeEnum action, double fl) {
        boolean isReasonable = true;
        switch (ft) {
            case "hb": {
                isReasonable = (fl <= action.hbfl);
            }
            break;
            case "zq":
                if ("lc".equals(subt) || "sc".equals(subt)) {
                    isReasonable = (fl <= action.zqlsfl);
                } else {
                    isReasonable = (fl <= action.zqfl);
                }
                break;
            case "gp":
            case "hh": {
                isReasonable = (fl <= action.gphhfl);
            }
            break;
            case "qdii": {
                isReasonable = (fl <= action.qd2fl);
            }
            break;
            default:
                break;
        }

        return isReasonable;
    }
}
