package com.hgicreate.rno.ltestrucanlsservice.datasource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

@Configuration
@MapperScan(basePackages = "com.hgicreate.rno.ltestrucanlsservice.mapper.spark", sqlSessionFactoryRef = "sparkSqlSessionFactory")
public class SparkDataSourceConfig {
    @Bean
    @ConfigurationProperties(prefix = "mybatis.spark")
    public MybatisProperties sparkMybatisProperties() {
        return ConfigUtil.getMybatisProperties();
    }

    @Bean
    @ConfigurationProperties(prefix = "datasource.spark")
    public DataSource sparkDataSource() {
        return ConfigUtil.getDataSource();
    }

    @Bean
    public DataSourceTransactionManager sparkTransactionManager(@Qualifier("sparkDataSource") DataSource dataSource) {
        return ConfigUtil.getTransactionManager(dataSource);
    }

    @Bean
    public SqlSessionFactory sparkSqlSessionFactory(@Qualifier("sparkDataSource") DataSource dataSource, @Qualifier("sparkMybatisProperties") MybatisProperties mybatisProperties) throws Exception {
        return ConfigUtil.getSqlSessionFactory(dataSource, mybatisProperties);
    }
}