package com.xq.secser.ssbser.pojo.po;

import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author sk-qianxiao
 * @date 2019/12/31
 */
public interface IFund {
    /**
     * 插入基金
     *
     * @param foundPoList
     */
    void insertFundBatch(List<FoundPo> foundPoList);

    /**
     * 根据类型和级别获取相关基金
     *
     * @param ft
     * @param level
     * @return
     */
    List<FoundPo> getByLevel(@Param("ft") String ft, @Param("level") long level);
}
