package com.xq.secser.secser.pojo.po;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

/**
 * @author sk-qianxiao
 * @date 2019/12/31
 */
@Data
@Builder
@ToString
public class CompPo {
    /**
     * 排序
     */
    private Long index;
    /**
     * 公司名
     */
    private String name;
    /**
     * 在什么类型中的排序
     */
    private String ft;
    /**
     * 成立时间
     */
    private String estime;
    /**
     * 规模：亿元
     */
    private Double scale;
    /**
     * 基金数
     */
    private Long fnnum;
    /**
     * 经理数
     */
    private Long managernum;
}
