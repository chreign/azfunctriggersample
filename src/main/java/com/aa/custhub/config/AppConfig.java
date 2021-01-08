package com.aa.custhub.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;

@Configuration
@PropertySources(value = {@PropertySource(ignoreResourceNotFound = true, value = "classpath:/application.properties")})
public class AppConfig {
    @Value("${testEnv}")
    private String value;
    @Value("${testSecret}")
    private String secret;

    @Autowired
    private Environment env;

    @Bean
    public void test() {
        System.out.println("spring triggered");
        System.out.println("env testEnv: " + env.getProperty("testEnv"));
        System.out.println("env testSecret: " + env.getProperty("testSecret"));
        System.out.println("value: " + value);
        System.out.println("secret: " + secret);
    }
}
