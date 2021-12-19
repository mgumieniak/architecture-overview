package com.mgumieniak.architecture.webapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactivefeign.spring.config.EnableReactiveFeignClients;

@SpringBootApplication
public class ArchitectureWebappApplication {

    public static void main(String[] args) {
        SpringApplication.run(ArchitectureWebappApplication.class, args);
    }

}
