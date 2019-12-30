package com.xq.secser.secser.model;

/**
 * @author sk-qianxiao
 * @date 2019/12/30
 */
public enum FundTypeEnum {
    GP(1, "gp"),
    HH(2, "hh"),
    ZQ(3, "zq");

    private int code;
    private String urlParam;

    FundTypeEnum(int code, String urlParam) {
        this.code = code;
        this.urlParam = urlParam;
    }

    public int getCode() {
        return code;
    }

    public String getUrlParam() {
        return urlParam;
    }
}
