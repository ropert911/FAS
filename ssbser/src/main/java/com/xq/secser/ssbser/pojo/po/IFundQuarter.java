package com.xq.secser.ssbser.pojo.po;

import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author sk-qianxiao
 * @date 2019/12/31
 */
public interface IFundQuarter {
    /**
     * 插入基金季度数据
     *
     * @param fundQuarterPoList
     */
    void insertFundQuarterBatch(List<FundQuarterPo> fundQuarterPoList);

    /**
     * 查看不存在的id
     * @return
     */
    List<String> getExistCodes(List<String> ids);

    /**
     * 根据id获取所有记录
     * @return
     */
    List<FundQuarterPo> getQuarterDataByCode(List<String> codeList);
}
