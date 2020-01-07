package com.xq.secser.provider;

import com.xq.secser.ssbser.pojo.po.FundQuarterPo;

import java.util.List;

/**
 * @author sk-qianxiao
 * @date 2020/1/7
 */
public interface FundHisProvider {
    List<FundQuarterPo> downLoadFoudQuartData(List<String> notexistCodeList);
}
