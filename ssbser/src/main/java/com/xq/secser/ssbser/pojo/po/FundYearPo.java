package com.xq.secser.ssbser.pojo.po;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

/**
 * @author sk-qianxiao
 * @date 2020/1/7
 */
@Data
@Builder
@ToString
public class FundYearPo {
    public FundYearPo(String code, String year, Double rank, Double rise) {
        this.code = code;
        this.year = year;
        this.rank = rank;
        this.rise = rise;
    }

    private String code;
    private String year;
    private Double rank;
    private Double rise;
}
