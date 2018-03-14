package com.hgicreate.rno.ltestrucanlsservice.datasource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

@Configuration
@MapperScan(basePackages = "com.hgicreate.rno.ltestrucanlsservice.mapper.oracle", sqlSessionFactoryRef = "oracleSqlSessionFactory")
public class OracleDataSourceConfig {
    @Bean
    @Primary
    @ConfigurationProperties(prefix = "mybatis.oracle")
    public MybatisProperties oracleMybatisProperties() {
        return ConfigUtil.getMybatisProperties();
    }

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "datasource.oracle")
    public DataSource oracleDataSource() {
        return ConfigUtil.getDataSource();
    }

    @Bean
    @Primary
    public DataSourceTransactionManager oracleTransactionManager(@Qualifier("oracleDataSource") DataSource dataSource) {
        return ConfigUtil.getTransactionManager(dataSource);
    }

    @Bean
    @Primary
    public SqlSessionFactory oracleSqlSessionFactory(@Qualifier("oracleDataSource") DataSource dataSource,@Qualifier("oracleMybatisProperties") MybatisProperties mybatisProperties) throws Exception {
        return ConfigUtil.getSqlSessionFactory(dataSource, mybatisProperties);
    }
}