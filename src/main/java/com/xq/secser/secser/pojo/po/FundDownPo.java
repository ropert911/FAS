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
public class FundDownPo {
    private String code;
    private String ft;
    private String info;
}
