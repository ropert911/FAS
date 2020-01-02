package com.xq.secser.ssbser.pojo.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.sql.Date;

/**
 * @author sk-qianxiao
 * @date 2019/12/31
 */
@Data
@Builder
@ToString
public class CompPo {
    public CompPo(String comcode, String ft, Long ordernum, String name, Date estime, Double scale, Long fnnum, Long managernum) {
        this.comcode = comcode;
        this.ft = ft;
        this.ordernum = ordernum;
        this.name = name;
        this.estime = estime;
        this.scale = scale;
        this.fnnum = fnnum;
        this.managernum = managernum;
    }

    /**
     * 公司编码
     */
    private String comcode;
    /**
     * 在什么类型中的排序
     */
    private String ft;
    /**
     * 排序
     */
    private Long ordernum;
    /**
     * 公司名
     */
    private String name;

    /**
     * 成立时间
     */
    private Date estime;
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
