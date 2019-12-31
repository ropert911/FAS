package com.xq.secser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SecserApplication {
    private static Logger logger = LoggerFactory.getLogger(SecserApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(SecserApplication.class, args);
    }
}
