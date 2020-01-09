package com.xq.secser.constant;

/**
 * @author sk-qianxiao
 * @date 2020/1/9
 */
public enum TransactionTypeEnum {
    SG(1, 0.08, 0.08, 0.15),
    YZ(2, 0.4, 0.8, 1.75),
    SH(3, 0.2, 0.25, 0.5);
    int code;
    //纯债
    double zqlsfl;
    //其它债
    double zqfl;
    //股票混合
    double gphhfl;

    TransactionTypeEnum(int code, Double zqlsfl, Double zqfl, Double gphhfl) {
        this.code = code;
        this.zqlsfl = zqlsfl;
        this.zqfl = zqfl;
        this.gphhfl = gphhfl;
    }

    public static boolean isFlReasonable(String ft, String subt, TransactionTypeEnum action, double fl) {
        boolean isReasonable = true;
        switch (ft) {
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
            default:
                break;
        }

        return isReasonable;
    }
}
