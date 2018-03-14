package com.hgicreate.rno.lte.pciafp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author chen.c10
 */
@SpringBootApplication
@EnableScheduling
@EnableFeignClients
@EnableDiscoveryClient
@EnableCircuitBreaker
public class PciAfpServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(PciAfpServiceApplication.class, args);
    }
}
