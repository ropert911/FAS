package com.xq.secser.constant;

/**
 * @author xq
 * @data 2020/1/1
 **/
public enum ComTypEnum {
    ZS(11, "zs", "指数型"),
    GP(25, "gp", "股票型"),
    HH(27, "hh", "混合型"),
    ZQ(31, "zq", "债券型"),
    HB(35, "hb", "货币型"),
    LC(36, "lc", "理财型"),
    QDII(37, "dq2", "QDII"),
    BB(38, "bb", "保本型");
    private long code;
    private String abb;
    private String commen;

    ComTypEnum(long code, String abb, String commen) {
        this.code = code;
        this.abb = abb;
        this.commen = commen;
    }

    public long getCode() {
        return code;
    }

    public String getAbb() {
        return abb;
    }

    public String getCommen() {
        return commen;
    }
}
