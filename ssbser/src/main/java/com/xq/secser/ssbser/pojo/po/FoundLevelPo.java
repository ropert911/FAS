package com.xq.secser.ssbser.pojo.po;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

/**
 * @author sk-qianxiao
 * @date 2020/1/2
 */
@Data
@Builder
@ToString
public class FoundLevelPo {
    /**
     * 基金代码
     */
    private String code;
    /**
     * 公司代码
     */
    private String comcode;
    /**
     * 基金级别
     */
    private Double level;
}
