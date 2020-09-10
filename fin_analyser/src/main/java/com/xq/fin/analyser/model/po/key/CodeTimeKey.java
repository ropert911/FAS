package com.xq.fin.analyser.model.po.key;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author sk-qianxiao
 * @date 2020/9/9
 */
@ToString
@Data
public class CodeTimeKey implements Serializable {
    //代码
    private String code;
    //时间：202003，202006
    private String time;
}
