package com.xq.secser.provider;

import com.xq.secser.ssbser.pojo.po.FoundPo;
import com.xq.secser.ssbser.pojo.vo.RedeemRate;

import java.util.List;

/**
 * @author sk-qianxiao
 * @date 2019/12/31
 */
public interface FundProvider {
    /**
     * 下载初始化found数据
     */
    void initFoundData();

    /**
     * 解析得到found详细信息
     *
     * @return
     */
    List<FoundPo> parseFund();
    /**
     * 解析得到hbfound详细信息
     *
     * @return
     */
    List<FoundPo> parseHbFund();

    /**
     * 下载货币found数据
     */
    void initHbFoundData();

    /**
     *
     * @param ccode
     * @return
     */
    RedeemRate getflinfo(String ccode);
}
