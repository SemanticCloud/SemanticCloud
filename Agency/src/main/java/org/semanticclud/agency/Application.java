package org.semanticclud.agency;

import org.semanticclud.agency.jade4spring.JadeBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class Application {
    @Autowired
    private JadeBean jadeBean;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}