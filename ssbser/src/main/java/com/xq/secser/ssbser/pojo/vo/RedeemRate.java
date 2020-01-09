package com.xq.secser.ssbser.pojo.vo;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

/**
 * @author sk-qianxiao
 * @date 2020/1/9
 */
@Data
@Builder
@ToString
public class RedeemRate {
    /**
     * 资产模式
     */
    private Double zcgm;
    //申购
    private Double sgfl;
    //动作费率
    private Double yzfl;
    //管理费率
    private Double managefl;
    //托管费率
    private Double tgfl;
    //赎回
    private String c1;
    private Double fl;
    private String c2;
    private Double f2;
    private String c3;
    private Double f3;
    private String c4;
    private Double f4;
}
