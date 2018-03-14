package com.hgicreate.rno.config;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import com.hgicreate.rno.mapper.oracle.OracleMapper;

@Configuration
public class OracleDataSourceConfig {
	@Bean(name = "oracleMybatisProperties")
	@Primary
	@ConfigurationProperties(prefix = "mybatis.oracle")
	public MybatisProperties oracleMybatisProperties() {
		return new MybatisProperties();
	}

	@Bean(name = "oracleDataSource")
	@Primary
	@ConfigurationProperties(prefix = "datasource.oracle")
	public DataSource oracleDataSource() {
		return ConfigUtil.getDataSource();
	}

	@Bean(name = "oracleTransactionManager")
	@Primary
	public DataSourceTransactionManager oracleTransactionManager(@Qualifier("oracleDataSource") DataSource dataSource) {
		return ConfigUtil.getTransactionManager(dataSource);
	}

	@Bean(name = "oracleSqlSessionFactory")
	@Primary
	public SqlSessionFactory oracleSqlSessionFactory(@Qualifier("oracleDataSource") DataSource dataSource,
			@Qualifier("oracleMybatisProperties") MybatisProperties mybatisProperties) throws Exception {
		return ConfigUtil.getSqlSessionFactory(dataSource, mybatisProperties);
	}

	@Bean(name = "oracleSqlSessionTemplate")
	@Primary
	public SqlSessionTemplate oracleSqlSessionTemplate(
			@Qualifier("oracleSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
		return ConfigUtil.getSqlSessionTemplate(sqlSessionFactory);
	}

	@Bean
	@Primary
	public OracleMapper oracleMapper(@Qualifier("oracleSqlSessionTemplate") SqlSessionTemplate sqlSessionTemplate)
			throws Exception {
		return sqlSessionTemplate.getMapper(OracleMapper.class);
	}
}