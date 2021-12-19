package com.mgumieniak.architecture.webapp.clients;

import org.springframework.context.annotation.Configuration;
import reactivefeign.spring.config.EnableReactiveFeignClients;

@Configuration
@EnableReactiveFeignClients(basePackages = {
        "com.mgumieniak.architecture.connectors"
})
public class Config {
}
