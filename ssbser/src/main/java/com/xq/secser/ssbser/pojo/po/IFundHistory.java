package com.xq.secser.ssbser.pojo.po;

import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author sk-qianxiao
 * @date 2019/12/31
 */
public interface IFundHistory {
    /**
     * 插入基金季度数据
     *
     * @param fundQuarterPoList
     */
    void insertFundQuarterBatch(List<FundQuarterPo> fundQuarterPoList);


    /**
     * 插入基金年度数据
     *
     * @param fundYearPoList
     */
    void insertFundYearBatch(List<FundYearPo> fundYearPoList);

    /**
     * 查看不存在的id
     *
     * @return
     */
    List<String> getExistCodes(List<String> ids);

    /**
     * 根据id获取所有记录
     * @param codeList
     * @return
     */
    List<FundQuarterPo> getQuarterDataByCode(List<String> codeList);

    /**
     * 根据 id获取看所有记录
     * @param codeList
     * @return
     */
    List<FundYearPo> getYearDataByCode(List<String> codeList);
}
