package com.xq.secser.ssbser.pojo.po;

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
public class FoundPo {
    public FoundPo(String code, String comcode, String name, String ft, Date date, Double level, Double l1y, Double l2y, Double l3y, Double ty, Double cy) {
        this.code = code;
        this.comcode = comcode;
        this.name = name;
        this.ft = ft;
        this.date = date;
        this.level = level;
        this.l1y = l1y;
        this.l2y = l2y;
        this.l3y = l3y;
        this.ty = ty;
        this.cy = cy;
    }

    /**
     * 代号
     */
    private String code;
    /**
     * 公司代码
     */
    private String comcode;
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
    private Date date;

    /**
     * 级别
     */
    private Double level;
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
