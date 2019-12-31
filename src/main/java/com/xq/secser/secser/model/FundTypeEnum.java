package com.xq.secser.secser.model;

/**
 * @author sk-qianxiao
 * @date 2019/12/30
 */
public enum FundTypeEnum {
    GP(1, "gp",25),
    HH(2, "hh",27),
    ZQ(3, "zq",31);

    private int code;
    private String urlParam;
    private long icode;

    FundTypeEnum(int code, String urlParam, long icode) {
        this.code = code;
        this.urlParam = urlParam;
        this.icode=icode;
    }

    public int getCode() {
        return code;
    }

    public String getUrlParam() {
        return urlParam;
    }

    public long getIcode() {
        return icode;
    }
}
