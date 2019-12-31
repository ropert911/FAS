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
public class FoundPo {
    private String code;
    private String name;
    private String ft;
    private String date;
    private Double l1y;
    private Double l2y;
    private Double l3y;
    private Double ty;
    private Double cy;
}
