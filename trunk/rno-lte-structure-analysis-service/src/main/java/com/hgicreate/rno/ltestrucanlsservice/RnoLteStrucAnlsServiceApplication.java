package com.hgicreate.rno.ltestrucanlsservice;

import com.hgicreate.rno.ltestrucanlsservice.properties.RnoProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableConfigurationProperties({RnoProperties.class})
@EnableScheduling
public class RnoLteStrucAnlsServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RnoLteStrucAnlsServiceApplication.class, args);
    }
}