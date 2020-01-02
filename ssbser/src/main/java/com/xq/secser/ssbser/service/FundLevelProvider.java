package com.xq.secser.ssbser.service;

import com.xq.secser.ssbser.pojo.po.FoundLevelPo;

import java.util.List;

/**
 * @author sk-qianxiao
 * @date 2020/1/2
 */
public interface FundLevelProvider {
    /**
     * 下载初始化foundlevel数据
     */
    void initFundLevelData();

    /**
     * 解析级别数据
     *
     * @return
     */
    List<FoundLevelPo> parseFundLevelData();
}
