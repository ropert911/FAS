package com.xq.secser.ssbser.pojo.po;

import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author sk-qianxiao
 * @date 2019/12/31
 */
public interface IFund {
    void insertFundBatch(List<FoundPo> foundPoList);
    void delNameEpItem();
    void updateLevel(List<FoundLevelPo> foundLevelPoList);

    List<FoundPo> getByLevel(@Param("ft") String ft, @Param("level") long level);
}
