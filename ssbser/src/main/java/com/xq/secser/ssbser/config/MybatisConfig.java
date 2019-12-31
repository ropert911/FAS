package com.xq.secser.ssbser.config;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.Reader;

/**
 * @author sk-qianxiao
 * @date 2019/12/31
 */
@Configuration
public class MybatisConfig {


    @Bean
    SqlSessionFactory sqlSessionFactory() throws Exception {
        Reader reader = Resources.getResourceAsReader("mybatis/Configure.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);

        return sqlSessionFactory;
    }

}
