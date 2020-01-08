package com.xq.secser.ssbser.model;

/**
 * @author sk-qianxiao
 * @date 2020/1/8
 */
public enum ZQSubTypeEnum {
    LC("041", "lc", "长期纯债"),
    SC("042", "sc", "短期纯债"),
    HH("043", "hh", "混合债基"),
    KZ("045", "kz", "可转债");

    private String code;
    private String subt;
    private String dis;

    ZQSubTypeEnum(String code, String subt, String dis) {
        this.code = code;
        this.subt = subt;
        this.dis = dis;
    }

    public String getCode() {
        return code;
    }

    public String getSubt() {
        return subt;
    }

    public String getDis() {
        return dis;
    }

    public static String getDisStringBySubT(String subt) {
        for (ZQSubTypeEnum st : values()) {
            if (st.subt.equals(subt)) {
                return st.dis;
            }
        }

        return "";
    }
}
