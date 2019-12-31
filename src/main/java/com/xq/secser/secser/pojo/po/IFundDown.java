package com.xq.secser.secser.pojo.po;


import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author sk-qianxiao
 */
public interface IFundDown {
    @Select("select * from funddownload where code= #{code}")
    FundDownPo getInfoByCode(String code);
//
//    User getUser(int id);
//
//    List<User> getUserList();

    /**
     * 插入一个记录
     *
     * @param fundDownPo
     */
    void insertFundDown(FundDownPo fundDownPo);

    /**
     * 批量插入记录
     *
     * @param fundDownPoList
     */
    void insertFundBatch(List<FundDownPo> fundDownPoList);
//
//    void updateUser(User user);
//
//    void deleteUser(int userId);
}