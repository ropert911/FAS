package com.xq.secser.ssbser.pojo.po;

import java.util.List;

/**
 * @author sk-qianxiao
 * @date 2020/1/9
 */
public interface IFundFl {
    List<String> getExistCodeByCodeList(List<String> codelist);
    void batchInsert(List<FoundFlPo> foundFlPoList);
    List<FoundFlPo> getFlByCodeList(List<String> codelist);
}
