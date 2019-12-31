package com.xq.secser.ssbser.tt.pojo;


import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author sk-qianxiao
 */
public interface ITtFund {
    /**
     * 插入一个记录
     *
     * @param ttFundPo
     */
    void insertFundDown(TtFundPo ttFundPo);

    /**
     * 批量插入记录
     *
     * @param ttFundPoList
     */
    void insertFundBatch(List<TtFundPo> ttFundPoList);
//
//    void updateUser(User user);
//
//    void deleteUser(int userId);

    @Select("select * from ttfund where code= #{code}")
    TtFundPo getInfoByCode(String code);


    /**
     * 查询全部
     *
     * @return
     */
    List<TtFundPo> getAll();
}