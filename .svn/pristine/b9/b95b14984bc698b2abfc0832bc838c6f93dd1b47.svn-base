package com.hgicreate.rno.config;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import com.hgicreate.rno.mapper.spark.SparkMapper;

@Configuration
public class SparkDataSourceConfig {
	@Bean(name = "sparkMybatisProperties")
	@ConfigurationProperties(prefix = "mybatis.spark")
	public MybatisProperties sparkMybatisProperties() {
		return new MybatisProperties();
	}

	@Bean(name = "sparkDataSource")
	@ConfigurationProperties(prefix = "datasource.spark")
	public DataSource sparkDataSource() {
		return ConfigUtil.getDataSource();
	}

	@Bean(name = "sparkTransactionManager")
	public DataSourceTransactionManager sparkTransactionManager(@Qualifier("sparkDataSource") DataSource dataSource) {
		return ConfigUtil.getTransactionManager(dataSource);
	}

	@Bean(name = "sparkSqlSessionFactory")
	public SqlSessionFactory sparkSqlSessionFactory(@Qualifier("sparkDataSource") DataSource dataSource,
			@Qualifier("sparkMybatisProperties") MybatisProperties mybatisProperties) throws Exception {
		return ConfigUtil.getSqlSessionFactory(dataSource, mybatisProperties);
	}

	@Bean(name = "sparkSqlSessionTemplate")
	public SqlSessionTemplate sparkSqlSessionTemplate(
			@Qualifier("sparkSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
		return ConfigUtil.getSqlSessionTemplate(sqlSessionFactory);
	}

	@Bean
	public SparkMapper sparkMapper(@Qualifier("sparkSqlSessionTemplate") SqlSessionTemplate sqlSessionTemplate)
			throws Exception {
		return sqlSessionTemplate.getMapper(SparkMapper.class);
	}
}