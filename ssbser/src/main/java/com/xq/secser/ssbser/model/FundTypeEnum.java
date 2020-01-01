package com.xq.secser.ssbser.model;

/**
 * @author sk-qianxiao
 * @date 2019/12/30
 */
public enum FundTypeEnum {
    BB("bb"),
    GP("gp"),
    HH("hh"),
    ZQ("zq"),
    ZS("zs"),
    QDII("qdii"),
    LOF("lof"),
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
