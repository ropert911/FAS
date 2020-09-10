package com.xq.fin.analyser.model.repository;

import com.xq.fin.analyser.model.po.key.CodeTimeKey;
import com.xq.fin.analyser.model.po.ZyzbPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author sk-qianxiao
 * @date 2020/9/9
 */
@Repository
public interface ZyzbRepository extends JpaRepository<ZyzbPo, CodeTimeKey>, JpaSpecificationExecutor {
    //根据code查找
    List<ZyzbPo> findByCode(String code);
}
