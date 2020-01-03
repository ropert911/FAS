package com.xq.secser.ssbser.pojo.po;

import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author sk-qianxiao
 * @date 2019/12/31
 */
public interface IComp {
    /**
     * 批量插入公司信息
     *
     * @param compPoList
     */
    void insertCompBatch(List<CompPo> compPoList);

    /**
     * 获取排名在前N名的某一类基金公司
     *
     * @param ft
     * @param limit
     * @return
     */
    List<CompPo> getTopN(@Param("ft") String ft, @Param("limit") int limit);
}
