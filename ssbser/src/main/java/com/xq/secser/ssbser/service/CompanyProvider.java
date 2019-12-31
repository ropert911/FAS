package com.xq.secser.ssbser.service;

import com.xq.secser.ssbser.pojo.po.CompPo;

import java.util.List;

/**
 * @author sk-qianxiao
 * @date 2019/12/31
 */
public interface CompanyProvider {
    /**
     * 下载初始化
     */
    void initCompany();

    /**
     *
     */
    List<CompPo> parseCompany();
}
