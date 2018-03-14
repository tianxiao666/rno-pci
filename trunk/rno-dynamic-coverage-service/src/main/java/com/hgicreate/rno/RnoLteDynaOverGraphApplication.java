package com.hgicreate.rno;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ImportResource;

import com.hgicreate.rno.properties.RnoProperties;

@SpringBootApplication
@EnableConfigurationProperties({ RnoProperties.class })
@ImportResource("classpath:hadoop-config.xml")
public class RnoLteDynaOverGraphApplication {

	public static void main(String[] args) {
		SpringApplication.run(RnoLteDynaOverGraphApplication.class, args);
	}
}
