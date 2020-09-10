package com.xq.fin.analyser.model.po;

import lombok.Data;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author sk-qianxiao
 * @date 2020/9/9
 */
@Entity
@ToString
@Data
@Table(name = "gp_info")
public class BaseInfoPo {
    @Id
    private String code;
    private String name;
}
