package com.xq.fin.analyser.model.repository;

import com.xq.fin.analyser.model.po.BaseInfoPo;
import com.xq.fin.analyser.model.po.BfbPo;
import com.xq.fin.analyser.model.po.CodeTimeKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author sk-qianxiao
 * @date 2020/9/9
 */
@Repository
public interface BaseInfoRepository extends JpaRepository<BaseInfoPo, String>, JpaSpecificationExecutor {
    //根据code查找
    List<BaseInfoPo> findByCode(String code);
}
