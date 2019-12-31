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
    private Long index;
    private String name;
    private String estime;
    private Double scale;
    private Long fnnum;
    private Long managernum;
}
