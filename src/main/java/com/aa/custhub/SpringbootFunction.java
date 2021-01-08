package com.aa.custhub;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.function.Function;


@SpringBootApplication
public class SpringbootFunction {
    private static final Logger logger = LoggerFactory.getLogger(SpringbootFunction.class);

    @Value("${testEnv}")
    private String testVariable;

    public static void main(String[] args) {
        SpringApplication.run(SpringbootFunction.class, args);
    }

    // the method name has to be the same as function name
    @Bean
    public Function<String, Boolean> timer() {
        logger.info("this is triggered during init include below");
        logger.info("------not suppose to process the job here------");
        logger.info("this is triggered during init include above");
        return input -> {
            logger.info("------trigger the job here------");
            logger.info("testVariable value is: " + testVariable);
            return Boolean.TRUE;
        };
    }

    @Bean
    public Function<String, String> hello() {
        logger.info("this is triggered during init and http trigger in spring boot");
        return user -> "Hello, " + user;
    }
}
