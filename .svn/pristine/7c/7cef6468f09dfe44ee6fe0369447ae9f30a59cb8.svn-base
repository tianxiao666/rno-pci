package com.hgicreate.rno;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ImportResource;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.hgicreate.rno.properties.RnoProperties;

@SpringBootApplication
@EnableConfigurationProperties({ RnoProperties.class })
@EnableScheduling
@ImportResource("classpath:hadoop-config.xml")
public class Rno4GAzimuthCalcApplication {

	public static void main(String[] args) {
//		System.setProperty("HADOOP_USER_NAME", new Configuration().get("hadoop.user.name"));
		SpringApplication.run(Rno4GAzimuthCalcApplication.class, args);
	}
}
