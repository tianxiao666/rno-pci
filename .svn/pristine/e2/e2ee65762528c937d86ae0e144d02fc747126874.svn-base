package com.hgicreate.rno.ltestrucanlsservice.datasource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;

/**
 * Created by chen.c10 on 2016/11/22.
 * 配置工具
 */
class ConfigUtil {

    static MybatisProperties getMybatisProperties() {
        return new MybatisProperties();
    }

    static DataSource getDataSource() {
        return DataSourceBuilder.create().build();
    }

    static DataSourceTransactionManager getTransactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    static SqlSessionFactory getSqlSessionFactory(DataSource dataSource, MybatisProperties properties) throws Exception {
        final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        sessionFactory.setConfiguration(properties.getConfiguration());
        if (StringUtils.hasLength(properties.getTypeAliasesPackage())) {
            sessionFactory.setTypeAliasesPackage(properties.getTypeAliasesPackage());
        }
        if (StringUtils.hasLength(properties.getTypeHandlersPackage())) {
            sessionFactory.setTypeHandlersPackage(properties.getTypeHandlersPackage());
        }
        if (!ObjectUtils.isEmpty(properties.resolveMapperLocations())) {
            sessionFactory.setMapperLocations(properties.resolveMapperLocations());
        }
        return sessionFactory.getObject();
    }
}
