package com.example.integration;

import com.example.integration.config.ConfigurationManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * Application main.
 *
 * @author Anatolii Hamza
 */
@SpringBootApplication
@EnableConfigurationProperties(ConfigurationManager.class)
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}