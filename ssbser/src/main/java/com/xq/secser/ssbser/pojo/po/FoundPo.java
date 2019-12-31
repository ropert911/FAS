package com.xq.secser.ssbser.pojo.po;

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
public class FoundPo {
    /**
     * 代号
     */
    private String code;
    /**
     * 名称
     */
    private String name;
    /**
     * 类型
     */
    private String ft;
    /**
     * 更新时间
     */
    private String date;
    /**
     * 近1年
     */
    private Double l1y;
    /**
     * 近2年
     */
    private Double l2y;
    /**
     * 近3年
     */
    private Double l3y;
    /**
     * 今年
     */
    private Double ty;
    /**
     * 成立以来
     */
    private Double cy;
}
