package com.xq.secser.ssbser.pojo.po;

import java.util.List;

/**
 * @author sk-qianxiao
 * @date 2019/12/31
 */
public interface IComp {
    void insertCompBatch(List<CompPo> compPoList);
    List<CompPo> getTopN(/*String ft, int limit*/);
}
