package com.xq.secser.provider;

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
     * 解析基金公司
     *
     * @return
     */
    List<CompPo> parseCompany();
}
