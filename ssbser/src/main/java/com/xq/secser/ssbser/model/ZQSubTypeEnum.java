package com.xq.secser.ssbser.model;

/**
 * @author sk-qianxiao
 * @date 2020/1/8
 */
public enum ZQSubTypeEnum {
    //长期纯债
    LC("041", "lc"),
    //短期纯债
    SC("042", "sc"),
    //混合债基
    HH("043", "hh"),
    //可转债
    KZ("045", "kz");

    private String code;
    private String subt;

    ZQSubTypeEnum(String code, String subt) {
        this.code = code;
        this.subt = subt;
    }

    public String getCode() {
        return code;
    }

    public String getSubt() {
        return subt;
    }

    public static String getDisStringBySubT(String subt) {
        String dis = "";
        switch (subt) {
            case "lc":
                dis = "长期纯债";
                break;
            case "sc":
                dis = "短期纯债";
                break;
            case "hh":
                dis = "混合债基";
                break;
            case "kz":
                dis = "可转债";
                break;
            default:
                break;
        }

        return dis;
    }
}
