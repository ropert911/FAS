package com.xq.secser.secser.model;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

/**
 * @author sk-qianxiao
 * @date 2019/12/30
 */
@Data
@Builder
@ToString
public class PageInfo {
    long pageNum;
    long pageIndex;
    long allPages;
    long allRecords;
    long allNum;
    long gpNum;
    long hhNum;
    long zqNum;
    long zsNum;
    long bbNum;
    long qdiiNum;
    long etfNum;
    long lofNum;
    long fofNum;
}
