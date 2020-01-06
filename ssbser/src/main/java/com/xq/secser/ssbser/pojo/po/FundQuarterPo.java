package com.xq.secser.ssbser.pojo.po;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

/**
 * @author xq
 * @data 2020/1/7
 **/
@Data
@Builder
@ToString
public class FundQuarterPo {
    public FundQuarterPo(String code, String quarter, Double rank, Double rise) {
        this.code = code;
        this.quarter = quarter;
        this.rank = rank;
        this.rise = rise;
    }

    private String code;
    private String quarter;
    private Double rank;
    private Double rise;
}
