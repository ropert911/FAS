package com.xq.secser.constant;

/**
 * @author sk-qianxiao
 * @date 2019/12/30
 */
public enum FundTypeEnum {
    GP("gp"),
    HH("hh"),
    ZQ("zq"),
    ZS("zs"),
    QDII("qdii"),
    FOF("fof");

    private String urlParam;
    private long icode;

    FundTypeEnum(String urlParam) {
        this.urlParam = urlParam;
    }

    public String getUrlParam() {
        return urlParam;
    }
}
