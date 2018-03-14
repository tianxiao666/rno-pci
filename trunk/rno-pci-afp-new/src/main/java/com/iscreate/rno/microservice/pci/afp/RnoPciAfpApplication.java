package com.iscreate.rno.microservice.pci.afp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.iscreate.rno.microservice.pci.afp.properties.RnoProperties;

@SpringBootApplication
@EnableConfigurationProperties({ RnoProperties.class })
@EnableScheduling
public class RnoPciAfpApplication {

	public static void main(String[] args) {
		SpringApplication.run(RnoPciAfpApplication.class, args);
	}
	
	@Bean
	public EmbeddedServletContainerCustomizer containerCustomizer(){
	       return new EmbeddedServletContainerCustomizer() {
	           @Override
	           public void customize(ConfigurableEmbeddedServletContainer container) {
	                container.setSessionTimeout(1800);//单位为S
	          }
	    };
	}

}
